/**
 * 
 */
package saar.agents;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import saar.*;



/**
 * @author QuispelL
 *
 */


public class Census implements Steppable 
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Properties and constructors
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private Double averageRiskPerception;
	
	public Double getAverageRiskPerception() { return averageRiskPerception ; }
	
	public void Census()
	{
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Methods 
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */

	public void step(SimState state)
	{
		// calculate average risk perception
		Saar model = (Saar) state;
		Bag citizens = new Bag(model.getFriends().getAllNodes());
		int numberOfCitizens = citizens.size();
		averageRiskPerception = 0.0;
		
		for(int i = 0 ; i < numberOfCitizens ; i++)
			averageRiskPerception = averageRiskPerception + ((Citizen) citizens.get(i)).getRiskPerception();
		averageRiskPerception = averageRiskPerception / numberOfCitizens;
		
	}

}


