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
	private double primaryRiskPerception; // dummy to allow for property inspectors and interval. Real Primary Risk stored riskPerceptions at index saar.getPrimaryRiskType

	public DoubleBag getRiskPerceptions() {return riskPerceptions;}
	public void setRiskPerceptions( double newRP ) { riskPerceptions.set(0, newRP ) ; }
	public int getAgentID() {return agentID;}
	public Saar getModel() { return model ; } 
	public Bag getIncomingQueue() { return incomingQueue;}
	public Bag getOutgoingQueue() {return outgoingQueue;}
	public double getPrimaryRiskPerception()  { return riskPerceptions.get( model.getPrimaryRiskType() ); } 
	public void setPrimaryRiskPerception( double newRP ) { riskPerceptions.set(model.getPrimaryRiskType(),newRP); }
	public Object domPrimaryRiskPerception() { return new sim.util.Interval(0.0000001,1.0)  ; } 
	
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
	@Override public void step(SimState state) {
		// TODO: why get state here and not in constructor ?
		model = (Saar) state;
	}
	
	/**
	 * 
	 * @param message
	 */
	protected void processMessage(Message message)
	{
		System.out.println(message.getPerformative() );
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
	 */
	protected void sendMessages() {
		
		try {
			while ( ! outgoingQueue.isEmpty())
				sendMessage(  (Message) outgoingQueue.pop());
		}
		catch ( Exception e) {
			System.out.println(e);
		}
	}
			
	
	/**
	 * 
	 * @param message
	 */
	protected void sendMessage(Message message) {

		try {
			Bag receivers = message.getReceivers();
			for ( int i = 0 ; i < receivers.size() ; i++)
				((Citizen) receivers.get(i)).receiveMessage(message);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * 
	 * @param message
	 */
	public void receiveMessage(Message message) {

		try {
			incomingQueue.add(message);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/**
	 * 
	 */
	public void emptyIncomingQueue()
	{
		incomingQueue.clear();
	}

	
	
	

}