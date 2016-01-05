package saar.ui;


import sim.engine.*;
import sim.display.*;
import sim.portrayal.network.*;
import sim.portrayal.continuous.*;
import sim.portrayal.simple.*;
import javax.swing.*;

import com.beust.jcommander.JCommander;

import java.awt.Color;
import saar.Saar;


public class SaarWithGui extends GUIState {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties, constructors and main
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	public Display2D display;
	public JFrame displayFrame;
	ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
	NetworkPortrayal2D buddiesPortrayal = new NetworkPortrayal2D();
	
	public static String getName() { return "Social Amplification and Attenuation of Risk"; }
    public Object getSimulationInspectedObject() { return state; }  // non-volatile; needed to create inspectors
	
	/**
	 * 
	 * @param commandLineArgs
	 */
	public SaarWithGui(CommandLineArgs commandLineArgs) { 
		super(new Saar(System.currentTimeMillis(), commandLineArgs));
	}
	
	/**
	 * 
	 * @param state
	 */
	public SaarWithGui(SimState state) { 
		super(state); 

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

		// create model and gui
		SaarWithGui vid = new SaarWithGui(commandLineArgs);
		Console c = new Console(vid);
		c.setVisible(true);
	
	}
		
		

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Initialization and starting methods
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		/**
		 * 
		 */
		public void setupPortrayals()
		{
			Saar saar = (Saar) state;
			// tell the portrayals what to portray and how to portray them
			yardPortrayal.setField( saar.getArea() );
			AgentPortrayal agentPortrayal = new AgentPortrayal(Saar.FLOOD);
			yardPortrayal.setPortrayalForAll(agentPortrayal);
			
			buddiesPortrayal.setField( new SpatialNetwork2D( saar.getArea(), saar.getFriends() ) );
			buddiesPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());

					// reschedule the displayer
			display.reset();
			display.setBackdrop(Color.white);
			// redraw the display
			display.repaint();
		}
		
	/**
	 * 
	 */
		public void init(Controller c)
		{
			super.init(c);
			display = new Display2D(600,600,this);
			display.setClipping(false);
			displayFrame = display.createFrame();
			displayFrame.setTitle("Saar Display");
			c.registerFrame(displayFrame); // so the frame appears in the "Display" list
			displayFrame.setVisible(true);
			//display.attach( buddiesPortrayal, "Buddies" );
			display.attach( yardPortrayal, "Area" );
			
		}
		
	    /***
	     * 
	     */
		public void start()
		{
			super.start();
			setupPortrayals();
		
		}
		
		/**
		 * 
		 */
		public void load(SimState state)
		{
			super.load(state);
			setupPortrayals();
		}
		
		/**
		 * 
		 */
		public void quit()
		{
			super.quit();
			if (displayFrame!=null) displayFrame.dispose();
			displayFrame = null;
			display = null;
		}
		
	
}
