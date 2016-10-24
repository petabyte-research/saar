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

import javax.imageio.ImageIO;

import sim.portrayal.DrawInfo2D;
import sim.portrayal.simple.OvalPortrayal2D;
import saar.agents.*;
import saar.*; 

/**
 * @author QuispelL
 *
 */
public class MediumPortrayal extends OvalPortrayal2D {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	protected static final long serialVersionUID = 1L;
	
	protected int colorRiskPerceptionType;
	
	protected Image medium = null; 
	
	/**
	 * 
	 */
	public MediumPortrayal() {
		
		try {
			medium = ImageIO.read(new File("src/img/medium.png"));
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
		Medium tmpAgent = (Medium) object;	
		
		switch ( tmpAgent.getMediaRole() ) {
			case Medium.OBJECTIVE:  // broadcast real risk
				MasonGUI.mediumJ.setText("<html><body>Medium role: OBJECTIVE<br/>Medium Risk message: "+tmpAgent.sendValue+"</body></html>");
				break;
			case Medium.LEADER_AVERAGE: // broadcast average of real risk and public risk perception 
				MasonGUI.mediumJ.setText("<html><body>Medium role: LEADER AVERAGE<br/>Medium Risk message: "+tmpAgent.sendValue+"</body></html>");
				break;
			case Medium.FOLLOWER:	 // broadcast public risk perception
				MasonGUI.mediumJ.setText("<html><body>Medium role: FOLLOWER<br/>Medium Risk message: "+tmpAgent.sendValue+"</body></html>");
				break;
		}
		
		super.scale = 1.5;
       	
		Rectangle2D.Double draw = info.draw;
		final double width = draw.width*scale + offset;
        final double height = draw.height*scale + offset;
        final int x = (int)(draw.x - width / 2.0);
        final int y = (int)(draw.y - height / 2.0);
        
		graphics.drawImage(medium, x, y, null);
    }


}
