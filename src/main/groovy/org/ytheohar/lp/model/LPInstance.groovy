package org.ytheohar.lp.model

import groovy.transform.CompileStatic;
import groovy.transform.TupleConstructor;

import java.util.List;

@CompileStatic
@TupleConstructor
class LPInstance<V> {
	List<V> vars
	LPFunction objectiveFunction
	List<LPConstraint> constraints
	boolean max
	boolean nonNegative
}
