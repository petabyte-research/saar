/**
 * 
 */
package saar;

/**
 * @author QuispelL
 *
 */
public class RiskSignal {

///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int senderID;
	private int riskType;
	private double risk; 
	
	public int getSenderID() { return senderID; }
	public void setSenderID(int senderID) { this.senderID = senderID; }
	public int getRiskType() { return riskType; }
	public void setRiskType(int riskType) { this.riskType = riskType; }
	public double getRisk() { return risk; }
	public void setRisk(double risk) { this.risk = risk; }

	/**
	 * 
	 */
	public RiskSignal(int SenderID, int RiskType, double Risk ) { 
		
		senderID = SenderID;
		riskType = RiskType;
		risk = Risk;
	}

}
