/**
 * 
 */
package saar;

/**
 * @author quispell
 *
 */
import sim.engine.*;
import sim.util.*;
import sim.field.continuous.*;
import sim.field.network.*;
import saar.agents.*;


public class Saar extends SimState
{
	
	
	public Continuous2D area = new Continuous2D(1.0,100,100);
	public int numCitizens = 1000;
	public Network friends = new Network(false);
	
	/**
	 * @param seed
	 */
	public Saar(long seed) {
		
		super(seed);
	}
	
	/**
	 * 
	 */
	
	public void start()
	{
		super.start();
		
		// clear area
		area.clear();

		// add citizens
		for(int i = 0; i < numCitizens; i++)
		{
			Citizen citizen = new Citizen();
			
			area.setObjectLocation(citizen, new Double2D(area.getWidth() * 0.5 + random.nextDouble() - 0.5, area.getHeight() * 0.5 + random.nextDouble() - 0.5));
			
			// add citizen to social network and schedule 
			friends.addNode(citizen);
			schedule.scheduleRepeating(citizen);

		}
		
		createSocialNetwork("Lattice1D");
	}
	
	/**
	 * @param networkType
	 */
	
	public void createSocialNetwork(String networkType)
	{

		Bag citizens = friends.getAllNodes();
		
		switch ( networkType ) {
			case "Lattice1D":
				for(int i = citizens.size() - 1 ; i > 0 ; i--)
				{
					Object citizen1 = citizens.get(i);
					Object citizen2 = citizens.get(i-1);
					friends.addEdge(citizen1, citizen2, 1.0);
				}
				
				break;
			case "WattsBetaSmallWorld":
				
				break;
			default:
				break;
				
		
		}
		

	
	}

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		
		doLoop(Saar.class, args);
		System.exit(0);

	}

}
