/**
 * 
 */
package saar.agents;

import java.util.*;
import java.text.*;
import java.io.*;
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
	
	private Double meanRiskPerception;
	private Double stDevRiskPerception;
	private String logFileName;
	private BufferedWriter writer;
	
	public Double getMeanRiskPerception() { return meanRiskPerception ; }
	public Double getStDevRiskPerception() { return stDevRiskPerception ; }
	
	/**
	 * 
	 */
	public Census()
	{
		meanRiskPerception = 0.0;
		stDevRiskPerception = 0.0;
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//  Methods 
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 */
	public void initializeLogFile()
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
 	   	Date date = new Date();
 	    logFileName = dateFormat.format(date);
        try {
             writer = new BufferedWriter(new FileWriter( logFileName + ".log" ));
             writer.write(logFileName + "\n");
             writer.newLine();
             writer.flush();
         } catch (IOException e) {
        	 System.out.println(e);
         }
	}
	
	/**
	 * 
	 */
	public void endSession()
	{
		log("\n Job ended.");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		log( dateFormat.format(date) );
		
	}
	
	
	/**
	 * 
	 */
	public void step(SimState state)
	{
		Saar model = (Saar) state;
			
		// calculate average risk perception and its standard deviation (wellford algorithm)
		Bag citizens = new Bag(model.getFriends().getAllNodes());
		int numberOfCitizens = citizens.size();
		meanRiskPerception = ((Citizen) citizens.get(0)).getRiskPerception(0);
		Double value;
		Double mCurrent = 0.0;
		Double sD = 0.0;
		Double mPrevious = meanRiskPerception;
		for(int i = 1 ; i < numberOfCitizens ; i++) {
			value = ((Citizen) citizens.get(i)).getRiskPerception(0);
			meanRiskPerception = meanRiskPerception + value;
			mCurrent = mPrevious + ( value - mPrevious ) / i;
			sD =  sD + ( value - mPrevious ) * ( value - mCurrent );
			mPrevious = mCurrent;
		}
		meanRiskPerception = meanRiskPerception / numberOfCitizens;
		stDevRiskPerception = Math.sqrt( sD/ numberOfCitizens );

		// write data to file
		try 
		{
			writer.newLine();
			writer.write( String.valueOf(model.schedule.getSteps()) + ",");
			writer.write(meanRiskPerception.toString());  
			writer.write(","+ stDevRiskPerception.toString());
			writer.write(",");
		}
		catch (IOException e) {
				System.out.println(e);
		}
		
	}
	
	/**
	 * 
	 * @param logString
	 */
	public void log(String logString)
	{
		System.out.println(logString);
		try {
			writer.write(logString);
			writer.flush();
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	
	/**
	 * 
	 */
	public void flush()
	{
		try {
			writer.flush();
		}
		catch (IOException e) {
			System.out.println(e);
		}
	}
	
}




