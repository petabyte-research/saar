/**
 * 
 */
package saar.agents;

import saar.*;
import saar.memes.Message;
import sim.engine.SimState;
import sim.util.Bag;
import sim.util.DoubleBag;

/**
 * @author QuispelL
 *
 */
public class Medium extends Person {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Static constants
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// Media role
	public static final int OBJECTIVE = 0;
	public static final int LEADER_AVERAGE = 1;
	public static final int FOLLOWER = 2;
	
	public static final String OBJECTIVE_STRING = "Objective";
	public static final String LEADER_AVERAGE_STRING = "Leader Average";
	public static final String FOLLOWER_STRING = "Follower";
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected static final long serialVersionUID = 1L;
	
	protected int mediaRole;
	
	public int getMediaRole() { return mediaRole ; }
	public void setMediaRole(int MediaRole ) { mediaRole = MediaRole; } 
	public Object domMediaRole() { return new String[] { "OBJECTIVE", "LEADER_AVERAGE", "FOLLOWER" }; }

	/**
	 * 
	 * @param AgentID
	 * @param MediaRole
	 */
	public Medium(int AgentID, Saar Model, int MediaRole) 
	{
		super(AgentID,Model);
		initMedia(mediaRole); 
	}

	/**
	 * 
	 * @param AgentID
	 * @param MediaRole
	 * @param initialRP
	 */
	public Medium(int AgentID, Saar Model, int MediaRole, Double initialRP)
	{
		super(AgentID,Model);
		initMedia(mediaRole); 
		riskPerceptions.add(initialRP);
	}
	
	/**
	 * 
	 * @param AgentID
	 * @param MediaRole
	 * @param initialRP
	 */
	public Medium(int AgentID, Saar Model, int MediaRole, DoubleBag initialRP)
	{
		super(AgentID,Model);
		initMedia(mediaRole); 
		for ( int i = 0 ; i < initialRP.size() ; i++) 
			riskPerceptions.add(initialRP.get(i));
		
	}
	
	/**
	 * 
	 * @param MediaRole
	 */
	public void initMedia(int MediaRole)
	{
		mediaRole = MediaRole ; // TODO: check whether MediaRole is valid
	}
	
	
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Behavior  
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param state
	 */
	@Override public void step(SimState state) 
	{
		super.step(state);
		
		// query risk perception of citizens; we will use census because it already gather the required info:
		// query real risk; we will use Saar.objectiveRisk because we know it already 
		
		// determine message and broadcast 
		broadcast();
	}
	
	/**
	 * 
	 */
	public void broadcast()
	{
		Message broadcastMessage = new Message(this, "riskbroadcast");
		Bag rpResponseContent = new Bag();
		switch ( mediaRole ) {
			case OBJECTIVE:  // broadcast real risk
				for ( int i = 0 ; i < riskPerceptions.size() ; i++)
					rpResponseContent.add(model.getObjectiveRisk(i));
				break;
			case LEADER_AVERAGE: // broadcast average of real risk and public risk perception 
				for ( int i = 0 ; i < riskPerceptions.size() ; i++)
					rpResponseContent.add( (model.getCensus().getMeanRiskPerception(i) + model.getObjectiveRisk(i)) / 2);
				break;
			case FOLLOWER:	 // broadcast public risk perception
				for ( int i = 0 ; i < riskPerceptions.size() ; i++)
					rpResponseContent.add(model.getCensus().getMeanRiskPerception(i));
				break;
			default:
				model.getCensus().log("!!! mediaRole not recognized:");
				break;
		}
		
		broadcastMessage.setContent(rpResponseContent);
		// TODO: use JUNG to broadcast message over medianetwork
		/* broadcastMessage.setReceivers(  new Bag(model.getFriends().getAllNodes()) );
		sendMessage(broadcastMessage);*/
	}
	
}
