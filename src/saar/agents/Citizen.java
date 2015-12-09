package saar.agents;

/**
 * @author QuispelL
 *
 */


import sim.engine.*;
import sim.field.network.*;
import sim.util.*;
import saar.*;

public class Citizen extends Agent  {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final long serialVersionUID = 1L;
	
 
	
	private int opinionDynamic;
	private DoubleBag riskSignal;
	private IntBag rpTotals;
	private int eventMemory; 
	
	public Bag getIncomingQueue() { return incomingQueue;}
	public void setIncomingQueue(Bag incomingQueue) {this.incomingQueue = incomingQueue;}
	public Bag getOutgoingQueue() {return outgoingQueue;}
	public void setOutgoingQueue(Bag outgoingQueue) {this.outgoingQueue = outgoingQueue;}
	
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
		opinionDynamic = 0;
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
		
		// check whether the agent experiences or remembers a risk event
		if ( eventMemory > 0 ) 
			eventMemory--;
		else
			if ( experiencedRiskEvent(Saar.FLOOD) ) {
				eventMemory = model.getEventMemory();
				riskPerceptions.setValue(Saar.FLOOD, 1.0);
				model.census.log("! Risk Event Experienced by agent " + String.valueOf(agentID) + " ");
			}
			
		// communicate with peers. 
		// method dependent on chosen opinion dynamic
		switch ( opinionDynamic ) {
			case ( Saar.ONGGO ):
				queryFriendsRiskPerception();
				processMessages(); 
				break;
			default:
				// TODO: determine default opinion dynamic
				break;
			
		}
		processMessages(); // need to call it twice to make sure all queries are answered
			
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
			case "rprequest":
				// risk perception query received; return risk perception in reply
				Message rpResponse = new Message(agentID,"rpresponse");
				Bag rpResponseContent = new Bag();
				for ( int i = 0 ; i < riskPerceptions.size() ; i++)
					rpResponseContent.add(riskPerceptions.get(i));
				rpResponse.setContent(rpResponseContent);
				sendMessage(rpResponse);
				break;
			case "rpresponse":
				// response to risk perception query received; store information in risk signal
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
	public void queryFriendsRiskPerception()
	{
		Message rpRequest = new Message(agentID,"rprequest");
		
		// add neighbours to receivers of gossip query
		Bag neighbours = model.getFriends().getEdgesOut(this);
		for (int i = 0 ; i < neighbours.size(); i++ ) 
			rpRequest.addReceiver( (Citizen) ((Edge) neighbours.get(i)).getOtherNode(this));
		
		// send gossip query directly (no need to use outgoing queue here) 
		sendMessage(rpRequest);
		
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
				
			// change risk perception with probability based on occurens of Saar.LOWER and Saar.HIGHER risk perception
			int rpTotal = rpTotals.get(Saar.HIGHER) + rpTotals.get(Saar.EQUAL) + rpTotals.get(Saar.LOWER);
			
			if ( tmpRiskPerception > riskPerceptions.get(Saar.FLOOD) ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(Saar.HIGHER) / rpTotal ) 
					riskPerceptions.setValue(Saar.FLOOD, tmpRiskPerception);
				rpTotals.setValue(Saar.HIGHER, (int) rpTotals.getValue(Saar.HIGHER) +1 );
			}
			
			if ( tmpRiskPerception == riskPerceptions.get(Saar.FLOOD) ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(Saar.EQUAL) / rpTotal ) 
					riskPerceptions.setValue(Saar.FLOOD, tmpRiskPerception);
				rpTotals.setValue(Saar.EQUAL, (int) rpTotals.getValue(Saar.EQUAL) +1 );
			}
			
			if ( tmpRiskPerception < riskPerceptions.get(Saar.FLOOD) ) {
				if ( model.randomGenerator.nextDouble() < rpTotals.get(Saar.LOWER) / rpTotal ) 
					riskPerceptions.setValue(Saar.FLOOD, tmpRiskPerception);
				rpTotals.setValue(Saar.LOWER, (int) rpTotals.getValue(Saar.LOWER) +1 );
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

