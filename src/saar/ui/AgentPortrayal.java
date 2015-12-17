/**
 * 
 */
package saar.ui;

import java.awt.Graphics2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import saar.agents.*;

/**
 * @author QuispelL
 *
 */
public class AgentPortrayal extends OvalPortrayal2D {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	protected static final long serialVersionUID = 1L;
	
	protected int colorRiskPerceptionType;
	
	/**
	 * 
	 */
	public AgentPortrayal(int ColorRiskPerceptionType ) {
		colorRiskPerceptionType = ColorRiskPerceptionType;
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Behavior  
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	public void draw(Object object, Graphics2D graphics, DrawInfo2D info)
    {
		Citizen tmpAgent = null;
		double riskPerception = 0.0;
		double objectiveRisk= 0.0;
		try {
		  	tmpAgent = (Citizen) object;
	       	riskPerception =  tmpAgent.getRiskPerception(colorRiskPerceptionType);
	       	objectiveRisk = tmpAgent.getModel().getObjectiveRisk(colorRiskPerceptionType);
	       	paint = new java.awt.Color( 0 , 0, 0);
		}
		catch (Exception e) {
				
		}
		
				         	
       	if ( riskPerception > objectiveRisk )
       	{ 
       	// risk perception greater then objective risk
       		if ( riskPerception < 1.0 )
       			paint = new java.awt.Color( (int)  ( riskPerception * 255) , 0, 0);
       		else
       			paint = new java.awt.Color( 255 , 255, 0);
       	} else 	{
       		paint = new java.awt.Color( 0, 128 , 0 );
       	}
       	 
       	super.draw(object, graphics, info);  // it'll use the new paint  value
    }


}
