package saar.agents;

/**
 * @author QuispelL
 *
 */


import sim.engine.*;
import sim.field.network.*;
import sim.util.*;
import saar.Saar;
import saar.Message;

public class Citizen implements Steppable {
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Properties and constructors
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int HIGHER = 0;
	private int EQUAL = 1;
	private int LOWER = 2; 
	
	private int agentID;
	private Saar model;
	private Bag incomingQueue ;
	private Bag outgoingQueue ; 
	private Double riskPerception ;
	private DoubleBag riskSignal;
	private IntBag rpTotals;
	private int eventMemory; 
	
	public Bag getIncomingQueue() { return incomingQueue;}
	public void setIncomingQueue(Bag incomingQueue) {this.incomingQueue = incomingQueue;}
	public Bag getOutgoingQueue() {return outgoingQueue;}
	public void setOutgoingQueue(Bag outgoingQueue) {this.outgoingQueue = outgoingQueue;}
	public Double getRiskPerception() {return riskPerception;}
	public void setRiskPerception(Double riskPerception) {this.riskPerception = riskPerception;}
	public int getAgentID()	{return agentID;}
	
	/**
	 * 
	 */
	public Citizen(int id) {
		
		agentID = id;
		riskPerception = 0.0;
		initCitizen();
	}
	
	/**
	 * 
	 */
	public Citizen(int id, Double initialRP)
	{
		agentID = id;
		riskPerception = initialRP;
		initCitizen();
	}
	
	/**
	 * 
	 */
	
	private void initCitizen()
	{
		incomingQueue = new Bag();
		outgoingQueue = new Bag(); 
		riskSignal = new DoubleBag();
		rpTotals = new IntBag(3);
		for ( int i = 0 ; i < 3 ; i++ )
			rpTotals.add(1);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Behavior  
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	
	public void step(SimState state)
	{
		// TODO: why get state here and not in constructor ?
		model = (Saar) state;
	
		// reset risk signal
		riskSignal.clear();
		
		// check whether the agent experiences a risk event
		if ( eventMemory >= 0 ) 
			eventMemory--;
		else
			if ( model.randomGenerator.nextDouble(true, true) < model.getObjectiveRisk() ) {
				eventMemory = model.getEventMemory();
				riskPerception = 1.0;
				System.out.println("Risk Event Experienced !!!");
			}
			
		
		// communicate
		gossip();
		processMessages();
		sendMessages();
		
		// perceive risk
		perceiveRisk();
		
	}
	
	/**
	 * 
	 */
	
	
	public void processMessages()
	{
		
		Message tmpMessage;
		while ( ! incomingQueue.isEmpty())
		{
			tmpMessage =  (Message) incomingQueue.pop();
			switch ( tmpMessage.getPerformative() )
			{
				case "gossiprequest":
					// gossip query received; return risk perception in gossip reply
					Message gossipResponse = new Message(agentID,"gossipresponse");
					Bag gossipResponseContent = new Bag();
					gossipResponseContent.add(riskPerception);
					gossipResponse.setContent(gossipResponseContent);
					outgoingQueue.add(gossipResponse);
					break;
				case "gossipresponse":
					// response to gossip request received; store information in risk signal
					riskSignal.add( (Double) tmpMessage.getContent().get(0));
					break;
				default:
					// TODO: handle unknown performative
					System.out.println("Unknown Performative in Message !!!");
					break;
			}
		}
	}
	
	/**
	 * 
	 */
	
	
	public void gossip()
	{
		Message gossipRequest = new Message(agentID,"gossiprequest");
		
		// add neighbours to receivers of gossip query
		Bag neighbours = model.getFriends().getEdgesOut(this);
		for (int i = 0 ; i < neighbours.size(); i++ ) 
			gossipRequest.addReceiver( (Citizen) ((Edge) neighbours.get(i)).getOtherNode(this));
		
		// add gossip query to outgoing message queue
		outgoingQueue.add(gossipRequest);
		
	}
	
	/**
	 * 
	 * 
	 */
		
	public void perceiveRisk()
	{
	
		int riskSignalSize = riskSignal.size();
		if ( riskSignalSize != 0 ) 
		{ 
			// select risk perception of random neighbour
			Double tmpRiskPerception = riskSignal.get(model.randomGenerator.nextInt(riskSignal.size() ) );
				
			// change risk perception with probability based on occurens of lower and higher risk perception
			int rpTotal = rpTotals.get(HIGHER) + rpTotals.get(EQUAL) + rpTotals.get(LOWER);
			
			if ( tmpRiskPerception > riskPerception ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(HIGHER) / rpTotal ) 
					riskPerception = tmpRiskPerception; 
				rpTotals.setValue(HIGHER, (int) rpTotals.getValue(HIGHER) +1 );
			}
			
			if ( tmpRiskPerception == riskPerception ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(EQUAL) / rpTotal ) 
					riskPerception = tmpRiskPerception; 
				rpTotals.setValue(EQUAL, (int) rpTotals.getValue(EQUAL) +1 );
			}
			
			if ( tmpRiskPerception < riskPerception ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(LOWER) / rpTotal ) 
					riskPerception = tmpRiskPerception; 
				rpTotals.setValue(LOWER, (int) rpTotals.getValue(LOWER) +1 );
			}
		}
		
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Auxiliary 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

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
		Bag receivers = message.getReceivers();
		for ( int i = 0 ; i < receivers.size() ; i++)
			((Citizen) receivers.get(i)).receiveMessage(message);
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

