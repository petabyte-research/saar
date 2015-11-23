package saar.agents;

/**
 * @author QuispelL
 *
 */


import sim.engine.*;
import sim.field.continuous.*;
import sim.util.*;
import saar.Saar;
import saar.Message;

public class Citizen implements Steppable {
	
	private int agentID;
	private Saar model;
	private Bag incomingQueue ;
	private Bag outgoingQueue ; 
	
	/*
	 * 
	 */
	
	public int getAgentID()
	{
		return agentID;
	}
	
	/**
	 * 
	 */
	public Citizen(int id) {
		
		agentID = id;
		incomingQueue = new Bag();
		outgoingQueue = new Bag(); 
	}
	
	/**
	 * 
	 * */
	
	public void step(SimState state)
	{
		// get state 
		// TODO: why get state here and not in constructor ?
		model = (Saar) state;
		
	}
	
	/**
	 * 
	 */
	
	public void processMessages()
	{
		
	}
	
	/**
	 * 
	 * @param message
	 */
	
	public void receiveMessage(Message message)
	{
		// TODO: error handling
		incomingQueue.add(message);
	}
	
	/**
	 * 
	 * @param message
	 */
	
	public void sendMessage(Message message)
	{
		// TODO: error handling
		
		IntBag receivers = message.getReceivers();
		for ( int i = 0 ; i < receivers.size() ; i++)
			((Citizen) model.getAgent(receivers.get(i))).receiveMessage(message);
	}
	
	/*
	 * 
	 */

	public void sendMessages()
	{
		// TODO: error handling
		for ( int i = 0 ; i < outgoingQueue.size() ; i++) 
			sendMessage( (Message) outgoingQueue.get(i) );

	}
}

