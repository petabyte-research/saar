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
		int xPos = 1;
		int yPos = 0;
		
		for(int i = 0; i < numCitizens; i++)
		{
			Citizen citizen = new Citizen();
			
			// spread citizens over the area
			
			if ( xPos < 100 ) 
				xPos = xPos + 3;
			else {
				xPos = 0;
				yPos = yPos + 3;
			}
			area.setObjectLocation(citizen, new Double2D(xPos, yPos));				
				
			// add citizen to social network and schedule 
			friends.addNode(citizen);
			schedule.scheduleRepeating(citizen);
		}
		
		
		// create edges in social network
		
		String networkType = "WattsBeta";
		switch ( networkType ) 
		{
			case "Lattice":
				createNetworkLattice();
				break;
			case "WattsBeta":
				createNetworkWattsStrogatz(8, 0.25);
				break;
			default:
				// TODO: add default social network
				break;
				
		}		
	}
	
	/**
	 * @param networkType
	 */
	
	public void createNetworkLattice()
	{

		Bag citizens = friends.getAllNodes();

		for(int i = citizens.size() - 1 ; i > 0 ; i--)
		{
			Object citizen1 = citizens.get(i);
			Object citizen2 = citizens.get(i-1);
			friends.addEdge(citizen1, citizen2, 1.0);
		}
	}
	
	/**
	 * @param degree
	 * @param beta
	 */
	
	public void createNetworkWattsStrogatz(int degree, double beta) 
	{
		Bag citizens = new Bag(friends.getAllNodes()); // create copy to be sure the Bag doesn't change or gets garbage collected
		Bag neighbours = new Bag();
		
		for (int i = 0 ; i < citizens.size() ; i++  )
		{
			Object citizen = citizens.get(i);
			Double2D pos = area.getObjectLocation(citizen);
			
			// get degree neigbours
			neighbours = area.getNearestNeighbors(pos, degree, false, false, false, neighbours);
			
			// wire neighbours and/or random node		
			Object acquaintance = new Object();
			for ( int n = 0; n < neighbours.size() ; n++ )
			{
				Object neighbour = neighbours.get(n);
				if ( random.nextDouble() < beta ) 
				{
					System.out.print(" !!! hit beta ...  "); 
					// wire with random node with probability beta
					// TODO: this is not entirely correct; a node has to be randomly selected from a set without already wired nodes and neighbour
					do {
						int tmp = random.nextInt(numCitizens);
						acquaintance = citizens.get(tmp);
					}
					while ( citizen == acquaintance || neighbour == acquaintance || friends.getEdge(citizen, acquaintance) == null );
					friends.addEdge(citizen,acquaintance,1.0);
				} 
				else
				{
					// otherwise, just wire neighbour (if not wired already) 
					if ( friends.getEdge(citizen, neighbour) == null )
						friends.addEdge(citizen,neighbour,1.0);
				}
			
			}
		}
		
	}
	

	/**
	 * @param args
	 */
	
	public static void main(String[] args) 
	{
		
		doLoop(Saar.class, args);
		System.exit(0);

	}

}
