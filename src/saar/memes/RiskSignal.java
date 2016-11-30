/**
 * 
 */
package saar.memes;

import sim.util.*;

/**
 * @author QuispelL
 *
 */
public class RiskSignal {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Static constants
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final int POLARITY = 0;
	public static final int OBJECTIVITY = 1;
	public static final int ANGER = 2;
	public static final int CONFUSION = 3;
	public static final int TENSION = 4;
	public static final int VIGOR = 5;
	public static final int DEPRESSION = 6; 
 	public static final int FATIGUE = 7;
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int senderID;
	private int riskType;
	private double risk; 
	private Bag sentimentVector;
	
	public int getSenderID() { return senderID; }
	public void setSenderID(int senderID) { this.senderID = senderID; }
	public int getRiskType() { return riskType; }
	public void setRiskType(int riskType) { this.riskType = riskType; }
	public double getRisk() { return risk; }
	public void setRisk(double risk) { this.risk = risk; }
	public Bag getSentimentVector() { return sentimentVector ; }
	public void setSentimentVector( Bag SentimentVector ) { sentimentVector = SentimentVector ; } 
	public double getSentiment ( int sentimentType ) { return (double) sentimentVector.get(sentimentType) ; } 

	/**
	 * 
	 * @param SenderID
	 * @param RiskType
	 * @param Risk
	 */
	public RiskSignal(int SenderID, int RiskType, double Risk ) { 
		
		senderID = SenderID;
		riskType = RiskType;
		risk = Risk;
	}
	
	/**
	 * 
	 * @param SenderID
	 * @param RiskType
	 * @param Risk
	 */
	public RiskSignal(int SenderID, int RiskType, double Risk, Bag SentimentVector ) { 
		
		senderID = SenderID;
		riskType = RiskType;
		risk = Risk;
		sentimentVector = SentimentVector;
	}

}
