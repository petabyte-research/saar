package saar.ui.handlers;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import saar.ui.CitizenDisplay;
import saar.ui.panels.PopupPanel;

/**
 * @author Marc van Almkerk
 *
 */

public class AgentMouseHandler implements MouseListener {
	
	private PopupPanel popup;
	private CitizenDisplay target = null;
	
	public void setPopup(PopupPanel popup) { this.popup = popup; }

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(target != (CitizenDisplay) e.getSource())
		{
			if(target != null){
				target.toggleSelect();
				target.showFriends(false);
			}
			
			target = (CitizenDisplay) e.getSource();
			target.showFriends(true);
			target.toggleSelect();
			popup.setLocation(target.getLocation());
			popup.setVisible(true);
			popup.setTitle(target.getName());
			popup.setAgent(target);
			popup.reset(true);
			popup.step();
		}
		else
		{
			target.showFriends(false);
			target.toggleSelect();
			popup.setAgent(null);
			popup.reset();
			target = null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
