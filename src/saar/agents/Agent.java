package saar.agents;

import saar.Message;
import saar.Saar;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.*;

public class Agent implements Steppable {

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
	protected DoubleBag riskPerceptions;

	public DoubleBag getRiskPerceptions() {return riskPerceptions;}
	public int getAgentID() {return agentID;}
	public Saar getModel() { return model ; } 
	public Bag getIncomingQueue() { return incomingQueue;}
	public Bag getOutgoingQueue() {return outgoingQueue;}
	public Double getPrimaryRiskPerception() { return riskPerceptions.get(0); } 
		
	/**
	 * 
	 */
	public Agent() {
		super();
		initAgent(0); // TODO: determine ID here
	}
	
	/**
	 * 
	 * @param iD
	 */
	public Agent(int iD, Saar Model) {
		super();
		model = Model;
		initAgent(iD);
	}
	
	/**
	 * 
	 */
	public void initAgent(int iD)
	{
		agentID = iD;
		incomingQueue = new Bag();
		outgoingQueue = new Bag(); 
		riskPerceptions = new DoubleBag();
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
	
	/**
	 * 
	 * @param riskType
	 * @return
	 */
	public double getRiskPerception(int riskType)
	{
		try { 
			return riskPerceptions.get(riskType);
		}
		catch ( Exception e )
		{
			// TODO: handle this better
			return 0.0;
		}
		
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Communication 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	protected void processMessages()
	{
		while ( ! incomingQueue.isEmpty())
			processMessage((Message) incomingQueue.pop());
	}
	
	/**
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