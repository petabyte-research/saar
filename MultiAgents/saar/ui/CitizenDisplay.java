package saar.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JButton;

import saar.agents.Citizen;
import sim.field.network.Edge;
import sim.util.Bag;

public class CitizenDisplay extends JButton {
	
	static final String UP = "UP";
	static final String DOWN = "DOWN";
	
	private Citizen P;
	private float brightness = 1.0f;
	private Bag friends;
	private Double RP = 1.0;
	private String flow = CitizenDisplay.UP;
	
	public String getName() { return this.getText(); }
	public Double getRiskPercentage() {	return P.getRiskPercentage();	}
	public void setBrightness(float percentage)	{	brightness = percentage;	}
	public String getFlow(){	return flow;	}
	
	public CitizenDisplay(Citizen parent)
	{
		P = parent;		
		this.setFont(new Font("Sans Serif", Font.PLAIN, 8));
		
		friends = P.getModel().getFriends().getEdgesOut(P);
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
		for (int i = 0 ; i < friends.size(); i++ )
		{
			Citizen friend = (Citizen) ((Edge) friends.get(i)).getOtherNode(P);
			avgFRP += friend.getRiskPercentage();
			
			if(friend.getDisplay() != null)
			{
				if(friend.getDisplay().getFlow() == CitizenDisplay.UP) ups+=1;
			}
		}
		
		avgFRP /= friends.size();
		avgFRP = Math.round(avgFRP*10000)/100;
		ups /= friends.size();
		ups = Math.round(ups*10000)/100;
		
		return "# of friends: "+friends.size()+"<br/>Avg. friend risk: "+(int)(avgFRP)+"%<br/>UP: "+(int)(ups)+"<br/>DOWN: "+(int)(100-ups);
	}
	
	public void showFriends(boolean flag)
	{
		float newB = 1.0f;
		if(flag) newB = 0.5f;
		for (int i = 0 ; i < friends.size(); i++ )
		{
			Citizen friend = (Citizen) ((Edge) friends.get(i)).getOtherNode(P);
			if(friend.getDisplay() != null) friend.getDisplay().brightness = newB;
		}
	}
}
