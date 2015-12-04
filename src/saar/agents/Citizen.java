package saar.agents;

/**
 * @author QuispelL
 *
 */


import sim.engine.*;
import sim.field.network.*;
import sim.util.*;
import saar.Message;

public class Citizen extends Agent  {
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Properties and constructors
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int HIGHER = 0;
	private int EQUAL = 1;
	private int LOWER = 2; 
	
	DoubleBag riskSignal;
	private IntBag rpTotals;
	int eventMemory; 
	
	public Bag getIncomingQueue() { return incomingQueue;}
	public void setIncomingQueue(Bag incomingQueue) {this.incomingQueue = incomingQueue;}
	public Bag getOutgoingQueue() {return outgoingQueue;}
	public void setOutgoingQueue(Bag outgoingQueue) {this.outgoingQueue = outgoingQueue;}
	public void setRiskPerception(Double riskPerception) {this.riskPerception = riskPerception;}
	
	/**
	 * 
	 */
	public Citizen(int id) 
	{
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
		super.step(state);
		
		// reset risk signal
		riskSignal.clear();
		
		// check whether the agent experiences a risk event
		if ( eventMemory >= 0 ) 
			eventMemory--;
		else
			if ( model.randomGenerator.nextDouble(true, true) < model.getObjectiveRisk() ) {
				eventMemory = model.getEventMemory();
				riskPerception = 1.0;
				model.census.log("! Risk Event Experienced by agent " + String.valueOf(agentID) + " ");
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
	 * @param message
	 */
	public void processMessage(Message message)
	{
		switch ( message.getPerformative() )
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
				riskSignal.add( (Double) message.getContent().get(0));
				break;
			default:
				// TODO: handle unknown performative
				System.out.println("Unknown Performative in Message !!!");
				break;
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
	

}

