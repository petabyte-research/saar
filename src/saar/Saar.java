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
import sim.field.continuous.*;
import sim.field.network.*;
import saar.agents.*;


public class Saar extends SimState
{
		
	public Continuous2D area = new Continuous2D(1.0,100,100);
	public int numCitizens = 16;
	public String networkType; 
	public String riskManagerBehavior;
	public String mediaBehavior;
	public Double objectiveRisk;
	public int eventMemory;
	public Network friends = new Network(false);
	
	// properties
		
	public int getNumCitizens() {
		return numCitizens;
	}

	public void setNumCitizens(int numCitizens) {
		this.numCitizens = numCitizens;
	}

	public String getNetworkType() {
		return networkType;
	}

	public void setNetworkType(String networkType) {
		this.networkType = networkType;
	}

	public String getRiskManagerBehavior() {
		return riskManagerBehavior;
	}

	public void setRiskManagerBehavior(String riskManagerBehavior) {
		this.riskManagerBehavior = riskManagerBehavior;
	}

	public String getMediaBehavior() {
		return mediaBehavior;
	}

	public void setMediaBehavior(String mediaBehavior) {
		this.mediaBehavior = mediaBehavior;
	}

	public Double getObjectiveRisk() {
		return objectiveRisk;
	}

	public void setObjectiveRisk(Double objectiveRisk) {
		this.objectiveRisk = objectiveRisk;
	}

	public int getEventMemory() {
		return eventMemory;
	}

	public void setEventMemory(int eventMemory) {
		this.eventMemory = eventMemory;
	}
	
	/**
	 * @param seed
	 */
	
	public Saar(long seed) {
		super(seed);
	}

	/**
	 * 
	 */
	
	public void start()
	{
		super.start();
		
		// clear area
		area.clear();
		
		// add citizens
		int xPos = 1;
		int yPos = 0;
		
		System.out.print("Creating agents: ");
		for(int i = 0; i < numCitizens; i++)
		{
			Citizen citizen = new Citizen(i);
			
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
		}
		
		// create edges in social network
		System.out.println("");
		System.out.print("Creating Social Network: ");
		
		networkType = "Lattice";
		switch ( networkType ) 
		{
			case "Lattice":
				createNetworkLattice();
				break;
			case "WattsBeta":
				createNetworkWattsStrogatz(4, 0.5);
				break;
			default:
				// TODO: add default social network
				break;
				
		}		
		
		System.out.println("Model Started.");
	}
	
	/**
	 * @param networkType
	 */
	
	public void createNetworkLattice()
	{
		System.out.println("Lattice");

		Bag citizens = friends.getAllNodes();

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
						int tmp = random.nextInt(numCitizens);
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

	/**
	 * @param args
	 */
	
	public static void main(String[] args) 
	{		
		doLoop(Saar.class, args);
		System.exit(0);

	}

}
