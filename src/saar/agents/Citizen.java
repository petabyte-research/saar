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
	
	// opinion dynamics
	public static final int ONGGO = 0;	
 
	
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
		initCitizen();
		riskPerceptions.add(initialFirstRP);
		riskSignalQueue.add( new Bag() );
		
	}
	
	/**
	 * 
	 * @param id
	 * @param initialRP
	 */
	public Citizen(int id, DoubleBag initialRP)
	{
		super(id);
		initCitizen();
		agentID = id;
		for ( int i = 0 ; i < initialRP.size() ; i++) {
			riskPerceptions.add(initialRP.get(i));
			riskSignalQueue.add( new Bag() );
		}
	
	}
	
	
	/**
	 * 
	 */
	private void initCitizen()
	{
		opinionDynamic = 0;
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
		for ( int i = 0 ; i < riskSignalQueue.size() ; i++ )
			( (Bag) riskSignalQueue.get(i)).clear();
		
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
					riskSignalQueue.add(new RiskSignal(message.getSender(),i,RiskSignal.CITIZEN,(double) message.getContent().get(i) ));
				break;
			case "riskbroadcast":
				for ( int i = 0 ; i < message.getContent().size() ; i++ )
					riskSignalQueue.add(new RiskSignal(message.getSender(),i,RiskSignal.MEDIUM,(double) message.getContent().get(i) ));
				break;
			default:
				// TODO: handle unknown performative
				System.out.println("Unknown Performative in Message !!!" + message.getPerformative() + "+" );
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
		
	public void calculateOnggoRP()
	{
		Bag riskSignalBag = (Bag) riskSignalQueue.get(0);
	
		if ( riskSignalBag.size() != 0 ) 
		{ 
			// select risk perception of random citizen neighbour
			RiskSignal tmpSignal ;
			Double tmpRiskPerception;
			do {
				tmpSignal = (RiskSignal) riskSignalBag.get(model.randomGenerator.nextInt( riskSignalBag.size() ));
				tmpRiskPerception = tmpSignal.getRisk();
			} while ( tmpSignal.getSource() != RiskSignal.CITIZEN )  ;
				
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

