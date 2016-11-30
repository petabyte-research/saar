/**
 * 
 */
package saar.ui;

import com.beust.jcommander.JCommander;

import sim.display.Console;
import sim.display.Controller;
import sim.engine.SimState;
import sim.field.continuous.*;
import sim.util.*;
import saar.agents.*;

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
		commandLineArgs.numCitizens = 16;
		commandLineArgs.objectiveRisk = 0.3;
		commandLineArgs.verbosity = 1;
		commandLineArgs.connectedNeighbours = 4;
		commandLineArgs.wattsBeta = 0.25;
		
		// create model and gui
		AgentTester vid = new AgentTester(commandLineArgs);
		Console c = new Console(vid);
		c.setVisible(true);
	
	}
	
	/*
	 * (non-Javadoc)
	 * @see saar.ui.MasonGUI#init(sim.display.Controller)
	 */
	public void init(Controller c)
	{
		super.init(c);

	}
	
	/* 
	public void setupPortrayals()
	{
		super.setupPortrayals();
		Continuous2D area = model.getArea(); 
		
		// redistribute agents for better viewing
		Bag citizens = area.getAllObjects();
		int xPosition = 10;
		int yPosition = 10;
		for  ( int i = 0; i < citizens.size() ; i++ ) {
			Object tmp = citizens.get(i);
			if ( tmp.getClass() == Citizen.class ) {
				area.setObjectLocation(tmp, new Double2D(xPosition, yPosition));
				xPosition += 15;
				if ( xPosition >= 145 ) { 
					xPosition = 10;
					yPosition = yPosition + 15; 
				}
			}
			
		}
		
	}*/

}
