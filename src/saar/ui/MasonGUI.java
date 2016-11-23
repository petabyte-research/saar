package saar.ui;

import sim.engine.*;
import sim.display.*;
import sim.portrayal.network.*;
import sim.portrayal.continuous.*;

import javax.swing.*;

import com.beust.jcommander.*;

import java.awt.BorderLayout;

import saar.Saar;
import saar.ui.panels.AgentPanel;
import saar.ui.panels.InfoPanel;

public class MasonGUI extends GUIState {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties, constructors and main
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private InfoPanel infoPanel;
	private AgentPanel agentPanel;
	
	public JFrame display;
	protected ContinuousPortrayal2D yardPortrayal = new ContinuousPortrayal2D();
	protected NetworkPortrayal2D buddiesPortrayal = new NetworkPortrayal2D();
	
	public static String getName() { return "Social Amplification and Attenuation of Risk"; }
    public Object getSimulationInspectedObject() { return state; }  // non-volatile; needed to create inspectors
    protected Saar model;
	
	/**
	 * 
	 * @param commandLineArgs
	 */
	public MasonGUI(CommandLineArgs commandLineArgs) { 
		super(new Saar(System.currentTimeMillis(), commandLineArgs));
	}
	
	/**
	 * 
	 * @param state
	 */
	public MasonGUI(SimState state) { 
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
		MasonGUI vid = new MasonGUI(commandLineArgs);
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
			/*model = (Saar) state;
			// tell the portrayals what to portray and how to portray them
			yardPortrayal.setField( model.getArea() );
			AgentPortrayal agentPortrayal = new AgentPortrayal(model.getPrimaryRiskType() );
			yardPortrayal.setPortrayalForClass( Citizen.class , agentPortrayal);
						
			buddiesPortrayal.setField( new SpatialNetwork2D( model.getArea(), model.getFriends() ) );
			buddiesPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());
 
			// reschedule the displayer
			display.reset();
			display.setBackdrop(Color.white);
			// redraw the display
			display.repaint();*/
		}
		
	/**
	 * 
	 */
		public void init(Controller c)
		{
			super.init(c);
			
			/*display = new Display2D(600,600,this);
			display.setClipping(false);
			displayFrame = display.createFrame();
			displayFrame.setTitle("Saar Display");
			c.registerFrame(displayFrame); // so the frame appears in the "Display" list
			displayFrame.setVisible(true);
			display.attach( yardPortrayal, "Area" );*/
						
		}
		
	    /***
	     * 
	     */
		public void start()
		{
			super.start();
			
			model = (Saar) state;
			
			//---------------SETUP GUI------------------\\
			//*-- SET FRAME
			display = new JFrame();
			display.setSize(1300,750);
			display.setResizable(false);
			display.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			//*-- SET PANEL
			agentPanel = new AgentPanel(display.getWidth()-150,display.getHeight(),model);
			
			//*-- SET INFO PANEL
			infoPanel = new InfoPanel(150,display.getHeight(),model);
			
			agentPanel.setPopUpPanel(infoPanel.getPopUpPanel());
			
			//*-- DISPLAY GUI
			display.add(infoPanel,BorderLayout.WEST);
			display.add(agentPanel,BorderLayout.EAST);
			display.setVisible(true);
		}
		
		/***
	     * 
	     */
		public boolean step()
		{
			boolean b = super.step();
			
			agentPanel.step();
			infoPanel.step();
			
			return b;
		}
		
		/**
		 * 
		 */
		public void load(SimState state)
		{
			super.load(state);
		}
		
		/**
		 * 
		 */
		public void quit()
		{
			super.quit();
			if (display!=null) display.dispose();
			display = null;
		}
		
	
}
