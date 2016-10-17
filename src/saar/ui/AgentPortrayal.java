/**
 * 
 */
package saar.ui;

import java.awt.Graphics2D;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import saar.agents.*;
import saar.*; 

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
		double fraction = 0.0;
	    int drawColor = 0;
	      	
		try {
		  	tmpAgent = (Citizen) object;
	       	riskPerception =  tmpAgent.getRiskPerception(colorRiskPerceptionType);
	       	objectiveRisk = tmpAgent.getModel().getObjectiveRisk(colorRiskPerceptionType);
	       	paint = new java.awt.Color( 0 , 0, 0);
	       	 
	      	if ( riskPerception > objectiveRisk ) { 
	       		if ( riskPerception < tmpAgent.getModel().getCensus().getMaximumRiskPerception(colorRiskPerceptionType) ) { 
	       			fraction = (riskPerception - objectiveRisk) / tmpAgent.getModel().getCensus().getUpperRiskPerceptionInterval(colorRiskPerceptionType);
	       			drawColor = (int) ( fraction * 255.0 );
	       			paint = new java.awt.Color( drawColor , 0, 0);
	       		}
	       		else if ( riskPerception > 0.99 )
	       				paint = new java.awt.Color( 255 , 255, 0);
	       			else
	       				paint = new java.awt.Color( 255 , 0 , 0);
	       	} else 	{
	       		fraction = ( riskPerception - tmpAgent.getModel().getCensus().getMinimumRiskPerception(colorRiskPerceptionType) ) / tmpAgent.getModel().getCensus().getLowerRiskPerceptionInterval(colorRiskPerceptionType);
	       		drawColor = 255 - ((int) ( fraction * 255.0 ));
	   			paint = new java.awt.Color( 0, drawColor , 0 );
	       	}
      	}
		catch (Exception e) {
			//System.out.println(e);
			}
       	 
       	super.draw(object, graphics, info);  // it'll use the new paint  value
    }


}
