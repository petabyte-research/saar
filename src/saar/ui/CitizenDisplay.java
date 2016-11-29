package saar.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

import java.util.Collection;
import java.util.Iterator;

import saar.agents.Citizen;
import sim.field.network.Edge;
import sim.util.Bag;

/**
 * @author Marc van Almkerk
 *
 */

public class CitizenDisplay extends JButton {
	
	static final String UP = "UP";
	static final String DOWN = "DOWN";
	
	private Citizen P;
	private float brightness = 1.0f;
	private Collection friends;
	private Double RP = 1.0;
	private Double RPavgFriends = 1.0;
	private String flow = CitizenDisplay.UP;
	private Boolean selected = false;
	
	public String getName() { return this.getText(); }
	public Double getRiskPercentage() {	return P.getRiskPercentage();	}
	public Double getAverageRiskFriends() {	return RPavgFriends;	}
	public void setBrightness(float percentage)	{	brightness = percentage;	}
	public String getFlow(){	return flow;	}
	public void setOpinionDynamic(int dynamic)	{	P.setOpinionDynamic(dynamic);	}
	public int getOpinionDynamic()	{	return P.getOpinionDynamic();	}
	
	public CitizenDisplay(Citizen parent)
	{
		P = parent;		
		this.setFont(new Font("Sans Serif", Font.PLAIN, 8));
		
		friends = P.getModel().getFriends().getVertices();
	}
	
	public void paintComponent(Graphics g) 
	{
	       
		if(P != null)
	    {
		    Double per = P.getRiskPercentage();
		    int rgb = Color.HSBtoRGB((float)(0.38f*(1-per)), 1.0f, brightness);
		    setBackground(new Color(rgb));
	        
		    super.paintComponent(g);
		       
		    Graphics2D g2 = (Graphics2D) g;
		    g2.setStroke(new BasicStroke(2));
		    g2.draw(new Line2D.Float(3, 3, 3, this.getHeight()-4));
		    g2.draw(new Line2D.Float(this.getWidth()-4, 3, this.getWidth()-4, this.getHeight()-4));
		    g2.setStroke(new BasicStroke(1));
		    g2.draw(new Line2D.Float((this.getWidth()/2), 5, (this.getWidth()/2), this.getHeight()-6));
		    
		    g.setColor(Color.white);
		    if(RP > per)
		    {
		    	g.setColor(Color.green);
		    	flow = CitizenDisplay.DOWN;
		    }
		    else if(RP < per)
		    {
		    	g.setColor(Color.red);
		    	flow = CitizenDisplay.UP;
		    }
		    g.fillOval((int)(per*this.getWidth())-2,(int)(this.getHeight()/2)-2, 4, 4);
		    
		    RP = per;
		}
		else
		{
			System.out.println("NO AGENT ASSIGNED");
		}
	}
	
	public String friendInformation()
	{
		double avgFRP = 0.0;
		double ups = 0.0;
		
		Iterator iter = friends.iterator();
		while (iter.hasNext() )  
		{
			Citizen friend = (Citizen) iter.next();
			avgFRP += friend.getRiskPercentage();
			
			if(friend.getDisplay() != null)
			{
				if(friend.getDisplay().getFlow() == CitizenDisplay.UP) ups+=1;
			}
		}
		
		avgFRP /= friends.size();
		RPavgFriends = avgFRP;
		avgFRP = Math.round(avgFRP*10000)/100;
		ups /= friends.size();
		ups = Math.round(ups*10000)/100;
		
		return "# of friends: "+friends.size()+"<br/>Avg. friend risk: "+(int)(avgFRP)+"%<br/>%Risk UP: "+(int)(ups)+"%<br/>%Risk DOWN: "+(int)(100-ups)+"%";
	}
	
	public void showFriends(boolean flag)
	{
		float newB = 1.0f;
		if(flag) newB = 0.5f;
		Iterator iter = friends.iterator();
		while (iter.hasNext() )  
		{
			Citizen friend = (Citizen) iter.next();
			if( friend.getDisplay() != null ) 
				friend.getDisplay().brightness = newB;
		}
	}
	
	public void toggleSelect()
	{
		selected = !selected;
		if(selected)
		{
			this.setBorder(BorderFactory.createLineBorder(Color.blue, 3));
		}
		else
		{
			System.out.println("achieved!");
			this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		}
	}
}
