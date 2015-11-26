/**
 * 
 */
package saar;

import saar.agents.Citizen;
import sim.util.*;


/**
 * @author QuispelL
 *
 */
public class Message {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	private int messageID;
	private int sender;
	private Bag receivers;
	private String performative;
	private Bag content;
	
	public Bag getReceivers() {	return receivers;}
	public void setReceivers(Bag receivers) {	this.receivers = receivers;	}
	public Bag getContent() {	return content;	}
	public void setContent(Bag content) { this.content = content;}
	public int getMessageID() {	return messageID;}
	public int getSender() {	return sender;	}
	public String getPerformative() {return performative;}
	
	
	/**
	 * 
	 */
	
	public Message(int newSender, String newPerformative) 
	{
		sender = newSender;
		performative = newPerformative;
		receivers = new Bag();
		content = new Bag();
		
		// TODO: determine message ID
		
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Methods
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * 
	 * @param newCitizen
	 */
	
	public void addReceiver(Citizen newCitizen)
	{
		receivers.add(newCitizen);
		
	}

}
