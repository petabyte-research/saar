package saar.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import saar.Saar;
import saar.agents.Citizen;
import saar.agents.Medium;
import saar.ui.CitizenDisplay;

/**
 * @author Marc van Almkerk
 *
 */

public class PopupPanel extends JPanel {
	
	private JLabel title, info, graphDesc;
	private JRadioButton[] options;
	private ButtonGroup optionGroup;
	private ActionListener changeMediaType, changeCitizenType;
	private Graph graph;
	
	private CitizenDisplay dataExtract;
	private Saar model;
	
	public PopupPanel(int width, int height, Saar model)
	{
		this.model = model;
		
		setSize(width, height);
		setPreferredSize(new Dimension(width,height));
		setBackground(Color.white);
		setVisible(true);
		
		graph = new Graph(width,width,model.getNumCitizens(),20);
		add(graph);
		
		graphDesc = new JLabel("");
		Border margin = new EmptyBorder(0,10,0,10);
		graphDesc.setFont(new Font("Sans Serif", Font.PLAIN, 9));
		graphDesc.setForeground(new Color(55,55,55));
		Border border = graphDesc.getBorder();
		graphDesc.setBorder(new CompoundBorder(border, margin));
		add(graphDesc);
		
		title = new JLabel("Click a Citizen");
		border = margin = new EmptyBorder(10,25,0,25);
		title.setFont(new Font("Sans Serif", Font.BOLD, 14));
		title.setForeground(new Color(40,40,40));
		border = title.getBorder();
		title.setBorder(new CompoundBorder(border, margin));
		add(title);
		
		info = new JLabel("Or change Medium type:");
		info.setHorizontalAlignment(JLabel.CENTER);
		margin = new EmptyBorder(0,10,0,10);
		info.setFont(new Font("Sans Serif", Font.PLAIN, 11));
		info.setForeground(new Color(55,55,55));
		border = title.getBorder();
		info.setBorder(new CompoundBorder(border, margin));
		add(info);
		
		changeMediaType = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  switch(e.getActionCommand())
		    	  {
						case Medium.OBJECTIVE_STRING:		
								model.getMedium().setMediaRole(Medium.OBJECTIVE);
								break;
						case Medium.LEADER_AVERAGE_STRING:
								model.getMedium().setMediaRole(Medium.LEADER_AVERAGE);
								break;
						case Medium.FOLLOWER_STRING:
								model.getMedium().setMediaRole(Medium.FOLLOWER);
								break;
					}
		      }
		};
		
		changeCitizenType = new ActionListener() {
		      public void actionPerformed(ActionEvent e) {
		    	  switch(e.getActionCommand())
		    	  {
						case Citizen.DEGROOT_STRING:
								dataExtract.setOpinionDynamic(Citizen.DEGROOT);
								break;
						case Citizen.HEGSELMAN_STRING:
								dataExtract.setOpinionDynamic(Citizen.HEGSELMAN);
								break;
						case Citizen.ONGGO_STRING:
								dataExtract.setOpinionDynamic(Citizen.ONGGO);
								break;
					}
		      }
		};
		
		createMediumOptions();
	}
	
	public void setTitle(String t)
	{
		title.setText(t);
	}
	
	public void setAgent(CitizenDisplay agent)
	{
		dataExtract = agent;
	}
	
	public void step()
	{
		if(dataExtract != null)
		{
			graphDesc.setText("<html><body><font color='#00ff00'>green</font>: Agent RP%<br/><font color='#ff0000'>red</font>: Avg RP% friends</body></html>");
			info.setText("<html><body><div style='text-align: center;'>Risk perception: "+Math.round(dataExtract.getRiskPercentage()*10000)/100+"%<br/>Risk flow: "+dataExtract.getFlow()+"<br/><br/>"+dataExtract.friendInformation()+"<br/><br/><br/><br/><br/><br/>change Citizen<br/>Opinion Dynamic:</div></body></html>");
			Double[] RP = new Double[2];
			RP[1] = dataExtract.getRiskPercentage();
			RP[0] = dataExtract.getAverageRiskFriends();
			graph.addStep(RP);
		}
		else
		{
			graphDesc.setText("");
			Double[] RP = new Double[model.getNumCitizens()];
			Double totAvgRP = 0.0;
			for(int i = 1; i < model.getNumCitizens(); i++)
			{
				Citizen agent = (Citizen) model.getAgent(i);
				RP[i-1] = agent.getRiskPercentage();
				totAvgRP += agent.getRiskPercentage();
			}
			totAvgRP /= model.getNumCitizens();
			totAvgRP = Math.min(1,totAvgRP); //soms groter dan 1?? fout in model??
			graph.drawLine(totAvgRP);
			graph.addStep(RP);
		}
	}
	
	public void reset()
	{
		graph.reset(model.getNumCitizens());
		clearOptions(); 
		createMediumOptions();
		
		title.setText("Click a Citizen");
		info.setText("Or change Medium type:");
	}
	
	public void reset(boolean agentFocus)
	{
		if(agentFocus)
		{	
			graph.reset(3); 
			clearOptions(); 
			createCitizenOptions();	
			
			title.setText(""+dataExtract.getName());
			info.setText("");
		}
		else
		{	
			graph.reset(model.getNumCitizens()); 
			clearOptions(); 
			createMediumOptions();
			
			title.setText("Click a Citizen");
			info.setText("Or change Medium type:");
		}
	}
	
	public void hideOptions(boolean flag)
	{
		for(int i = 0; i < options.length; i++)
		{	options[i].setVisible(!flag);	}
	}
	
	private void createMediumOptions()
	{
		options = new JRadioButton[3];
		optionGroup = new ButtonGroup();
		for(int i = 0; i < options.length; i++)
		{
			switch(i)
			{
				case 0: options[i] = new JRadioButton(Medium.OBJECTIVE_STRING);		
						options[i].setActionCommand(Medium.OBJECTIVE_STRING);
						break;
				case 1: options[i] = new JRadioButton(Medium.LEADER_AVERAGE_STRING);
						options[i].setActionCommand(Medium.LEADER_AVERAGE_STRING);
						break;
				case 2: options[i] = new JRadioButton(Medium.FOLLOWER_STRING);
						options[i].setActionCommand(Medium.FOLLOWER_STRING);
						break;
			}
			if(i == 0) options[i].setSelected(true);
			options[i].setFont(new Font("Sans Serif", Font.PLAIN, 11));
			options[i].setBackground(null);
			options[i].addActionListener(changeMediaType);
			optionGroup.add(options[i]);
			add(options[i]);
		}
	}
	
	private void createCitizenOptions()
	{
		options = new JRadioButton[3];
		optionGroup = new ButtonGroup();
		for(int i = 0; i < options.length; i++)
		{
			switch(i)
			{
				case 0: options[i] = new JRadioButton(Citizen.DEGROOT_STRING);		
						options[i].setActionCommand(Citizen.DEGROOT_STRING);
						if(dataExtract.getOpinionDynamic() == Citizen.DEGROOT) options[i].setSelected(true);
						break;
				case 1: options[i] = new JRadioButton(Citizen.HEGSELMAN_STRING);
						options[i].setActionCommand(Citizen.HEGSELMAN_STRING);
						if(dataExtract.getOpinionDynamic() == Citizen.HEGSELMAN) options[i].setSelected(true);
						break;
				case 2: options[i] = new JRadioButton(Citizen.ONGGO_STRING);
						options[i].setActionCommand(Citizen.ONGGO_STRING);
						if(dataExtract.getOpinionDynamic() == Citizen.ONGGO) options[i].setSelected(true);
						break;
			}
			options[i].setFont(new Font("Sans Serif", Font.PLAIN, 11));
			options[i].setBackground(null);
			options[i].addActionListener(changeCitizenType);
			optionGroup.add(options[i]);
			add(options[i]);
		}
	}
	
	private void clearOptions()
	{
		System.out.println("options deleted");
		for(int i = 0; i < options.length; i++)
		{	options[i].setVisible(false); remove(options[i]);		}
	}
}
