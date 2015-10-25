package org.ytheohar.lp.solver

import groovy.util.logging.Log;

import java.util.Map;

import org.apache.commons.math3.linear.OpenMapRealVector
import org.apache.commons.math3.linear.RealVector
import org.apache.commons.math3.optim.MaxIter
import org.apache.commons.math3.optim.PointValuePair
import org.apache.commons.math3.optim.linear.LinearConstraint
import org.apache.commons.math3.optim.linear.LinearConstraintSet
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction
import org.apache.commons.math3.optim.linear.NoFeasibleSolutionException
import org.apache.commons.math3.optim.linear.NonNegativeConstraint
import org.apache.commons.math3.optim.linear.Relationship
import org.apache.commons.math3.optim.linear.SimplexSolver
import org.apache.commons.math3.optim.linear.UnboundedSolutionException
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.ytheohar.lp.model.ConstraintRelationship
import org.ytheohar.lp.model.LPConstraint
import org.ytheohar.lp.model.LPFunction
import org.ytheohar.lp.model.LPIState
import org.ytheohar.lp.model.LPInstance;
import org.ytheohar.lp.model.LPSolver
import org.ytheohar.lp.model.Solution

@Log
class ApacheSimplexSolver implements LPSolver {

	private static final MaxIter DEFAULT_MAX_ITER = new MaxIter(100)

	Map<Object, Integer> varToIndex

	Solution solve(LPInstance lpi) {
		registerVars(lpi.vars)
		LinearObjectiveFunction f = toApacheFunction(lpi.objectiveFunction)
		List<LinearConstraint> apacheConstraints = lpi.constraints.collect{
			toApacheConstraint(it)
		}
		GoalType goalType = lpi.max ? GoalType.MAXIMIZE : GoalType.MINIMIZE

		SimplexSolver solver = new SimplexSolver()
		PointValuePair solution
		LPIState state = LPIState.FEASIBLE
		try {
			solution = solver.optimize(DEFAULT_MAX_ITER, f, new LinearConstraintSet(apacheConstraints),
			goalType, new NonNegativeConstraint(lpi.nonNegative))
		} catch (NoFeasibleSolutionException e) {
			state = LPIState.NOT_FEASIBLE
			log.info e.message
		} catch (UnboundedSolutionException e) {
			state = LPIState.UNBOUNDED
			log.info e.message
		}

		Map<Object, Double> at
		if (state == LPIState.FEASIBLE) {
			at = varToIndex.collectEntries {
				var, index ->
				[(var): solution?.point[index]]
			}
		}

		new Solution(solution?.value, at, state)
	}

	private void registerVars(List<Object> vars) {
		int i=0
		varToIndex = vars.collectEntries {
			[(it): i++]
		}
	}

	private LinearObjectiveFunction toApacheFunction(LPFunction f) {
		RealVector v = toVector(f.coeffMap)
		new LinearObjectiveFunction(v, f.constant)
	}

	private RealVector toVector(Map<Object, Double> coeffMap) {
		RealVector v = new OpenMapRealVector(varToIndex.size())
		coeffMap.each { e, coef ->
			v.setEntry(varToIndex[e], coef)
		}
		v
	}

	private LinearConstraint toApacheConstraint(LPConstraint c) {
		RealVector v = toVector(c.function.coeffMap)
		def rel = toApacheRelationship(c.relationship)
		new LinearConstraint(v, rel, c.constant)
	}

	private Relationship toApacheRelationship(ConstraintRelationship rel) {
		if (rel == ConstraintRelationship.EQ)
			Relationship.EQ
		else if (rel == ConstraintRelationship.LEQ)
			Relationship.LEQ
		else
			Relationship.GEQ
	}
}
