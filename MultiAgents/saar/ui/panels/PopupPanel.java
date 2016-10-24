package saar.ui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import saar.ui.CitizenDisplay;

public class PopupPanel extends JPanel {
	
	private JLabel title;
	private JLabel info;
	
	private CitizenDisplay dataExtract;
	
	public PopupPanel(int width, int height)
	{
		
		setSize(width, height);
		setPreferredSize(new Dimension(width,height));
		setBackground(Color.gray);
		//setLocation(10, 10);
		setVisible(true);
		
		title = new JLabel("Click an Citizen");
		Border border, margin = new EmptyBorder(10,25,0,25);
		title.setFont(new Font("Sans Serif", Font.BOLD, 14));
		title.setForeground(new Color(215,215,215));
		border = title.getBorder();
		title.setBorder(new CompoundBorder(border, margin));
		add(title);
		
		info = new JLabel("");
		margin = new EmptyBorder(0,10,0,10);
		info.setFont(new Font("Sans Serif", Font.PLAIN, 11));
		info.setForeground(new Color(200,200,200));
		border = title.getBorder();
		info.setBorder(new CompoundBorder(border, margin));
		add(info);
		
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
			info.setText("<html><body>Risk perception: "+Math.round(dataExtract.getRiskPercentage()*10000)/100+"%<br/>Flow: "+dataExtract.getFlow()+"<br/><br/>"+dataExtract.friendInformation()+"</body></html>");
		}
	}
	
	public void reset()
	{
		title.setText("Click an Citizen");
		info.setText("");
	}
}
