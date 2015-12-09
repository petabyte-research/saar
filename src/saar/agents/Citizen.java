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

	protected static final long serialVersionUID = 1L;
	
	private static final int HIGHER = 0;
	private static final int EQUAL = 1;
	private static final int LOWER = 2; 
	
	protected static final int FLOOD = 0;
	
	private DoubleBag riskSignal;
	private IntBag rpTotals;
	int eventMemory; 
	
	public Bag getIncomingQueue() { return incomingQueue;}
	public void setIncomingQueue(Bag incomingQueue) {this.incomingQueue = incomingQueue;}
	public Bag getOutgoingQueue() {return outgoingQueue;}
	public void setOutgoingQueue(Bag outgoingQueue) {this.outgoingQueue = outgoingQueue;}
	//public void setRiskPerception(Double riskPerception) {this.riskPerception = riskPerception;}
	
	/**
	 * 
	 * @param id
	 */
	public Citizen(int id) 
	{
		super(id);
		initCitizen();
	}
	
	/**
	 * 
	 * @param id
	 * @param initialFirstRP
	 */
	public Citizen(int id, Double initialFirstRP)
	{
		super(id);
		riskPerceptions.add(initialFirstRP);
		initCitizen();
	}
	
	/**
	 * 
	 * @param id
	 * @param initialRP
	 */
	public Citizen(int id, DoubleBag initialRP)
	{
		super(id);
		agentID = id;
		for ( int i = 0 ; i < initialRP.size() ; i++)
			riskPerceptions.add(initialRP.get(i));
		initCitizen();
	}
	
	
	/**
	 * 
	 */
	private void initCitizen()
	{
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
		
		// check whether the agent experiences or remembers a flood risk event
		if ( eventMemory >= 0 ) 
			eventMemory--;
		else
			if ( experiencedRiskEvent(FLOOD) ) {
				eventMemory = model.getEventMemory();
				riskPerceptions.setValue(FLOOD, 1.0);
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
				gossipResponseContent.add(riskPerceptions.get(FLOOD));
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
			
			if ( tmpRiskPerception > riskPerceptions.get(FLOOD) ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(HIGHER) / rpTotal ) 
					riskPerceptions.setValue(FLOOD, tmpRiskPerception);
				rpTotals.setValue(HIGHER, (int) rpTotals.getValue(HIGHER) +1 );
			}
			
			if ( tmpRiskPerception == riskPerceptions.get(FLOOD) ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(EQUAL) / rpTotal ) 
					riskPerceptions.setValue(FLOOD, tmpRiskPerception);
				rpTotals.setValue(EQUAL, (int) rpTotals.getValue(EQUAL) +1 );
			}
			
			if ( tmpRiskPerception < riskPerceptions.get(FLOOD) ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(LOWER) / rpTotal ) 
					riskPerceptions.setValue(FLOOD, tmpRiskPerception);
				rpTotals.setValue(LOWER, (int) rpTotals.getValue(LOWER) +1 );
			}
		}
		
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean experiencedRiskEvent(int riskType)
	{
		if ( model.randomGenerator.nextDouble(true, true) < model.getObjectiveRisk(riskType) )
			return true;
		else
			return false;
	}

}

