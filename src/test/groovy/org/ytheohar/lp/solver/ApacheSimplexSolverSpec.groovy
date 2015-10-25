package org.ytheohar.lp.solver

import java.util.List

import org.ytheohar.lp.model.ConstraintRelationship;
import org.ytheohar.lp.model.LPConstraint;
import org.ytheohar.lp.model.LPFunction;
import org.ytheohar.lp.model.LPInstance;
import org.ytheohar.lp.solver.ApacheSimplexSolver

import spock.lang.Specification
import spock.lang.Unroll
import org.ytheohar.lp.model.LPIState

class ApacheSimplexSolverSpec extends Specification {

	def 'from SimplexSolverTest.testSimplexSolver' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':15, 'x1':10], 7)
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':1], 0), ConstraintRelationship.LEQ, 2), 
				new LPConstraint(new LPFunction(['x1':1], 0), ConstraintRelationship.LEQ, 3), 
				new LPConstraint(new LPFunction(['x0':1, 'x1':1], 0), ConstraintRelationship.EQ, 4)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)

		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 57
			solution.at['x0'] == 2
			solution.at['x1'] == 2
	}

	def 'from SimplexSolverTest.testSingleVariableAndConstraint' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0']
			def objFunc = new LPFunction(['x0':3], 0)
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':1], 0), ConstraintRelationship.LEQ, 10)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 30
			solution.at['x0'] == 10
	}

	def 'from SimplexSolverTest.testMinimization' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':-2, 'x1':1], -5)
			def constraints = [
				new LPConstraint(new LPFunction(['x0':1, 'x1':2], 0), ConstraintRelationship.LEQ, 6),
				new LPConstraint(new LPFunction(['x0':3, 'x1':2], 0), ConstraintRelationship.LEQ, 12)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == -13
			solution.at['x0'] == 4
			solution.at['x1'] == 0
	}

	def 'from SimplexSolverTest.testInfeasibleSolution' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0']
			def objFunc = new LPFunction(['x0':15])
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':1]), ConstraintRelationship.LEQ, 1), 
				new LPConstraint(new LPFunction(['x0':1]), ConstraintRelationship.GEQ, 3)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.NOT_FEASIBLE
