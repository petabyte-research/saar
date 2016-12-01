/**
 * 
 */
package saar.agents;

/**
 * @author QuispelL
 *
 */
public class Connection implements Link {
	
		
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties, constructors 
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private int id;
	private int type;
	private double weight;
	
	/* (non-Javadoc)
	 * @see saar.agents.Link#getType()
	 */
	@Override
	public int getType() { return type ; }
	
	
	/* (non-Javadoc)
	 * @see saar.agents.Link#getID()
	 */
	@Override
	public int getID() { return id ; }
	
	
	/* (non-Javadoc)
	 * @see saar.agents.Link#getWeight()
	 */
	@Override
	public double getWeight() { return weight ; }
	
	
	/* (non-Javadoc)
	 * @see saar.agents.Link#setWeight(double)
	 */
	
	@Override
	public void setWeight( double Weight ) { weight = Weight ; } 
	
	/**
	 * 
	 */
	public Connection(int ID, int ConnectionType)
	{
		id = ID;
		type = ConnectionType;
		weight = 0.0;
	}

}
