package org.ytheohar.lp.model

import groovy.transform.CompileStatic

@CompileStatic
class LPConstraint {
	LPFunction function
	ConstraintRelationship relationship
	double constant
	
	LPConstraint(LPFunction function, ConstraintRelationship relationship, double constant = 0) {
		this.function = new LPFunction(function.coeffMap)
		this.relationship = relationship
		this.constant = constant - function.constant
	}
	
	@Override
	String toString() {
		function.toString() + relationship + constant
	}
}