//			thrown(org.apache.commons.math3.optim.linear.NoFeasibleSolutionException)
	}

	def 'from SimplexSolverTest.testUnboundedSolution' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':15, 'x1':10])
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':1]), ConstraintRelationship.LEQ, 2), 
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.UNBOUNDED
//			thrown(org.apache.commons.math3.optim.linear.UnboundedSolutionException)
	}

	def 'from SimplexSolverTest.testRestrictVariablesToNonNegative' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1', 'x2', 'x3', 'x4']
			def objFunc = new LPFunction(['x0':409, 'x1':523, 'x2':70, 'x3':204, 'x4':339])
			def constraints = [
			   			    new LPConstraint(new LPFunction(['x0':43, 'x1':56, 'x2':345, 'x3':56, 'x4':5]), ConstraintRelationship.LEQ, 4567456), 
						    new LPConstraint(new LPFunction(['x0':12, 'x1':45, 'x2':7, 'x3':56, 'x4':23]), ConstraintRelationship.LEQ, 56454), 
						    new LPConstraint(new LPFunction(['x0':8, 'x1':768, 'x3':34, 'x4':7456]), ConstraintRelationship.LEQ, 1923421), 
						    new LPConstraint(new LPFunction(['x0':12342, 'x1':2342, 'x2':34, 'x3':678, 'x4':2342]), ConstraintRelationship.GEQ, 4356), 
						    new LPConstraint(new LPFunction(['x0':45, 'x1':678, 'x2':76, 'x3':52, 'x4':23]), ConstraintRelationship.EQ, 456356)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.optimum.trunc(6) == 1438556.749140
			solution.at['x0'].trunc(10) == 2902.9278350515
			solution.at['x1'].trunc(10) == 480.4192439862
			solution.at['x2'] == 0
			solution.at['x3'] == 0
			solution.at['x4'] == 0
	}

	def 'from SimplexSolverTest.testEpsilon' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1', 'x2']
			def objFunc = new LPFunction(['x0':10, 'x1':5, 'x2':1])
			def constraints = [
  				new LPConstraint(new LPFunction(['x0':9, 'x1':8]), ConstraintRelationship.EQ, 17), 
				new LPConstraint(new LPFunction(['x1':7, 'x2':8]), ConstraintRelationship.LEQ, 7), 
				new LPConstraint(new LPFunction(['x0':10, 'x2':2]), ConstraintRelationship.LEQ, 10), 
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.optimum == 15
			solution.at['x0'] == 1
			solution.at['x1'] == 1
			solution.at['x2'] == 0
	}

	def 'from SimplexSolverTest.testTrivialModel' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':1, 'x1':1])
			def constraints = [
				new LPConstraint(new LPFunction(['x0':1, 'x1':1]), ConstraintRelationship.LEQ, 0)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.optimum == 0
			solution.at['x0'] == 0
			solution.at['x1'] == 0
	}

	def 'from SimplexSolverTest.testMath713NegativeVariable' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':1, 'x1':1])
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':1]), ConstraintRelationship.EQ, 1)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.optimum == 1
			solution.at['x0'] == 1
			solution.at['x1'] == 0
	}

	def 'from SimplexSolverTest.testMath434UnfeasibleSolution' () {
		given:
			def solver = new ApacheSimplexSolver()
			def epsilon = 1e-6;
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':1])
			def constraints = [
			   	new LPConstraint(new LPFunction(['x0':epsilon/2, 'x1':0.5]), ConstraintRelationship.EQ, 0), 
				new LPConstraint(new LPFunction(['x0':1e-3, 'x1':0.1]), ConstraintRelationship.EQ, 10)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.NOT_FEASIBLE
	}

	def 'from SimplexSolverTest.testMath434PivotRowSelection' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0']
			def objFunc = new LPFunction(['x0':1])
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':200], 0), ConstraintRelationship.GEQ, 1), 
				new LPConstraint(new LPFunction(['x0':100], 0), ConstraintRelationship.GEQ, 0.499900001)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 0.005
			solution.at['x0'] == 0.005
	}

	def 'from SimplexSolverTest.testMath272' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1', 'x2']
			def objFunc = new LPFunction(['x0':2, 'x1':2, 'x2':1])
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':1, 'x1':1]), ConstraintRelationship.GEQ, 1), 
				new LPConstraint(new LPFunction(['x0':1, 'x2':1]), ConstraintRelationship.GEQ, 1),
				new LPConstraint(new LPFunction(['x1':1]), ConstraintRelationship.GEQ, 1)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 3
			solution.at['x0'] == 0
			solution.at['x1'] == 1
			solution.at['x2'] == 1
	}

	def 'from SimplexSolverTest.testMath286' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1', 'x2', 'x3', 'x4', 'x5']
			def objFunc = new LPFunction(['x0':0.8, 'x1':0.2, 'x2':0.7, 'x3':0.3, 'x4':0.6, 'x5':0.4])
			def constraints = [
				new LPConstraint(new LPFunction(['x0':1, 'x2':1, 'x4':1]), ConstraintRelationship.EQ, 23),
				new LPConstraint(new LPFunction(['x1':1, 'x3':1, 'x5':1]), ConstraintRelationship.EQ, 23),
				new LPConstraint(new LPFunction(['x0':1]), ConstraintRelationship.GEQ, 10),
				new LPConstraint(new LPFunction(['x2':1]), ConstraintRelationship.GEQ, 8),
				new LPConstraint(new LPFunction(['x4':1]), ConstraintRelationship.GEQ, 5)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum.trunc(1) == 25.8
			solution.at['x0'] == 10
			solution.at['x1'] == 0
			solution.at['x2'] == 8
			solution.at['x3'] == 0
			solution.at['x4'] == 5
			solution.at['x5'] == 23
	}

	def 'from SimplexSolverTest.testDegeneracy' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':0.8, 'x1':0.7])
			def constraints = [
  				new LPConstraint(new LPFunction(['x0':1, 'x1':1], 0), ConstraintRelationship.LEQ, 18),
			    new LPConstraint(new LPFunction(['x0':1], 0), ConstraintRelationship.GEQ, 10), 
				new LPConstraint(new LPFunction(['x1':1], 0), ConstraintRelationship.GEQ, 8)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 13.6
			solution.at['x0'] == 10
			solution.at['x1'] == 8
	}

	def 'from SimplexSolverTest.testMath288' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1', 'x2', 'x3']
			def objFunc = new LPFunction(['x0':7, 'x1':3])
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':3, 'x2':-5]), ConstraintRelationship.LEQ, 0),
			    new LPConstraint(new LPFunction(['x0':2, 'x3':-5]), ConstraintRelationship.LEQ, 0),
			    new LPConstraint(new LPFunction(['x1':3, 'x3':-5]), ConstraintRelationship.LEQ, 0),
			    new LPConstraint(new LPFunction(['x0':1]), ConstraintRelationship.LEQ, 1),
       			new LPConstraint(new LPFunction(['x1':1]), ConstraintRelationship.LEQ, 1)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 10
			solution.at['x0'] == 1
			solution.at['x1'] == 1
			solution.at['x2'].trunc(5) == 0.60000
			solution.at['x3'].trunc(5) == 0.60000
	}

	def 'from SimplexSolverTest.testMath290GEQ' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':1, 'x1':5])
			def constraints = [
			    new LPConstraint(new LPFunction(['x0':2]), ConstraintRelationship.GEQ, -1)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 0
			solution.at['x0'] == 0
			solution.at['x1'] == 0
	}

	def 'from SimplexSolverTest.testMath290LEQ' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1']
			def objFunc = new LPFunction(['x0':1, 'x1':5])
			def constraints = [
				new LPConstraint(new LPFunction(['x0':2]), ConstraintRelationship.LEQ, -1)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, false, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.NOT_FEASIBLE
	}

	def 'from SimplexSolverTest.testMath293' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1', 'x2', 'x3', 'x4', 'x5']
			def objFunc = new LPFunction(['x0':0.8, 'x1':0.2, 'x2':0.7, 'x3':0.3, 'x4':0.4, 'x5':0.6])
			def constraints = [
				new LPConstraint(new LPFunction(['x0':1, 'x2':1, 'x4':1]), ConstraintRelationship.EQ, 30),
				new LPConstraint(new LPFunction(['x1':1, 'x3':1, 'x5':1]), ConstraintRelationship.EQ, 30),
				new LPConstraint(new LPFunction(['x0':0.8, 'x1':0.2]), ConstraintRelationship.GEQ, 10),
				new LPConstraint(new LPFunction(['x2':0.7, 'x3':0.3]), ConstraintRelationship.GEQ, 10),
				new LPConstraint(new LPFunction(['x4':0.4, 'x5':0.6]), ConstraintRelationship.GEQ, 10)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, true)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum.trunc(4) == 40.5714
			solution.at['x0'].trunc(3) == 15.714
			solution.at['x1'] == 0
			solution.at['x2'].trunc(3) == 14.285
			solution.at['x3'] == 0
			solution.at['x4'] == 0
			solution.at['x5'] == 30
	}
	
	def 'from SimplexSolverTest.testMath781' () {
		given:
			def solver = new ApacheSimplexSolver()
			def vars = ['x0', 'x1', 'x2']
			def objFunc = new LPFunction(['x0':2, 'x1':6, 'x2':7])
			def constraints = [
			   	new LPConstraint(new LPFunction(['x0':1, 'x1':2, 'x2':1]), ConstraintRelationship.LEQ, 2),
				new LPConstraint(new LPFunction(['x0':-1, 'x1':1, 'x2':1]), ConstraintRelationship.LEQ, -1),
				new LPConstraint(new LPFunction(['x0':2, 'x1':-3, 'x2':1]), ConstraintRelationship.LEQ, -1)
			]
			def lpi = new LPInstance(vars, objFunc, constraints, true, false)
			
		when:
			def solution = solver.solve(lpi)

		then:
			solution.state == LPIState.FEASIBLE
			solution.optimum == 2
			solution.at['x0'].trunc(4) == 1.0909
			solution.at['x1'].trunc(4) == 0.8181
			solution.at['x2'].trunc(4) == -0.7273
     }
}