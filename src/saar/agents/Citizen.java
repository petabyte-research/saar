package saar.agents;

/**
 * @author QuispelL
 *
 */


import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import saar.Saar;

public class Citizen implements Steppable {

	/**
	 * 
	 */
	public Citizen() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * 
	 * */
	
	public void step(SimState state)
	{
		// get state and location
		Saar saar = (Saar) state;
		Continuous2D area = saar.area;
		Double2D me = saar.area.getObjectLocation(this);
	}
	 

}

