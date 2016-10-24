package saar.ui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import saar.Saar;
import saar.agents.Citizen;
import saar.agents.Medium;
import saar.ui.CitizenDisplay;
import saar.ui.MasonGUI;

public class InfoPanel extends JPanel {
	
	private Saar model;
	private JLabel mediumJ, agentJ;
	private PopupPanel popup;
	
	public PopupPanel getPopUpPanel(){	return popup; }
	
	public InfoPanel(int width, int height, Saar model)
	{
		this.model = model;
		
		setBackground(Color.black);
		setPreferredSize(new Dimension(width,height));
		setLayout(new BorderLayout());
		
		/*JLabel l1 = new JLabel("Medium", JLabel.CENTER);
		JLabel l2 = new JLabel("Citizens");
		mediumJ = new JLabel("Medium value: 1.00");
		agentJ = new JLabel("<html><body>avg. risk value: 1.00<br/>min. risk value: 1.00<br/>max. risk value: 1.00<br/>std. risk value: 1.00</body></html>");
		
		Border border, margin;
		
		margin = new EmptyBorder(25,25,0,25);
		l1.setFont(new Font("Sans Serif", Font.BOLD, 14));
		l1.setForeground(Color.white);
		border = l1.getBorder();
		l1.setBorder(new CompoundBorder(border, margin));
		
		l2.setFont(new Font("Sans Serif", Font.BOLD, 14));
		l2.setForeground(Color.white);
		border = l2.getBorder();
		l2.setBorder(new CompoundBorder(border, margin));
		
		margin = new EmptyBorder(3,0,0,0);
		mediumJ.setFont(new Font("Sans Serif", Font.PLAIN, 11));
		mediumJ.setForeground(new Color(215,215,215));
		border = mediumJ.getBorder();
		mediumJ.setBorder(new CompoundBorder(border, margin));
		
		agentJ.setFont(new Font("Sans Serif", Font.PLAIN, 11));
		agentJ.setForeground(new Color(215,215,215));
		border = agentJ.getBorder();
		agentJ.setBorder(new CompoundBorder(border, margin));
	
		add(l1);
		add(mediumJ);
		add(l2);
		add(agentJ);*/
		
		popup = new PopupPanel(width,(int)(height/1.5));
		add(popup,BorderLayout.PAGE_END);
	}
	
	public void step()
	{
		/*Medium medium = model.getMedium();
		switch ( medium.getMediaRole() ) {
			case Medium.OBJECTIVE:  // broadcast real risk
				mediumJ.setText("<html><body>Medium role: OBJECTIVE<br/>Medium Risk message: "+medium.sendValue+"</body></html>");
				break;
			case Medium.LEADER_AVERAGE: // broadcast average of real risk and public risk perception 
				mediumJ.setText("<html><body>Medium role: LEADER AVERAGE<br/>Medium Risk message: "+medium.sendValue+"</body></html>");
				break;
			case Medium.FOLLOWER:	 // broadcast public risk perception
				mediumJ.setText("<html><body>Medium role: FOLLOWER<br/>Medium Risk message: "+medium.sendValue+"</body></html>");
				break;
		}
		
		Double per = (model.getCensus().getMeanRiskPerception(1)-model.getCensus().getMinimumRiskPerception(1))/(model.getCensus().getMaximumRiskPerception(1)-model.getCensus().getMinimumRiskPerception(1));
		
		agentJ.setText("<html><body>avg. risk %: "+per+"</body></html>");*/
		
		popup.step();
	}
}
