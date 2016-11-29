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
import saar.ui.*;
import com.beust.jcommander.*;
import java.util.Collection;
import java.util.Iterator;
import edu.uci.ics.jung.graph.*;

public class Saar extends SimState
{
	

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Static constants
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final long serialVersionUID = 1L;
	
	// risk types
	public static final int NONE = 0;  // Dummy to allow for sending a sentiment vector as content[0] in Message
	public static final int FLOOD = 1;
	
	// auxiliary
	public static final int HIGHER = 0;
	public static final int EQUAL = 1;
	public static final int LOWER = 2;
		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties, constructors and main
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// model properties
	private Continuous2D area ; 
	public ec.util.MersenneTwisterFast randomGenerator;
	private SparseMultigraph<Agent, Connection> friendsNetwork;
	private SparseMultigraph<Agent, Connection> mediaNetwork;
	private Census census;
	private Medium medium;
	
	// configuration properties
	private int numCitizens;
	private int primaryRiskType;
	private Double wattsBeta; 
	private int connectedNeighbours;
	private String riskManagerBehavior;
	private String mediaBehavior;
	private String opinionDynamic;
	private DoubleBag objectiveRisks;
	private int eventMemory;
	private Double confidence;
	private String logFile;
	private int verbosity;
		
	public Continuous2D getArea() { return area;} 
	public SparseMultigraph<Agent, Connection> getFriends() { return friendsNetwork;}
	public Census getCensus() { return census; }
	public Medium getMedium() { return medium;}
	
	public int getNumCitizens() { return numCitizens;}
	public void setNumCitizens(int numCitizens) { this.numCitizens = numCitizens; }
	public int getPrimaryRiskType() { return primaryRiskType ; }
	public void setPrimaryRiskType( int newRiskType ) { primaryRiskType = newRiskType ; } 
	public int getConnectedNeighbours () { return connectedNeighbours ;}
	public void setConnectedNeighbours (int ConnectedNeighbours ) { this.connectedNeighbours = ConnectedNeighbours ; }
	public Double getWattsBeta() { return wattsBeta; }
	public void setWattsBeta(double WattsBeta) { this.wattsBeta = WattsBeta; }
	public String getRiskManagerBehavior() { return riskManagerBehavior; }
	public void setRiskManagerBehavior(String riskManagerBehavior) { this.riskManagerBehavior = riskManagerBehavior; }
	public String getMediaBehavior() { return mediaBehavior; }
	public void setMediaBehavior(String mediaBehavior) { this.mediaBehavior = mediaBehavior; }
	public DoubleBag getObjectiveRisks() { return objectiveRisks; }
	public void setObjectiveRisks(DoubleBag objectiveRisk) { this.objectiveRisks = objectiveRisk; }
	public int getEventMemory() { return eventMemory; }
	public void setEventMemory(int eventMemory) { this.eventMemory = eventMemory; }
	
		
	/**
	 * 
	 * @param seed
	 * @param NetworkType
	 * @param ObjectiveFirstRisk
	 * @param NumCitizens
	 * @param EventMemory
	 */
	public Saar(long seed, Double WattsBeta, int ConnectedNeighbours, String OpinionDynamic, Double ObjectiveFirstRisk, int NumCitizens, int EventMemory, Double Confidence, String LogFile, int Verbosity) {
		super(seed);
		area = new Continuous2D(1.0,100,100);
		randomGenerator = new MersenneTwisterFast();
		friendsNetwork = new SparseMultigraph<Agent, Connection>();
		mediaNetwork = new SparseMultigraph<Agent, Connection>();
		objectiveRisks = new DoubleBag();
		
		wattsBeta = WattsBeta;
		connectedNeighbours = ConnectedNeighbours;
		opinionDynamic = OpinionDynamic;
		objectiveRisks.add(0);
		objectiveRisks.add(ObjectiveFirstRisk);
		numCitizens = NumCitizens;
		eventMemory = EventMemory;
		confidence = Confidence;
		logFile = LogFile;
		verbosity = Verbosity;
		
	}
	
