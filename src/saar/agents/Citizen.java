package saar.agents;

/**
 * @author QuispelL
 *
 */


import sim.engine.*;
import sim.field.continuous.*;
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

	private int agentID;
	private Saar model;
	private Bag incomingQueue ;
	private Bag outgoingQueue ; 
	private Double riskPerception ;
	private DoubleBag riskSignal;
	
	public Bag getIncomingQueue() { return incomingQueue;}
	public void setIncomingQueue(Bag incomingQueue) {this.incomingQueue = incomingQueue;}
	public Bag getOutgoingQueue() {return outgoingQueue;}
	public void setOutgoingQueue(Bag outgoingQueue) {this.outgoingQueue = outgoingQueue;}
	public Double getRiskPerception() {return riskPerception;}
	//public void setRiskPerception(Double riskPerception) {this.riskPerception = riskPerception;}
	public int getAgentID()	{return agentID;}
	
	/**
	 * 
	 */
	public Citizen(int id) {
		
		agentID = id;
		incomingQueue = new Bag();
		outgoingQueue = new Bag(); 
		riskPerception = 0.0;
		riskSignal = new DoubleBag();

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
		Bag neighbours = model.friends.getEdgesOut(this);
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

