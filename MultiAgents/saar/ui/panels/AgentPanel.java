package saar.ui.panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JPanel;

import saar.Saar;
import saar.agents.Citizen;
import saar.agents.Medium;
import saar.ui.handlers.AgentMouseHandler;
import saar.ui.CitizenDisplay;

public class AgentPanel extends JPanel {
	
	private Saar model;
	private AgentMouseHandler agentClick;
	private CitizenDisplay[] agents;
	
	public AgentPanel(int width, int height, Saar model)
	{
		this.model = model;
		
		//*-- SET AGENT PANEL
		setBackground(Color.white);
		setPreferredSize(new Dimension(width,height));
		setLayout(null);
		
		//init mouse handler
		agentClick = new AgentMouseHandler();
		
		//display agents
		agents = new CitizenDisplay[model.getNumCitizens()];
		int xPos = 13, yPos = 10, widthA = 57, heightA = 30;
		for(int i = 1; i < model.getNumCitizens(); i++)
		{
			Citizen agent = (Citizen) model.getAgent(i);
			CitizenDisplay b = new CitizenDisplay(agent);
			b.setLocation(xPos, yPos);
			b.setSize(widthA, heightA);
			b.setText("A"+i);
			b.addMouseListener(agentClick);
			add(b);
			
			if ( xPos < 1160-10-widthA*2 ){ 
				xPos = xPos + widthA + 3;
			}
			else {
				xPos = 13;
				yPos = yPos + heightA + 3;
			}
			
			agents[i] = b;
			agent.setDisplay(b);
		}
	}
	
	public void step()
	{
		for(int i = 1; i < model.getNumCitizens(); i++)
		{
			agents[i].repaint();
		}
	}
	
	public void setPopUpPanel(PopupPanel popup)
	{	
		agentClick.setPopup(popup);
	}
	
	@Override 
	public boolean isOptimizedDrawingEnabled()
	{
		return false;
	}
}
