package org.ytheohar.lp.model

import java.util.List

import groovy.transform.CompileStatic

@CompileStatic
interface LPSolver {
	
	/**
	 * Solves the specified LP instance
	 * 
	 * @param lpi the LP instance to solve
	 * @return the solution of the LP instance
	 */
	Solution solve(LPInstance lpi)	
}