	public Saar(long seed, CommandLineArgs config) {
		super(seed);
		area = new Continuous2D(1.0,100,100);
		randomGenerator = new MersenneTwisterFast();
		friendsNetwork = new SparseMultigraph<Agent, Connection>();
		mediaNetwork = new SparseMultigraph<Agent, Connection>();
		objectiveRisks = new DoubleBag();
		
		wattsBeta = config.wattsBeta;
		connectedNeighbours = config.connectedNeighbours;
		opinionDynamic = config.opinionDynamic;
		objectiveRisks.add(0);
		objectiveRisks.add(config.objectiveRisk);
		numCitizens = config.numCitizens;
		eventMemory = config.eventMemory;
		primaryRiskType = config.primaryRiskType;
		//confidence = config.confidence;
		logFile = config.logFile;
		verbosity = config.verbosity;
	}
	
	/**
	 * @param args
	 */
	
	public static void main(String[] args) 
	{		
		// parse arguments. If no arguments are given, defaults from CommandLindArgs Class are used
		CommandLineArgs commandLineArgs = new CommandLineArgs();
		new JCommander(commandLineArgs,args);

		// create the model with command line arguments
		SimState state = new Saar(System.currentTimeMillis(),commandLineArgs);
		
		// start the model
		int numJobs = commandLineArgs.numJobs;
		int numSteps = commandLineArgs.numSteps;
		state.nameThread();
		for(int job = 0; job < numJobs; job++)
		{
			state.setJob(job);
			state.start();
			do
				if (!state.schedule.step(state)) break;
			while(state.schedule.getSteps() < numSteps);
			state.finish();
		}
		
		// Exit
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
		census = new Census(2,objectiveRisks.get(0));
		if ( !logFile.isEmpty() ) 
			census.initializeLogFile(logFile);
		schedule.scheduleRepeating(census);
		if (verbosity > 0)
			census.setConsoleLogging(true);
		
		// place census object visible in gui
		area.setObjectLocation(census, new Double2D(50, 4));
					
		census.log("Event Memory: " + eventMemory + " ");
		census.log("Objective Risk: " + objectiveRisks.getValue(0) + " ");
		
		// add citizens
		census.log("Creating agents: " + numCitizens);
		census.log("Using opinion dynamic: " + opinionDynamic );
		int agentType;
		switch ( opinionDynamic ) {
			case "DEGROOT":
				agentType = Citizen.DEGROOT;
				break;
			case "HEGSELMAN":
				agentType = Citizen.HEGSELMAN;
				break;
			case "ONGGO":
				agentType = Citizen.ONGGO;
				break;
			default:
				// when no opinion dynamic is given, use the first
				agentType = 0; 
				break;
		}
		int xPos = 1;
		int yPos = 10;
		Double initialRisk = 0.0;
		Double lowerRiskBound = objectiveRisks.get(0) * 0.95;
		Double riskInterval = objectiveRisks.get(0) * 0.1;
		for(int i = 0; i < numCitizens; i++)
		{
			initialRisk = lowerRiskBound + randomGenerator.nextDouble() * riskInterval; 
			Citizen citizen = new Citizen(i, this, agentType, initialRisk, confidence); 
			
			// give citizen rules
			DecisionRule tmpRule = new DecisionRule(citizen,Saar.FLOOD,0.2,"evacuate");
			citizen.addRule(tmpRule);
			
			// spread citizens over the area
			if ( xPos < 100 ) 
				xPos = xPos + 2;
			else {
				xPos = 0;
				yPos = yPos + 2;
			}
			area.setObjectLocation(citizen, new Double2D(xPos, yPos));	
			
			// add citizen to social network and schedule 
			friendsNetwork.addVertex(citizen);
			schedule.scheduleRepeating(citizen);
			
		}
		
		// create social network
		createNetwork();		
		
		// add medium
		medium = new Medium(-1, this, Medium.OBJECTIVE);
		area.setObjectLocation(medium,new Double2D(5,4));
		mediaNetwork.addVertex(medium);
		schedule.scheduleRepeating(medium);
		
		
	
	}
	
