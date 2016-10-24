/**
 * 
 */
package saar.ui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

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

	private static Double avgRisk = 0.0;
	private static Double minRisk = Double.MAX_VALUE;
	private static Double maxRisk = Double.MIN_VALUE;
	private static int nAgents = 0;
	
	protected static final long serialVersionUID = 1L;
	
	protected int colorRiskPerceptionType;
	protected DecimalFormat df = new DecimalFormat("#.####");
	
	protected Image citizen = null; 
	protected Image citizen_red = null; 
	
	/**
	 * 
	 */
	public AgentPortrayal(int ColorRiskPerceptionType ) {
		colorRiskPerceptionType = ColorRiskPerceptionType;
		df.setRoundingMode(RoundingMode.CEILING);
		
		try {
			citizen = ImageIO.read(new File("src/img/citizen.png"));
			citizen_red = ImageIO.read(new File("src/img/citizen_red.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	    Image useImage = citizen;
	      	
		try {
		  	tmpAgent = (Citizen) object;
	       	riskPerception =  tmpAgent.getRiskPerception(colorRiskPerceptionType);
	       	System.out.println(colorRiskPerceptionType);
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
	       				paint = new java.awt.Color( 0 , 255 , 0);
	       				useImage = citizen_red;
	       	} else 	{
	       		fraction = ( riskPerception - tmpAgent.getModel().getCensus().getMinimumRiskPerception(colorRiskPerceptionType) ) / tmpAgent.getModel().getCensus().getLowerRiskPerceptionInterval(colorRiskPerceptionType);
	       		drawColor = 255 - ((int) ( fraction * 255.0 ));
	   			paint = new java.awt.Color( 0, drawColor , 0 );
	       	}
      	}
		catch (Exception e) {
			//System.out.println(e);
			}
		
		super.scale = 1.5;
       	
		Rectangle2D.Double draw = info.draw;
		final double width = draw.width*scale + offset;
        final double height = draw.height*scale + offset;
        final int x = (int)(draw.x - width / 2.0);
        final int y = (int)(draw.y - height / 2.0);
        
		graphics.drawImage(useImage, x, y, null);
		
		nAgents++;
		if(riskPerception > maxRisk) maxRisk = riskPerception;
		if(riskPerception < minRisk) minRisk = riskPerception;
		avgRisk += riskPerception;
		
		if(nAgents == 1000)
		{
			avgRisk /= 1000;
			MasonGUI.agentJ.setText("<html><body>avg. agent value: "+df.format(avgRisk*1000)+"<br/>min. agent value: "+df.format(minRisk*1000)+"<br/>max. agent value: "+df.format(maxRisk*1000)+"</body></html>");
			
			nAgents = 0;
			avgRisk = 0.0;
			minRisk = Double.MAX_VALUE;
			maxRisk = Double.MIN_VALUE;
		}
		
       	//super.draw(object, graphics, info);  // it'll use the new paint  value
    }


}
