package saar.ui.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author Marc van Almkerk
 *
 */

public class Graph extends JPanel {
	
	private int NumberOfSteps;
	
	private Double[][] drawStepsAgent;
	private int nAgents = 0;
	private String message = "";
	private Double drawline = -1.0;
	
	public void setMessage(String message){ this.message = message; }
	public void clearMessage(){ this.message = ""; }
	
	public void drawLine(Double percentage){	this.drawline = (1-percentage);	}
	
	public Graph(int width, int height, int nAgents, int numberOfSteps)
	{
		setSize(width, height);
		setPreferredSize(new Dimension(width,height));
		setBackground(Color.DARK_GRAY);
		
		drawStepsAgent = new Double[nAgents][numberOfSteps];
		for(int i = 0; i < nAgents-1; i++)
		{	Arrays.fill(drawStepsAgent[i], 0.5);	}
		
		this.NumberOfSteps = numberOfSteps;
		this.nAgents = nAgents;
	}
	
	public void addStep(Double[] RPagent)
	{
		for(int j = 0; j < nAgents-1; j++)
		{
			for(int i = 0; i < NumberOfSteps-1; i++)
			{	drawStepsAgent[j][i] = drawStepsAgent[j][i+1];	}
			drawStepsAgent[j][NumberOfSteps-1] = (1-RPagent[j]);
		}
		
		this.repaint();
	}
	
	public void paintComponent(Graphics g) 
	{
		
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		for(int j = 0; j < nAgents-1; j++)
		{
		    for(int i = 1; i < NumberOfSteps-1; i++)
			{
		    	int rgb = Color.HSBtoRGB((float)((1.0f/nAgents)*j), 1.0f, 1.0f);
		    	g2.setColor(new Color(rgb));
		    	Point p1 = new Point((i-1)*(this.getWidth()/NumberOfSteps),(int)(this.getHeight()*drawStepsAgent[j][i-1]));
		    	Point p2 = new Point((i)*(this.getWidth()/NumberOfSteps),(int)(this.getHeight()*drawStepsAgent[j][i]));
		    	
		    	g2.draw(new Line2D.Float(p1.x,p1.y,p2.x,p2.y));
			}
		}
		
		g2.setColor(Color.white);
		g2.draw(new Line2D.Float(this.getWidth()/2,0,this.getWidth()/2,this.getHeight()));
		g2.draw(new Line2D.Float(0,this.getHeight()/2,this.getWidth(),this.getHeight()/2));
		
		g2.setFont(new Font("Arial", Font.PLAIN, 8));
		g2.drawString("100%", this.getWidth()/2+5, 10);
		g2.drawString("0%", this.getWidth()/2+5, this.getHeight()-4);
		
		if(message != "")
		{
			g2.drawString(message, 5, this.getHeight()-4);
		}
		
		if(drawline >= 0.0)
		{
			g2.setColor(Color.yellow);
			g2.drawString("avg RP", 5, (float)(this.getHeight()*drawline)-4);
			g2.draw(new Line2D.Float(0,(float)(this.getHeight()*drawline),this.getWidth(),(float)(this.getHeight()*drawline)));
			drawline = -1.0;
		}
		
	}
	
	public void reset()
	{
		for(int i = 0; i < nAgents-1; i++)
		{	Arrays.fill(drawStepsAgent[i], 0.5);	}
		drawline = -1.0;
	}
	
	public void reset(int nAgents)
	{
		drawStepsAgent = new Double[nAgents][NumberOfSteps];
		for(int i = 0; i < nAgents-1; i++)
		{	Arrays.fill(drawStepsAgent[i], 0.5);	}
		
		drawline = -1.0;
		this.nAgents = nAgents;
	}
}
