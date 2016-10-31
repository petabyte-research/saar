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
//Static constants
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// opinion dynamics constants
	public static final int DEGROOT = 0;
	public static final int HEGSELMAN = 1;
	public static final int ONGGO = 10;	
 
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final long serialVersionUID = 1L;
	
	protected int opinionDynamic;
	private Bag riskSignalQueue;
	private IntBag rpTotals;
	private DoubleBag confidenceIntervalVector;
	protected int eventMemory; 
	
	public Bag getriskSignalQueue() { return riskSignalQueue;}
	
	/**
	 * 
	 * @param id
	 * @param Model
	 * @param OpinionDynamic
	 */
	public Citizen(int id, Saar Model, int OpinionDynamic, Double Confidence) 
	{
		super(id,Model);
		initCitizen(OpinionDynamic, Confidence,1);
	}
	
	/**
	 * 
	 * @param id
	 * @param Model
	 * @param OpinionDynamic
	 * @param initialFirstRP
	 */
	public Citizen(int id, Saar Model, int OpinionDynamic, Double initialFirstRP, Double Confidence)
	{
		super(id, Model);
		initCitizen(OpinionDynamic, Confidence,1);
		riskPerceptions.add(0.0);
		riskPerceptions.add(initialFirstRP);
	}
	
	/**
	 * 
	 * @param id
	 * @param Model
	 * @param OpinionDynamic 
	 * @param initialRP
	 */
	public Citizen(int id, Saar Model, int OpinionDynamic, DoubleBag initialRP, Double Confidence)
	{
		super(id, Model);
		initCitizen(OpinionDynamic, Confidence,initialRP.size());
		for ( int i = Saar.FLOOD ; i < initialRP.size() ; i++) 
			riskPerceptions.setValue(i,initialRP.get(i));
	}
	
	
	/**
	 * 
	 *  @param OpinionDynamic
	 */
	private void initCitizen(int OpinionDynamic, Double Confidence, int NumberOfRisks)
	{
		opinionDynamic = OpinionDynamic;
		riskSignalQueue = new Bag();
		confidenceIntervalVector = new DoubleBag(NumberOfRisks+1);
		rpTotals = new IntBag(NumberOfRisks+1);
		if ( opinionDynamic == DEGROOT )
			Confidence = 1.0;   // Bounded Confidence Model with unlimited confidence interval (1.0 = max risk) is De Groot Model
		for ( int i = 0 ; i < (NumberOfRisks + 1) ; i++ ) {
			rpTotals.add(1);
			confidenceIntervalVector.add(Confidence);
		}
		
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Behavior  
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 
	 */
	@Override public void step(SimState state) 
	{
		super.step(state);
		
		// reset risk signal
		riskSignalQueue.clear();
			
		// communicate with peers. 
		// method dependent on chosen opinion dynamic, yet to be implemented
		switch ( opinionDynamic ) {
			default:
				queryFriendsRiskPerception();
				break;
		}
		
		// check whether the agent remembers a risk event; if so, ignore incoming messages
		if ( eventMemory > 0 ) {
			// agent remembers risk event
			eventMemory--;
			emptyIncomingQueue();
		}
		else {
			// agent has forgotten risk event
			// Check whether agent experiences risk event
			if ( experiencedRiskEvent(Saar.FLOOD) ) {
				// risk event experienced; set risk perception and log it
				emptyIncomingQueue();
				eventMemory = model.getEventMemory();
				riskPerceptions.setValue(Saar.FLOOD, 1.0);
				model.getCensus().log("! Risk Event Experienced by agent " + String.valueOf(agentID) + " ");
			} else   	// decrease risk perception slightly after event memory period (so portrayel will color the agent red instead of yellow)
				if ( riskPerceptions.get(Saar.FLOOD) == 1.0 )
					if ( eventMemory == 0 ) 
						riskPerceptions.setValue(Saar.FLOOD, 0.99);
			
			// determine risk perception
			// method dependent on chosen opinion dynamic
			processMessages();
			switch ( opinionDynamic ) {
				default:
					calculateAverageOpinion();
					break;
				
			}
		}
		
		//  Decide on actions
		for ( int i = 0 ; i < decisionRules.size() ; i++ )
			((DecisionRule) decisionRules.get(i)).execute();
	
	}

	/**
	 * 
	 * @param message
	 */
	@Override public void processMessage(Message message)
	{
		switch ( message.getPerformative() )
		{
			// TODO: consider changing performative to static ints and look at other static method possibilities 
			case "rprequest":
				// risk perception query received; return risk perception in reply
				Message rpResponse = new Message(this,"rpresponse");
				rpResponse.addReceiver( message.getSender() );
				Bag rpResponseContent = new Bag();
				Bag sentiment = new Bag();
				rpResponseContent.add(sentiment);
				for ( int i = Saar.FLOOD ; i < riskPerceptions.size() ; i++)
					rpResponseContent.add(riskPerceptions.get(i));
				rpResponse.setContent(rpResponseContent);
				sendMessage(rpResponse);
				break;
			case "rpresponse":
			case "riskbroadcast":
				// broadcast or response to risk perception query received; store information in risk signal queue
				for ( int i = Saar.FLOOD ; i < message.getContent().size() ; i++ )
					riskSignalQueue.add(new RiskSignal(message.getSender().getAgentID(),i,(double) message.getContent().get(i), (Bag ) message.getContent().get(0)  ));
				// TODO: add code to spread risk signal
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
	public void evacuate()
	{
		// TODO: implement method
		System.out.println("!!! Citizen " + agentID + " is evacuating, rp = " + getPrimaryRiskPerception() );
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//  Internal Processing
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void calculateAverageOpinion()
	{
		int riskSignalQueueSize = riskSignalQueue.size();
		
		// only process risk signal if it contains risk information
		if ( riskSignalQueueSize > 0 ) {
			resetRiskMentalModel();
			RiskSignal riskSignal;
			int riskType;
			// loop through all risk signals
			for ( int i = 0 ; i < riskSignalQueueSize ; i++) {
				riskSignal = (RiskSignal) riskSignalQueue.get(i);
				riskType = riskSignal.getRiskType();
				// check whether opinion is within confidence interval
				if ( Math.abs( riskPerceptions.get(riskType) - riskSignal.getRisk())  < confidenceIntervalVector.get(riskType) ) {
					riskPerceptions.setValue(riskSignal.getRiskType(), riskPerceptions.get(riskSignal.getRiskType()) + riskSignal.getRisk());
					rpTotals.setValue(riskSignal.getRiskType(), rpTotals.get(riskSignal.getRiskType()) + 1) ;
				}
			}
			// calculate average of risk perceptions (only needed if risk signal size > 0) 
			for ( int i = 0 ; i < rpTotals.size() ; i++)
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

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Auxiliary
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	public void queryFriendsRiskPerception()
	{
		Message rpRequest = new Message(this,"rprequest");
		
		// add neighbours to receivers of gossip query
		Bag neighbours = model.getFriends().getEdgesOut(this);
		for (int i = 0 ; i < neighbours.size(); i++ ) 
			rpRequest.addReceiver( (Citizen) ((Edge) neighbours.get(i)).getOtherNode(this));
		
		// send gossip query directly (no need to use outgoing queue here) 
		sendMessage(rpRequest);
		
	}
	
	/**
	 * @param riskType
	 * @return
	 */
	public boolean experiencedRiskEvent(int riskType)
	{
		if ( model.randomGenerator.nextDouble(true, true) < model.getObjectiveRisk(riskType) )
			return true;
		else
			return false;
	}
	
	/**
	 * 
	 */
	public void resetRiskMentalModel()
	{
		for (int i = 0 ; i < riskPerceptions.size() ; i++) 
			riskPerceptions.setValue(i, 0.0);
			
		for ( int i = 0 ; i < rpTotals.size() ; i++)
			rpTotals.setValue(i, 0);
		
	}

}

