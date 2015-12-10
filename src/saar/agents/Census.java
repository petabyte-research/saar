/**
 * 
 */
package saar.agents;

import java.util.*;
import java.text.*;
import java.io.*;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.*;
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
	
	private DoubleBag meanRiskPerception;
	private DoubleBag stDevRiskPerception;
	private int numberOfRisks;
	private String logFileName;
	private BufferedWriter writer;
	
	public DoubleBag getMeanRiskPerception() { return meanRiskPerception ; }
	public DoubleBag getStDevRiskPerception() { return stDevRiskPerception ; }
	public double getMeanRiskPerception( int RiskType ) { return meanRiskPerception.get(RiskType); } 
	public double getStDevRiskPerception( int RiskType ) { return stDevRiskPerception.get(RiskType); } 
	
	/**
	 * 
	 * @param NumberOfRisks
	 */
	public Census(int NumberOfRisks)
	{
		numberOfRisks = NumberOfRisks;
		meanRiskPerception = new DoubleBag(numberOfRisks);
		stDevRiskPerception = new DoubleBag(numberOfRisks);
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
		Bag citizens = new Bag(model.getFriends().getAllNodes()); // TODO: check whether this is needed
		int numberOfCitizens = citizens.size();
		Double value;
		Double mCurrent;
		Double sD;
		Double mPrevious;
		
		for (int n = 1; n < numberOfRisks ; n++) {
			meanRiskPerception.setValue(n, ((Citizen) citizens.get(0)).getRiskPerception(n));
			mPrevious = meanRiskPerception.get(n);
			mCurrent = 0.0;
			sD = 0.0;
			for(int i = 1 ; i < numberOfCitizens ; i++) {
				value = ((Citizen) citizens.get(i)).getRiskPerception(n);
				meanRiskPerception.setValue(n, meanRiskPerception.get(n) + value);
				mCurrent = mPrevious + ( value - mPrevious ) / i;
				sD =  sD + ( value - mPrevious ) * ( value - mCurrent );
				mPrevious = mCurrent;
			}
			meanRiskPerception.setValue(n,  meanRiskPerception.get(n)  / numberOfCitizens  ) ;
			stDevRiskPerception.setValue(n, Math.sqrt( sD/ numberOfCitizens ))  ;
		}
	

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




