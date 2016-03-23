package saar.ui;

import com.beust.jcommander.Parameter;


public class CommandLineArgs {
	
	@Parameter(names = { "-j", "-numJobs" }, description = "Number of jobs (runs) to perform (default: 1) ")
	 public int numJobs = 1;
	
	@Parameter(names = { "-s", "-numSteps" }, description = "Number of steps within a job/run (default: 1000) ")
	public int numSteps = 1000;
	
	@Parameter(names = { "-t", "-networkType" }, description = "Social Network Type (default WattsBeta)")
	public String networkType = "WattsBeta";
	
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
	
	@Parameter(names = { "-c", "-confidenceInterval" }, description = "Confidence Interval used for Bounded Confidence algorithm")
	public Double confidence = 0.1;  // only used with HEGSELMAN opinion dynamic
	

}
