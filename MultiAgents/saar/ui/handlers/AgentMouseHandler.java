package saar.ui.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import saar.ui.CitizenDisplay;
import saar.ui.panels.PopupPanel;

public class AgentMouseHandler implements MouseListener {
	
	private PopupPanel popup;
	
	public void setPopup(PopupPanel popup) { this.popup = popup; }

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		CitizenDisplay agent = (CitizenDisplay) e.getSource();
		agent.showFriends(true);
		popup.setLocation(agent.getLocation());
		popup.setVisible(true);
		popup.setTitle(agent.getName());
		popup.setAgent(agent);
		popup.step();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		CitizenDisplay agent = (CitizenDisplay) e.getSource();
		agent.showFriends(false);
		popup.setAgent(null);
		popup.reset();
	}

}
