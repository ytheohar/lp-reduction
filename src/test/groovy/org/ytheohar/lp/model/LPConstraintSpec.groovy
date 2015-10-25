package org.ytheohar.lp.model

import org.ytheohar.lp.model.ConstraintRelationship;
import org.ytheohar.lp.model.LPConstraint;
import org.ytheohar.lp.model.LPFunction;

import spock.lang.Specification
import spock.lang.Unroll;

class LPConstraintSpec extends Specification {

	@Unroll
	def 'Constraint constant should take into account the function constant, #functionConstant' () {
		given: 
			LPFunction f = new LPFunction(['x0':3.2, 'x1':-1.2, 'x3':5.4], functionConstant)
			LPConstraint c = new LPConstraint(f, ConstraintRelationship.LEQ, -4.3)
		expect:
			c.constant == expectedConstant
			c.function.constant == 0
			c.toString() == '3.2*x0 + -1.2*x1 + 5.4*x3 <= '+ expectedConstant
		where:
			functionConstant 	|| expectedConstant
				0.0				||  -4.3
				1.0				||  -5.3
				-1.0			||  -3.3
	}
	
	@Unroll
	def 'Constraint constant (initialized to 0) should take into account the function constant, #functionConstant' () {
		given: 
			LPFunction f = new LPFunction(['x0':3.2, 'x1':-1.2, 'x3':5.4], functionConstant)
			LPConstraint c = new LPConstraint(f, ConstraintRelationship.LEQ)
		expect:
			c.constant == expectedConstant
			c.function.constant == 0
			c.toString() == '3.2*x0 + -1.2*x1 + 5.4*x3 <= '+ expectedConstant
		where:
			functionConstant 	|| expectedConstant
				0.0				||  0.0
				1.0				||  -1.0
				-1.0			||  1.0
	}

}