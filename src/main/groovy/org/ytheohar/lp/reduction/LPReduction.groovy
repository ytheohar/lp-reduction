package org.ytheohar.lp.reduction

import groovy.transform.CompileStatic;
import groovy.util.logging.Log;

import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException;
import org.ytheohar.lp.model.LPFunction
import org.ytheohar.lp.model.LPInstance
import org.ytheohar.lp.model.LPSolver
import org.ytheohar.lp.model.Solution
import org.ytheohar.lp.solver.ApacheSimplexSolver

/**
 * 
 * This class models the reduction of a problem P:D->R into linear programming (LP).
 * For a domain object x of type D, P(x) of type R is computed by a) mapping x to an LP instance y,  
 * b) solving y and c) mapping the solution LP(y) to an object of the range type R
 *
 * @param <D> P domain type (type of x)
 * @param <R> P range type (type of P(x))
 * 
 * @author Yannis Theocharis <ytheohar@gmail.com>
 */
class LPReduction<D, R> {
	Closure vars
	Closure objFunc
	Closure constraints
	Closure result
	// default is maximisation problem
	boolean max = true
	// default is to allow negative solution values
	boolean nonNegative = false
	LPSolver solver
	
	// caches the variables to avoid calling 'vars' closure more than once, 
	// in case 'constraints' closure needs this info.
	def variables

	/**
	 * Provides an algorithm for P, by reducing P to LP
	 * 
	 * @param x the domain object
	 * @return the reduced LP instance y
	 */
	LPInstance reduce(D x) {
		variables = vars(x)
		new LPInstance(variables,
				objFunc ? objFunc(x) : new LPFunction([:], 0),
				constraints(x),
				max,
				nonNegative)
	}

	/**
	 * Solves the specified LP instance. 
	 * If no solver is set, it uses ApacheSimplexSolver by default.
	 * 
	 * @param lpi the LP instance to solve
	 * @return the solution of the LP instance
	 */
	private Solution solve(LPInstance lpi) {
		if (!solver) {
			solver = new ApacheSimplexSolver()
		}
		solver.solve(lpi)
	}

	/**
	 * Computes P(x) by reducing P to LP
	 * 
	 * @param x the domain object
	 * @return P(x)
	 */
	R reduceAndSolve(D x) {
		LPInstance y = reduce(x)
		Solution solution = solve(y)
		result(solution)
	}
}