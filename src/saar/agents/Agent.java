package saar.agents;

import saar.Message;
import saar.Saar;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;

public class Agent {

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	protected static final long serialVersionUID = 1L;
	
	protected int agentID;
	protected Saar model;
	protected Bag incomingQueue;
	protected Bag outgoingQueue;
	protected Double riskPerception;

	public Double getRiskPerception() {return riskPerception;}
	public int getAgentID() {return agentID;}

	
	public Agent() {
		super();
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Behavior  
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * 
	 *  * @param state
	 */
	public void step(SimState state) {
		// TODO: why get state here and not in constructor ?
		model = (Saar) state;
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Communication 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/*
	 * 
	 */
	protected void processMessages()
	{
		Message tmpMessage;
		while ( ! incomingQueue.isEmpty())
			processMessage((Message) incomingQueue.pop());
	}
	
	/*
	 * 
	 * @param message
	 */
	protected void processMessage(Message message)
	{
		// TODO: design agent interface along with superclass
		System.out.println(message.getPerformative() );
	}
	
	/**
	 * 
	 */
	protected void sendMessages() {
		while ( ! outgoingQueue.isEmpty())
			sendMessage(  (Message) outgoingQueue.pop());
	}
	
	/**
	 * 
	 * @param message
	 */
	protected void sendMessage(Message message) {
		// TODO: error handling
		Bag receivers = message.getReceivers();
		for ( int i = 0 ; i < receivers.size() ; i++)
			((Citizen) receivers.get(i)).receiveMessage(message);
	}
	
	/**
	 * 
	 * @param message
	 */
	public void receiveMessage(Message message) {
		// TODO: error handling
		incomingQueue.add(message);
	}

	
	
	

}