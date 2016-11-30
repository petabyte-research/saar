package saar.ui;

import com.beust.jcommander.Parameter;


public class CommandLineArgs {
	
	@Parameter(names = { "-j", "-numJobs" }, description = "Number of jobs (runs) to perform (default: 1) ")
	 public int numJobs = 1;
	
	@Parameter(names = { "-s", "-numSteps" }, description = "Number of steps within a job/run (default: 1000) ")
	public int numSteps = 1000;
	
	@Parameter(names = { "-c", "-connectedNeighbours" }, description = "Number of neighbours connection per citizen (has to be even number, default: 4) ")
	public int connectedNeighbours = 4;
	
	@Parameter(names = { "-b", "-wattsBeta" }, description = "Beta parameter for social network (probability of rewiring edge, default 0.25) ")
	public Double wattsBeta = 0.25;
	
	@Parameter(names = { "-d", "-opinionDynamic" }, description = "Opinion Dynamic (default Onggo)")
	public String opinionDynamic = "DEGROOT";
	 	 
	@Parameter(names = { "-o", "-objectiveRisk" }, description = "Objective Risk, probability of experiencing risk event (default 0.0001) ")
	public Double objectiveRisk = 0.0001;
	
	@Parameter(names = { "-n", "-numCitizens" }, description = "Number of citizens (default: 1000) ")
	public int numCitizens = 1000;
	
	@Parameter(names = { "-m", "-eventMemory" }, description = "Memory for risk event in steps (default: 10) ")
	public int eventMemory = 10;
	
	@Parameter(names = { "-p", "-primaryRiskType" }, description = "Risk type used for statistics and display ")
	public int primaryRiskType = 1;  // 0 is not used for risk perception calculations
	
	@Parameter(names = { "-r", "-confidence" }, description = "Confidence Interval used for Bounded Confidence algorithm")
	public Double confidence = 1.0;  
	
	@Parameter(names = { "-l", "-logfile" }, description = "Start word/characters for log file. When omitted, no log file will be used.")
	public String logFile = ""; // default is to not use a log file
	
	@Parameter(names = { "-v", "-verbose" }, description = "Enable logging to stdout. Default is disabled.")
	public int verbosity = 0; 

}