	/**
	 * 
	 */
	public void finish()
	{
		census.endSession();
		super.finish();
	}
	
	/**
	 * 
	 */
	public void createNetwork()
	{
		// connect neighbours
		census.log("Creating Social Network, connected neighbours: " + connectedNeighbours );

		/*
		try {
			Bag citizens = new Bag(friends.getAllNodes()); // create copy to be sure the Bag doesn't change or gets garbage collected 
			int sideConnections = connectedNeighbours / 2;
			int i = sideConnections;
			Object currentCitizen;
			Object leftCitizen;
			Object rightCitizen;
			for( ; i < ( citizens.size() - sideConnections -1)  ; i++)
			{
				currentCitizen = citizens.get(i);
				for ( int n = 1 ; n <= sideConnections ; n++ ) 
				{
					leftCitizen = citizens.get(i-n);
					rightCitizen = citizens.get(i+n);
					friends.addEdge(currentCitizen,leftCitizen,1.0);
					friends.addEdge(currentCitizen,rightCitizen,1.0);
				}
			}
			
			// handle last nodes
			for ( ; i < citizens.size() ; i++)
			{
				currentCitizen = citizens.get(i);
				for ( int n = 1 ; n <= sideConnections ; n++ ) 
				{
					leftCitizen = citizens.get(i-n);
					friends.addEdge(currentCitizen,leftCitizen,1.0);
					if ( (i+n) < citizens.size() )
					{
						rightCitizen = citizens.get(i+n);
						friends.addEdge(currentCitizen,rightCitizen,1.0);
					}
				}
			}
			
			// rewire with probability beta
			 if ( wattsBeta > 0.0 )
			{
				 if ( random.nextDouble() < wattsBeta ) 
				{
					do {
						int tmp = randomGenerator.nextInt(numCitizens);
						acquaintance = citizens.get(tmp);
					}
					while ( citizen == acquaintance || neighbour == acquaintance || friends.getEdge(citizen, acquaintance) != null );
						
					friends.addEdge(citizen,acquaintance,1.0);
				} 
			}
		}
		catch (Exception e)
		{
			// TOCO: handle exception
		}*/
	}
	
	
	/**
	 * @param degree
	 * @param beta
	 */
	
	public void createNetworkWattsStrogatz(int degree, double beta) 
	{
		/* census.log("Creating Social Network: Watts beta");
		
		Bag citizens = new Bag(friends.getAllNodes()); // create copy to be sure the Bag doesn't change or gets garbage collected
		Bag neighbours = new Bag();
		
		for (int i = 0 ; i < citizens.size() ; i++  ) 
		{
			Object citizen = citizens.get(i);
			Double2D pos = area.getObjectLocation(citizen);
			
			// get degree neigbours
			neighbours = area.getNearestNeighbors(pos, degree, false, false, true, neighbours);
			
			// wire neighbours and/or random node		
			Object acquaintance = new Object();
			for ( int n = 0; n < degree ; n++ ) // should loop to citizens.size(), but that can be larger than degree
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
					while ( citizen == acquaintance || neighbour == acquaintance || friends.getEdge(citizen, acquaintance) != null );
		
					
					friends.addEdge(citizen,acquaintance,1.0);
				} 
				else
				{
					// otherwise, just wire neighbour (if not wired already) 
					if ( friends.getEdge(citizen, neighbour) == null )
						friends.addEdge(citizen,neighbour,1.0);
				}
			}
		}*/

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

		Collection individuals = friendsNetwork.getVertices();
		Citizen tmpCitizen;
		for  ( Iterator iter = individuals.iterator(); iter.hasNext(); ) 
		{
			tmpCitizen = (Citizen) iter.next();
			if ( tmpCitizen.getAgentID() == ID ) 
				return tmpCitizen;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param riskType
	 * @return
	 */
	public Double getObjectiveRisk(int riskType)
	{
		try {
			return objectiveRisks.get(riskType);
		}
		catch (Exception e)
		{
			// TODO: handle this better
			return 0.0;
		}
		
	}
	
}
