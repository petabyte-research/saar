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
		double riskSpread = 0.0;
		double riskFraction = 0.0;
		
		try {
		  	tmpAgent = (Citizen) object;
	       	riskPerception =  tmpAgent.getRiskPerception(colorRiskPerceptionType);
	       	objectiveRisk = tmpAgent.getModel().getObjectiveRisk(colorRiskPerceptionType);
	       	paint = new java.awt.Color( 0 , 0, 0);
	       	
		}
		catch (Exception e) {
				
		}
		
		int drawColor;
      	if ( riskPerception > objectiveRisk )
       	{ 
       		if ( riskPerception < 1.0 ) {
       			
       	//		riskSpread = tmpAgent.getModel().census.getMaximumRiskPerception(colorRiskPerceptionType) - riskPerception;
       	//		riskFraction = riskSpread / 
       			
       			if ( riskPerception > 1.05 * objectiveRisk )
           			drawColor = 255;
           		else
           			drawColor = 128;
       			paint = new java.awt.Color( drawColor , 0, 0);
       		}
       		else
       			paint = new java.awt.Color( 255 , 255, 0);
       	} else 	{
       		if ( riskPerception > 0.95 * objectiveRisk )
       			drawColor = 128;
       		else
       			drawColor = 255;
       		paint = new java.awt.Color( 0, drawColor , 0 );
       	}
       	 
       	super.draw(object, graphics, info);  // it'll use the new paint  value
    }


}
