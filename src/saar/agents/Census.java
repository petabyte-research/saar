/**
 * 
 */
package saar.agents;

import java.util.*;
import java.text.*;
import java.io.*;
import java.nio.file.*;
import static java.nio.file.StandardOpenOption.*;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Bag;
import saar.*;



/**
 * @author QuispelL
 *
 */


public class Census implements Steppable 
{
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Properties and constructors
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static final long serialVersionUID = 1L;
	
	private Double averageRiskPerception;
	private String logFileName;
	private BufferedWriter writer;
	
	public Double getAverageRiskPerception() { return averageRiskPerception ; }
	
	public Census()
	{
		averageRiskPerception = 0.0;
		
		// initialize log file
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
 	   	Date date = new Date();
 	    logFileName =  dateFormat.format(date) + ".log";
        try {
             writer = new BufferedWriter(new FileWriter(logFileName));
             writer.write(logFileName + "\n");
         } catch (IOException e) {
        	 System.out.println(e);
         }
				
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Methods 
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */

	public void step(SimState state)
	{
		// calculate average risk perception
		Saar model = (Saar) state;
		Bag citizens = new Bag(model.getFriends().getAllNodes());
		int numberOfCitizens = citizens.size();
		averageRiskPerception = 0.0;
		
		for(int i = 0 ; i < numberOfCitizens ; i++)
			averageRiskPerception = averageRiskPerception + ((Citizen) citizens.get(i)).getRiskPerception();
		averageRiskPerception = averageRiskPerception / numberOfCitizens;
		
		// write data to file
		try 
		{
			writer.write(averageRiskPerception.toString() + "\n");
		}
		catch (IOException e) {
				// TODO: handle file error
		}
		
	}
	
	public void log(String logString)
	{
		System.out.println(logString);
		try {
			writer.write(logString);
		}
		catch (IOException e) {
			System.out.println(e);
		}
	
	}

}


