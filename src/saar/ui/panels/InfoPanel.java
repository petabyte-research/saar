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

/**
 * @author Marc van Almkerk
 *
 */

public class InfoPanel extends JPanel {
	
	private Saar model;
	private PopupPanel popup;
	
	public PopupPanel getPopUpPanel(){	return popup; }
	
	public InfoPanel(int width, int height, Saar model)
	{
		this.model = model;
		
		setBackground(Color.black);
		setPreferredSize(new Dimension(width,height));
		setLayout(new BorderLayout());
		
		popup = new PopupPanel(width,(int)(height/1.5), model);
		add(popup,BorderLayout.PAGE_END);
	}
	
	public void step()
	{	
		popup.step();
	}
}
