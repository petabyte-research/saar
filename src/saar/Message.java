/**
 * 
 */
package saar;

import sim.util.*;


/**
 * @author QuispelL
 *
 */
public class Message {
	
	private int messageID;
	private int sender;
	private IntBag receivers;
	private String performative;
	private Bag content;
	
	/**
	 * 
	 * @return
	 */
	public IntBag getReceivers() {
		return receivers;
	}
	
	/**
	 * 
	 * @param receivers
	 */
	public void setReceivers(IntBag receivers) {
		this.receivers = receivers;
	}
	
	/**
	 * 
	 * @return
	 */
	public Bag getContent() {
		return content;
	}
	
	/**
	 * 
	 * @param content
	 */
	public void setContent(Bag content) {
		this.content = content;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getMessageID() {
		return messageID;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSender() {
		return sender;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getPerformative() {
		return performative;
	}
	
	/**
	 * 
	 */
	
	public Message(int newSender, String newPerformative) 
	{
		sender = newSender;
		performative = newPerformative;
		receivers = new IntBag();
		content = new Bag();
		
		// TODO: determine message ID
		
	}
	
	
	

}
