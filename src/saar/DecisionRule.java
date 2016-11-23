package saar;

import saar.agents.Agent;
import sim.util.*;
import java.lang.reflect.*;

/**
 * @author QuispelL
 *
 */

public class DecisionRule {
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Static constants
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public static final int OR_RULE = 0;   // default 
	public static final int AND_RULE = 1;
	public static final int XOR_RULE = 2;
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Properties and constructors
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	protected Agent decider;
	protected IntBag antecedents;
	protected DoubleBag thresholds; 
	protected Method consequent;
	protected int type;

	public void setType(int Type) { type = Type ; }
	public int getType() { return type ; }
	
	/**
	 * 
	 * @param Decider
	 * @param MemoryIndex
	 * @param Threshold
	 * @param ConsequentMethodName
	 */
	public DecisionRule(Agent Decider, int MemoryIndex, Double Threshold, String ConsequentMethodName)
	{
		// constructor for simple (1 antecedent) rule 
		
		initDecisionRule(Decider);
		addAntecedent(MemoryIndex,Threshold);
		setConsequent(ConsequentMethodName);
	
	}
	
	/**
	 * 
	 * @param Decider
	 */
	public DecisionRule(Agent Decider)
	{
		initDecisionRule(Decider);
	}
	
	/**
	 * 
	 * @param Decider
	 * @param Type
	 */
	public DecisionRule(Agent Decider, int Type)
	{
		initDecisionRule(Decider);
		setType(Type);
	}
	
	/**
	 * 
	 * @param Decider
	 */
	private void initDecisionRule(Agent Decider)
	{
		decider = Decider;
		setType(0);
		antecedents = new IntBag();
		thresholds = new DoubleBag();
		consequent = null;
	}
	
///////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//Methods
//
////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 
	 * @param MemoryIndex
	 * @param Threshold
	 */
	public void addAntecedent(int MemoryIndex, Double Threshold)
	{
		antecedents.add(MemoryIndex);
		thresholds.add(Threshold);
		
	}
	
	/**
	 * 
	 * @param MethodName
	 * @return
	 */
	public Boolean setConsequent(String MethodName)
	{
		try {
			consequent = decider.getClass().getMethod(MethodName, null);
		}
		catch (NoSuchMethodException e) {
			System.out.println(e);
			return false;
		}
	
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean execute()
	{
		try { 
			if ( this.evaluate() ) {
				consequent.invoke(decider);  
				return true;
			}
			else
				return false;
		}
		catch (IllegalAccessException e) {
			System.out.println(e);
			return false;
		} 
		catch (InvocationTargetException e) {
			System.out.println(e);
			return false;
		}
		catch( Exception e ) {
			System.out.println(e);
			return false;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Boolean evaluate()
	{
		Boolean ret = false;
		
		try { 
			// evaluate first antecedent
			if ( decider.getRiskPerception( antecedents.get(0) ) > thresholds.get(0) )
				ret = true;
			
			// if there is only 1 antecedent, we can return 
			// if there are more antecedents, process them
			for (int i = 1 ; i < antecedents.size() ; i++ ) {
				switch ( type )
				{
					case OR_RULE:  
						// if ret is true, no need to evaluate further
						// if ret is false, check whether it needs to be made true
						if ( ret != true )
							if ( decider.getRiskPerception( antecedents.get(i) ) > thresholds.get(i) )
								ret = true;
						break;
					case AND_RULE:
						// if ret is false, no need to evaluate further
						// if ret is true, check whether it needs to be made false
						if ( ret == true )
							if ( decider.getRiskPerception(antecedents.get(i)) <= thresholds.get(i) )
								ret = false;
						break;
					case XOR_RULE:
						// if ret is true, evaluate whether it can stay true
						// if ret is false, evaluate whether it should be made true
						if ( decider.getRiskPerception(antecedents.get(i)) > thresholds.get(i) )
							if ( ret == true )
								ret = false;
							else
								ret = true;
						else
							if ( ret == true )
								ret = false;
						break;
				}
			}
		}
		catch ( Exception e) {
			System.out.println("Error while evaluationg decision rule." );
			System.out.println(e);
		}
		
		return ret;
		
	}
	
}
