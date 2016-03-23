/**
 * 
 */
package saar.ui;

import com.beust.jcommander.JCommander;

import sim.display.Console;
import sim.engine.SimState;

/**
 * @author QuispelL
 *
 */
public class AgentTester extends MasonGUI {

	/**
	 * @param commandLineArgs
	 */
	public AgentTester(CommandLineArgs commandLineArgs) {
		super(commandLineArgs);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param state
	 */
	public AgentTester(SimState state) {
		super(state);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{
		// parse arguments. If no arguments are given, defaults from CommandLindArgs Class are used
		CommandLineArgs commandLineArgs = new CommandLineArgs();
		new JCommander(commandLineArgs,args);
		
		// change parameters to suit testing
		commandLineArgs.numCitizens = 15;
		commandLineArgs.numSteps = 10;
		commandLineArgs.objectiveRisk = 0.1;
		
		// create model and gui
		AgentTester vid = new AgentTester(commandLineArgs);
		Console c = new Console(vid);
		c.setVisible(true);
	
	}

}
