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
	private DoubleBag maximumRiskPerception;
	private DoubleBag minimumRiskPerception; 
	private DoubleBag upperRiskPerceptionInterval;
	private DoubleBag lowerRiskPerceptionInterval;
	private int numberOfRisks;
	private Double objectiveRisk;

	private String logFileName;
	private BufferedWriter writer;
	private Boolean fileLogging;
	private Boolean consoleLogging;

	public DoubleBag getMeanRiskPerception() { return meanRiskPerception ; }
	public DoubleBag getStDevRiskPerception() { return stDevRiskPerception ; }
	public double getMeanRiskPerception( int RiskType ) { return meanRiskPerception.get(RiskType); } 
	public double getStDevRiskPerception( int RiskType ) { return stDevRiskPerception.get(RiskType); } 
	public double getMaximumRiskPerception( int RiskType ) { return maximumRiskPerception.get(RiskType); } 
	public double getMinimumRiskPerception( int RiskType ) { return minimumRiskPerception.get(RiskType); } 
	public double getUpperRiskPerceptionInterval( int RiskType ) { return upperRiskPerceptionInterval.get(RiskType ); }
	public double getLowerRiskPerceptionInterval( int RiskType ) { return lowerRiskPerceptionInterval.get(RiskType ); }
	
	public Boolean getFileLogging() { return fileLogging ; }
	public void setFileLogging(Boolean FileLogging) { fileLogging = FileLogging ; }
	public Boolean getConsoleLogging() { return consoleLogging ; }
	public void setConsoleLogging(Boolean ConsoleLogging) { consoleLogging = ConsoleLogging ; }
	
	/**
	 * 
	 * @param NumberOfRisks
	 */
	public Census(int NumberOfRisks, double ObjectiveRisk)
	{
		numberOfRisks = NumberOfRisks;
		objectiveRisk = ObjectiveRisk;
		meanRiskPerception = new DoubleBag(numberOfRisks);
		stDevRiskPerception = new DoubleBag(numberOfRisks);
		maximumRiskPerception = new DoubleBag(numberOfRisks);
		minimumRiskPerception = new DoubleBag(numberOfRisks);
		upperRiskPerceptionInterval = new DoubleBag(numberOfRisks);
		lowerRiskPerceptionInterval = new DoubleBag(numberOfRisks);
		try {
			for (int i = 0 ; i < numberOfRisks ; i++ ) {
				meanRiskPerception.add(0.0);
				stDevRiskPerception.add(0.0);
				maximumRiskPerception.add(0.0);
				minimumRiskPerception.add(0.0);
				upperRiskPerceptionInterval.add(1 - objectiveRisk);
				lowerRiskPerceptionInterval.add(1 - objectiveRisk);
			}
		}
		catch ( Exception e) {
			System.out.println(e);
		}
		
		fileLogging = false;
		consoleLogging = false;
	}
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//  Behavior
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param state 
	 */
	@Override public void step(SimState state)
	{
		Saar model = (Saar) state;
			
		// calculate average risk perception and its standard deviation (wellford algorithm)
		Collection citizens = model.getFriends().getVertices();
		Citizen zero = (Citizen) model.getAgent(0);
		int numberOfCitizens = citizens.size();
		Double value;
		Double mCurrent;
		Double sD;
		Double mPrevious;
		
		try {
			for (int n = 0; n < numberOfRisks ; n++) {
				// initialize
				meanRiskPerception.setValue(n, zero.getRiskPerception(n));
				maximumRiskPerception.setValue(n, zero.getRiskPerception(n));
				minimumRiskPerception.setValue(n, zero.getRiskPerception(n));
				mPrevious = meanRiskPerception.get(n);
				mCurrent = 0.0;
				sD = 0.0;
				// get data from all citizens
				for( Iterator iter = citizens.iterator(); iter.hasNext() ; ) {
					value = ( (Citizen) iter.next() ).getRiskPerception(n);
					
					// get min and max if applicable
					if (minimumRiskPerception.get(n) > value )
						minimumRiskPerception.setValue(n, value);
					else
						if ( maximumRiskPerception.get(n) < value )
							if ( value < 0.99  )
								maximumRiskPerception.set(n, value);
				
					// get values for mean and stdev calculation
					meanRiskPerception.setValue(n, meanRiskPerception.get(n) + value);
					mCurrent = mPrevious + ( value - mPrevious ) / numberOfCitizens;
					sD =  sD + ( value - mPrevious ) * ( value - mCurrent );
					mPrevious = mCurrent;
				}
				// calculate
				meanRiskPerception.setValue(n,  meanRiskPerception.get(n)  / numberOfCitizens  ) ;
				stDevRiskPerception.setValue(n, Math.sqrt( sD/ numberOfCitizens ))  ;
				upperRiskPerceptionInterval.setValue(n, maximumRiskPerception.get(n) - objectiveRisk  ); 
				lowerRiskPerceptionInterval.setValue(n, objectiveRisk - minimumRiskPerception.get(n) );
			}
		} 
		catch (Exception e ) {
			System.out.println("Census Calculation Error !!!");
			System.out.println(e);
		}
	
		// write data to file, if required
		if ( fileLogging )
			try 
			{
				writer.newLine();
				writer.write( String.valueOf(model.schedule.getSteps()) );
				for ( int i = 1 ; i < numberOfRisks ; i++ ) {
					writer.write("," + meanRiskPerception.getValue(i).toString());  
					writer.write("," + stDevRiskPerception.getValue(i).toString());
					writer.write("," + maximumRiskPerception.getValue(i).toString() );
					writer.write("," + minimumRiskPerception.getValue(i).toString() );
				}
			}
			catch (Exception e) {
					System.out.println(e);
			}
		
		// write data to stdout, if required
		if ( consoleLogging ) {
			System.out.println("-> Step " + String.valueOf(model.schedule.getSteps()) + ":");
			for ( int i = 1 ; i < numberOfRisks ; i++ ) {
				System.out.print("Risk ");
				System.out.print(String.valueOf(i));
				System.out.print("; Mean: " + meanRiskPerception.getValue(i).toString());
				System.out.print(", StDev: " + stDevRiskPerception.getValue(i).toString());
				System.out.print(", Max: " + maximumRiskPerception.getValue(i).toString());
				System.out.println(", Min: " + minimumRiskPerception.getValue(i).toString() );
			
			}
		}
			
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	//  Logging 
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	
	/**
	 * 
	 */
	public void initializeLogFile(String LogFile)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
 	   	Date date = new Date();
 	   	logFileName = LogFile;
 	    logFileName += dateFormat.format(date);
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
	 * @param logString
	 */
	public void log(String logString)
	{
		if ( consoleLogging )
				System.out.println(logString);
		if ( fileLogging )
			try {
					writer.write(","+ logString);
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




