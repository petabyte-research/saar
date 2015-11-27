/**
 * 
 */
package saar;

/**
 * @author quispell
 *
 */
import sim.engine.*;
import sim.util.*;
import ec.util.*;
import sim.field.continuous.*;
import sim.field.network.*;
import saar.agents.*;


public class Saar extends SimState
{
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Properties, constructors and main
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static final long serialVersionUID = 1L;
		
	private Continuous2D area ; 
	private Network friends;
	private int numCitizens;
	private String networkType; 
	private String riskManagerBehavior;
	private String mediaBehavior;
	private Double objectiveRisk;
	private int eventMemory;
	public ec.util.MersenneTwisterFast randomGenerator;
	public Census census;
	
	public Continuous2D getArea() { return area;} 
	public Network getFriends() { return friends;}
	public int getNumCitizens() { return numCitizens;}
	public void setNumCitizens(int numCitizens) { this.numCitizens = numCitizens; }
	public String getNetworkType() { return networkType; }
	public void setNetworkType(String networkType) { this.networkType = networkType; }
	public String getRiskManagerBehavior() { return riskManagerBehavior; }
	public void setRiskManagerBehavior(String riskManagerBehavior) { this.riskManagerBehavior = riskManagerBehavior; }
	public String getMediaBehavior() { return mediaBehavior; }
	public void setMediaBehavior(String mediaBehavior) { this.mediaBehavior = mediaBehavior; }
	public Double getObjectiveRisk() { return objectiveRisk; }
	public void setObjectiveRisk(Double objectiveRisk) { this.objectiveRisk = objectiveRisk; }
	public int getEventMemory() { return eventMemory; }
	public void setEventMemory(int eventMemory) { this.eventMemory = eventMemory; }
	public Double getAverageRiskPerception() { return census.getAverageRiskPerception() ; } 
	
	/**
	 * @param seed
	 */
	
	public Saar(long seed, String NetworkType, Double ObjectiveRisk, int NumCitizens, int EventMemory) {
		super(seed);
		area = new Continuous2D(1.0,100,100);
		randomGenerator = new MersenneTwisterFast();
		friends = new Network(false);
		networkType = NetworkType;
		objectiveRisk = ObjectiveRisk;
		numCitizens = NumCitizens;
		eventMemory = EventMemory;
	}
	
	/**
	 * @param args
	 */
	
	public static void main(String[] args) 
	{		
		// create the model
		String networkType = "Lattice";
		Double objectiveRisk = 0.0001;
		int numCitizens = 1000;
		int eventMemory = 10;
		SimState state = new Saar(System.currentTimeMillis(),networkType,objectiveRisk,numCitizens,eventMemory); // TODO: get paramters from commandline or configfile
		
		// start the model
		int jobs = 1; 
		int numSteps = 1000;
		state.nameThread();
		for(int job = 0; job < jobs; job++)
		{
			state.setJob(job);
			state.start();
			do
				if (!state.schedule.step(state)) break;
			while(state.schedule.getSteps() < numSteps);
			state.finish();
		}
		
		System.exit(0);

	}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Initializing and Starting methods
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	
	
	public void start()
	{
		super.start();
		
		// clear area
		area.clear();
		
		// add Census object for gathering statistics
		census = new Census();
		schedule.scheduleRepeating(census);
		
		// add citizens
		System.out.print("Creating agents: ");
		int xPos = 1;
		int yPos = 0;
		Double initialRisk = 0.0;
		Double lowerRiskBound = objectiveRisk * 0.95;
		Double riskInterval = objectiveRisk * 0.1;
		for(int i = 0; i < numCitizens; i++)
		{
			initialRisk = lowerRiskBound + randomGenerator.nextDouble() * riskInterval; 
			Citizen citizen = new Citizen(i, initialRisk); 
			
			// spread citizens over the area
			if ( xPos < 100 ) 
				xPos = xPos + 10;
			else {
				xPos = 0;
				yPos = yPos + 10;
			}
			area.setObjectLocation(citizen, new Double2D(xPos, yPos));	
			
			// add citizen to social network and schedule 
			friends.addNode(citizen);
			schedule.scheduleRepeating(citizen);
			
			System.out.print(i);
		}
		System.out.println("");
		
		// create edges in social network
		System.out.print("Creating Social Network: ");
		switch ( networkType ) 
		{
			case "Lattice":
				createNetworkLattice();
				break;
			case "WattsBeta":
				createNetworkWattsStrogatz(4, 0.5);
				break;
			default:
				System.out.println("None !!!");
				System.out.println("*** Warning: no social network has been set up.");
				break;
				
		}		
		
		System.out.println("Model Initialized.");
	}
	
	/**
	 * @param networkType
	 */
	
	public void createNetworkLattice()
	{
		System.out.println("Lattice");

		Bag citizens = new Bag(friends.getAllNodes()); // create copy to be sure the Bag doesn't change or gets garbage collected 

		for(int i = citizens.size() - 1 ; i > 0 ; i--)
		{
			Object citizen1 = citizens.get(i);
			Object citizen2 = citizens.get(i-1);
			friends.addEdge(citizen1, citizen2, 1.0);
		}
	}
	
	/**
	 * @param degree
	 * @param beta
	 */
	
	public void createNetworkWattsStrogatz(int degree, double beta) 
	{
		System.out.println("Watts beta");
		
		Bag citizens = new Bag(friends.getAllNodes()); // create copy to be sure the Bag doesn't change or gets garbage collected
		Bag neighbours = new Bag();
		
		for (int i = 0 ; i < citizens.size() ; i++  )
		{
			Object citizen = citizens.get(i);
			Double2D pos = area.getObjectLocation(citizen);
			
			// get degree neigbours
			neighbours = area.getNearestNeighbors(pos, degree, false, false, false, neighbours);
			
			// wire neighbours and/or random node		
			Object acquaintance = new Object();
			for ( int n = 0; n < neighbours.size() ; n++ )
			{
				Object neighbour = neighbours.get(n);
				if ( random.nextDouble() < beta ) 
				{
					// wire with random node with probability beta
					// TODO: this is not entirely correct; a node has to be randomly selected from a set without already wired nodes and neighbour
					do {
						int tmp = randomGenerator.nextInt(numCitizens);
						acquaintance = citizens.get(tmp);
					}
					while ( citizen == acquaintance || neighbour == acquaintance || friends.getEdge(citizen, acquaintance) == null );
					
					friends.addEdge(citizen,acquaintance,1.0);
				} 
				else
				{
					// otherwise, just wire neighbour (if not wired already) 
					if ( friends.getEdge(citizen, neighbour) == null )
						friends.addEdge(citizen,neighbour,1.0);
				}
			
			}
		}
		
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Auxiliary 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * 
	 * @param ID
	 * @return
	 */
	
	public Object getAgent(int ID)
	{
		// TODO: for speed, maybe create a separate map to look up agents by ID

		Bag individuals = friends.getAllNodes();
		for ( int i = 1; i < individuals.size() ; i++ )
		{
			if ( ((Citizen) individuals.get(i)).getAgentID() == ID )
				return individuals.get(i);
		}
		return null;
	}



}
