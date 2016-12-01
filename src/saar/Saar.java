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
import saar.memes.DecisionRule;
import saar.ui.*;
import com.beust.jcommander.*;
import com.google.common.base.Supplier;
import java.util.Collection;
import java.util.Iterator;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.algorithms.generators.*;


public class Saar extends SimState
{
	//protected Supplier<UndirectedGraph<String,Number>> undirectedGraphFactory;
    protected Supplier<DirectedGraph<Agent,Link>> directedGraphFactory;
	protected Supplier<Agent> vertexFactory;
	protected Supplier<Link> edgeFactory;
	

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Static constants
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final long serialVersionUID = 1L;
	
	// risk types
	public static final int FLOOD = 0;
	
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
	private Graph<Agent,Link> friendsNetwork;
	private Graph<Agent, Link> mediaNetwork;
	private Census census;
	private Medium medium;

	// configuration properties
	private int numCitizens;
	private int primaryRiskType;
	private Double beta; 
	private int connectedNeighbours;
	private String riskManagerBehavior;
	private String mediaBehavior;
	private String opinionDynamic;
	private DoubleBag objectiveRisks;
	private int eventMemory;
	private Double confidence;
	private String logFile;
	private int verbosity;
	private int agentType;
		
	public Continuous2D getArea() { return area;} 
	public Graph<Agent, Link> getFriends() { return friendsNetwork;}
	public Census getCensus() { return census; }
	public Medium getMedium() { return medium;}
	
	public int getNumCitizens() { return numCitizens;}
	public void setNumCitizens(int numCitizens) { this.numCitizens = numCitizens; }
	public int getPrimaryRiskType() { return primaryRiskType ; }
	public void setPrimaryRiskType( int newRiskType ) { primaryRiskType = newRiskType ; } 
	public int getConnectedNeighbours () { return connectedNeighbours ;}
	public void setConnectedNeighbours (int ConnectedNeighbours ) { this.connectedNeighbours = ConnectedNeighbours ; }
	public Double getBeta() { return beta; }
	public void setWattsBeta(double WattsBeta) { this.beta = WattsBeta; }
	public String getRiskManagerBehavior() { return riskManagerBehavior; }
	public void setRiskManagerBehavior(String riskManagerBehavior) { this.riskManagerBehavior = riskManagerBehavior; }
	public String getOpinionDynamic() { return opinionDynamic; }
	public String getMediaBehavior() { return mediaBehavior; }
	public void setMediaBehavior(String mediaBehavior) { this.mediaBehavior = mediaBehavior; }
	public DoubleBag getObjectiveRisks() { return objectiveRisks; }
	public void setObjectiveRisks(DoubleBag objectiveRisk) { this.objectiveRisks = objectiveRisk; }
	public int getEventMemory() { return eventMemory; }
	public void setEventMemory(int eventMemory) { this.eventMemory = eventMemory; }
	public Double getConfidence() { return confidence ;}
	public void setConfidence(Double Confidence ) { confidence = Confidence ; } 
	public int getAgentType() { return agentType ; } 
	
		
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
		init();
		
		beta = WattsBeta;
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
		init();
		
		beta = config.wattsBeta;
		connectedNeighbours = config.connectedNeighbours;
		opinionDynamic = config.opinionDynamic;
		objectiveRisks.add(0);
		objectiveRisks.add(config.objectiveRisk);
		numCitizens = config.numCitizens;
		eventMemory = config.eventMemory;
		primaryRiskType = config.primaryRiskType;
		confidence = config.confidence;
		logFile = config.logFile;
		verbosity = config.verbosity;
		
		
		
	}
	
	protected void init()
	{
	
		area = new Continuous2D(1.0,100,100);
		randomGenerator = new MersenneTwisterFast();
		objectiveRisks = new DoubleBag();
		
		directedGraphFactory = new Supplier<DirectedGraph<Agent,Link>>() 
		{
            public DirectedGraph<Agent,Link> get() { return new DirectedSparseMultigraph<Agent,Link>();}
        };
        
        Saar model = this;
		vertexFactory = new Supplier<Agent>() 
		{
			int count; 
			Citizen citizen;
			DecisionRule tmpRule;
			public Citizen get() 
				{ 
					citizen = new Citizen(count++,model,agentType,0.1,confidence);
					schedule.scheduleRepeating(citizen);
					tmpRule = new DecisionRule(citizen,Saar.FLOOD,0.2,"evacuate");
					citizen.addRule(tmpRule);
					return citizen;
				} // TODO: how to initialize initial risk perception and decision rules ?
		};
		
		edgeFactory = new Supplier<Link>() 
		{
			int count;
			public Connection get() { return new Connection(count++,Connection.DIRECTED);}
		};
				
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
		/* if (verbosity != 0) 
			census.setConsoleLogging(true); */
		// TODO: find out why this does not work
	
		// place census object visible in gui
		area.setObjectLocation(census, new Double2D(50, 4));
					
		census.log("Event Memory: " + eventMemory + " ");
		census.log("Objective Risk: " + objectiveRisks.getValue(0) + " ");
		
		// add citizens
		census.log("Creating agents: " + numCitizens);
		census.log("Using opinion dynamic: " + opinionDynamic );

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
			
			// give citizen rules
			
			// spread citizens over the area
			if ( xPos < 100 ) 
				xPos = xPos + 2;
			else {
				xPos = 0;
				yPos = yPos + 2;
			}
			//area.setObjectLocation(citizen, new Double2D(xPos, yPos));	
			
			// add citizen to social network and schedule 
			//schedule.scheduleRepeating(citizen);*/
			
		}
		
		// create social network
		createNetwork("Lattice2d");		
		
		// add medium
		medium = new Medium(-1, this, Medium.OBJECTIVE);
		area.setObjectLocation(medium,new Double2D(5,4));
		//mediaNetwork.addVertex(medium);
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
	public void createNetwork(String networkType)
	{
		// connect neighbours
		census.log("Creating Social Network. Type: " + networkType + ", connected neighbours: " + connectedNeighbours + ", beta: " + beta );
			
		try {
			GraphGenerator<Agent,Link> generator ;
			switch ( networkType )  {
				case "BarabasiAlbert":
			 
					break;
				case "EppsteinPowerLaw":
					
					break;
				case "ErdosRenyi" :
					
					break;
				case "KleinbergSmallWorld":
					
					break;
				case "Lattice2d":
				default:
					// when no networktype is given, generate lattice2d network
					generator = new Lattice2DGenerator<Agent,Link>(directedGraphFactory,vertexFactory,edgeFactory,numCitizens/2,false); 
					friendsNetwork = generator.get();
					break;
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
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
