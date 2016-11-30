/**
 * 
 */
package saar.agents;

import saar.Saar;

/**
 * @author QuispelL
 *
 */
public class AgentFactory {
	
	public static final int CITIZEN = 0;
	public static final int MEDIUM = 1;
	public static final int GOVERNOR = 3;
	public static final int EXPERT = 4; 
	
	private int counter; // TODO: check MAXINT !!!
	protected Saar model;
	

	/**
	 * 
	 */
	public AgentFactory(Saar Model) {
		counter = -1;
		model = Model;
	}
	
	public Agent getAgent(int Type)
	{
		counter++;
		switch (Type) {
			case CITIZEN :
				return getCitizen(counter);
			case MEDIUM :
				return getMedium(counter);
			case GOVERNOR:
				return getGovernor(counter);
			case EXPERT:
				return getExpert(counter);
			default:
				System.out.println("!!! AgentFactory: wrong type of agent !!! ");
				counter--;
				return null; // TODO: handle this better 

		}
	}
	
	/**
	 * 
	 * @param ID
	 * @return
	 */
	public Citizen getCitizen(int ID)
	{
		return new Citizen(ID, model, model.getAgentType(), model.getConfidence() )	;	
	}
	
	/**
	 * 
	 * @param ID
	 * @return
	 */
	public Medium getMedium(int ID)
	{
		return new Medium(ID, model, Medium.OBJECTIVE  )	; 
	}
	
	/**
	 * 
	 * @param ID
	 * @return
	 */
	public Governor getGovernor(int ID)
	{
		return new Governor(ID,model);
	}
	
	/**
	 * 
	 * @param ID
	 * @return
	 */
	public Expert getExpert(int ID)
	{
		return new Expert(ID,model);
	}

}
