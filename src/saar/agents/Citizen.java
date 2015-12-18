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
	
	// opinion dynamics constants
	public static final int DEGROOT = 0;
	public static final int HEGSELMANN = 1;
	public static final int DEFFUANT = 2; 
	public static final int AVERAGE_NETWORK_NEIGHBOUR = 3;
	public static final int ONGGO = 4;	
 
	
	private int opinionDynamic;
	private Bag riskSignalQueue;
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
	public Citizen(int id, int OpinionDynamic) 
	{
		super(id);
		initCitizen(OpinionDynamic);
	}
	
	/**
	 * 
	 * @param id
	 * @param initialFirstRP
	 */
	public Citizen(int id, int OpinionDynamic, Double initialFirstRP)
	{
		super(id);
		initCitizen(OpinionDynamic);
		riskPerceptions.add(initialFirstRP);
	}
	
	/**
	 * 
	 * @param id
	 * @param initialRP
	 */
	public Citizen(int id, int OpinionDynamic, DoubleBag initialRP)
	{
		super(id);
		initCitizen(OpinionDynamic);
		agentID = id;
		for ( int i = 0 ; i < initialRP.size() ; i++) 
			riskPerceptions.add(initialRP.get(i));
	}
	
	
	/**
	 * 
	 */
	private void initCitizen(int OpinionDynamic)
	{
		opinionDynamic = OpinionDynamic;
		riskSignalQueue = new Bag();
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
		riskSignalQueue.clear();
		
		// check whether the agent experiences or remembers a risk event
		if ( eventMemory > 0 ) 
			eventMemory--;
		else
			if ( experiencedRiskEvent(Saar.FLOOD) ) {
				eventMemory = model.getEventMemory();
				riskPerceptions.setValue(Saar.FLOOD, 1.0);
				model.census.log("! Risk Event Experienced by agent " + String.valueOf(agentID) + " ");
			} 
		
		// decrease risk perception slightly after event memory period (so portrayel will color the agent red instead of yellow)
		if ( riskPerceptions.get(Saar.FLOOD) == 1.0 )
			if ( eventMemory == 0 )
				riskPerceptions.setValue(Saar.FLOOD, 0.99);
			
		// communicate with peers. 
		// method dependent on chosen opinion dynamic
		switch ( opinionDynamic ) {
			case ( AVERAGE_NETWORK_NEIGHBOUR ):  
			case ( ONGGO ):
				queryFriendsRiskPerception();
				processMessages(); 
				break;
			default:
				// TODO: determine default opinion dynamic
				break;
			
		}
		
		// process messages in incoming queue
		processMessages(); // need to call it twice to make sure all queries are answered
					
		// determine risk perception
		// method dependent on chosen opinion dynamic
		// TODO: see if we can do it all in one switch statement
		switch ( opinionDynamic ) {
			case ( AVERAGE_NETWORK_NEIGHBOUR ):
				calculateRiskSignalAverageRP();
				break;
			case ( ONGGO ):
				calculateOnggoRP();
				break;
			default:
				// TODO: determine default opinion dynamic
				break;
			
		}
		
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
				for ( int i = 0 ; i < message.getContent().size() ; i++ )
					riskSignalQueue.add(new RiskSignal(message.getSender(),i,(double) message.getContent().get(i) ));
				break;
			case "riskbroadcast":
				for ( int i = 0 ; i < message.getContent().size() ; i++ )
					riskSignalQueue.add(new RiskSignal(message.getSender(),i,(double) message.getContent().get(i) ));
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
	
	public void calculateRiskSignalAverageRP()
	{
		int riskSignalSize = riskSignalQueue.size();
		
		// only process risk signal if it contains risk information
		if ( riskSignalSize > 0 ) {
			riskPerceptions.clear();
			rpTotals.clear();
			RiskSignal riskSignal;
			
			// sum risk perceptions per risk type
			for ( int i = 0 ; i < riskSignalSize ; i++)
			{ 
				riskSignal = (RiskSignal) riskSignalQueue.get(i);
				riskPerceptions.setValue(riskSignal.getRiskType(), riskPerceptions.get(riskSignal.getRiskType()) + riskSignal.getRisk());
				rpTotals.setValue(riskSignal.getRiskType(), rpTotals.getValue(riskSignal.getRiskType() + 1) );
			}
			
			// calculate average of risk perceptions
			for ( int i = 0 ; i < riskPerceptions.size() ; i++)
				riskPerceptions.setValue(i, riskPerceptions.get(i) / rpTotals.get(i) );
		}
	}
	
	/**
	 * 
	 * 
	 */
		
	public void calculateOnggoRP()
	{
	
		if ( riskSignalQueue.size() != 0 ) 
		{ 
			// select risk perception of random neighbour
			Double tmpRiskPerception = ((RiskSignal) riskSignalQueue.get(model.randomGenerator.nextInt( riskSignalQueue.size() ) )).getRisk() ;
				
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

