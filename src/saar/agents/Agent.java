package saar.agents;

import saar.Saar;
import saar.memes.DecisionRule;
import saar.memes.Message;
import sim.engine.SimState;
import sim.util.Bag;
import sim.util.DoubleBag;

public interface Agent {

	DoubleBag getRiskPerceptions();

	void setRiskPerceptions(double newRP);

	int getAgentID();

	Saar getModel();

	Bag getIncomingQueue();

	Bag getOutgoingQueue();

	double getPrimaryRiskPerception();

	void setPrimaryRiskPerception(double newRP);

	Object domPrimaryRiskPerception();

	/**
	 * 
	 * @param riskType
	 * @return
	 */
	double getRiskPerception(int riskType);

	/**
	 * 
	 */
	void initAgent(int iD);

	/**
	 * 
	 *  * @param state
	 */
	void step(SimState state);

	/**
	 * 
	 * @param message
	 */
	void receiveMessage(Message message);

	/**
	 * 
	 */
	void emptyIncomingQueue();

	void addRule(DecisionRule Rule);

}