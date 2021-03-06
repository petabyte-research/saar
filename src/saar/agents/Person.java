package saar.agents;

import saar.Saar;
import saar.memes.DecisionRule;
import saar.memes.Message;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.*;

public class Person implements Steppable, Agent {

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
	protected Bag decisionRules;
	private double primaryRiskPerception; // dummy to allow for property inspectors and interval. Real Primary Risk stored riskPerceptions at index saar.getPrimaryRiskType

	/* (non-Javadoc)
	 * @see saar.agents.Agent#getRiskPerceptions()
	 */
	@Override
	public DoubleBag getRiskPerceptions() {return riskPerceptions;}
	/* (non-Javadoc)
	 * @see saar.agents.Agent#setRiskPerceptions(double)
	 */
	@Override
	public void setRiskPerceptions( double newRP ) { riskPerceptions.set(0, newRP ) ; }
	/* (non-Javadoc)
	 * @see saar.agents.Agent#getAgentID()
	 */
	@Override
	public int getAgentID() {return agentID;}
	/* (non-Javadoc)
	 * @see saar.agents.Agent#getModel()
	 */
	@Override
	public Saar getModel() { return model ; } 
	/* (non-Javadoc)
	 * @see saar.agents.Agent#getIncomingQueue()
	 */
	@Override
	public Bag getIncomingQueue() { return incomingQueue;}
	/* (non-Javadoc)
	 * @see saar.agents.Agent#getOutgoingQueue()
	 */
	@Override
	public Bag getOutgoingQueue() {return outgoingQueue;}
	/* (non-Javadoc)
	 * @see saar.agents.Agent#getPrimaryRiskPerception()
	 */
	@Override
	public double getPrimaryRiskPerception()  { return riskPerceptions.get( model.getPrimaryRiskType() ); } 
	/* (non-Javadoc)
	 * @see saar.agents.Agent#setPrimaryRiskPerception(double)
	 */
	@Override
	public void setPrimaryRiskPerception( double newRP ) { riskPerceptions.set(model.getPrimaryRiskType(),newRP); }
	/* (non-Javadoc)
	 * @see saar.agents.Agent#domPrimaryRiskPerception()
	 */
	@Override
	public Object domPrimaryRiskPerception() { return new sim.util.Interval(0.0000001,1.0)  ; } 
	
	/* (non-Javadoc)
	 * @see saar.agents.Agent#getRiskPerception(int)
	 */
	@Override
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
	public Person(int iD, Saar Model) {
		super();
		model = Model;
		initAgent(iD);
	}
	
	/* (non-Javadoc)
	 * @see saar.agents.Agent#initAgent(int)
	 */
	@Override
	public void initAgent(int iD)
	{
		agentID = iD;
		incomingQueue = new Bag();
		outgoingQueue = new Bag(); 
		riskPerceptions = new DoubleBag();
		decisionRules = new Bag();
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Behavior  
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/* (non-Javadoc)
	 * @see saar.agents.Agent#step(sim.engine.SimState)
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
	
	/* (non-Javadoc)
	 * @see saar.agents.Agent#receiveMessage(saar.Message)
	 */
	@Override
	public void receiveMessage(Message message) {

		try {
			incomingQueue.add(message);
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see saar.agents.Agent#emptyIncomingQueue()
	 */
	@Override
	public void emptyIncomingQueue()
	{
		incomingQueue.clear();
	}

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Auxiliary
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see saar.agents.Agent#addRule(saar.DecisionRule)
	 */
	@Override
	public void addRule(DecisionRule Rule)
	{
		decisionRules.add(Rule);
	}
	

}