package org.ytheohar.lp.model

import groovy.transform.Canonical
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor;

@TupleConstructor
@CompileStatic
class Solution<V> {
	Double optimum
	Map<V, Double> at
	LPIState state
}

enum LPIState {
	FEASIBLE, 
	NOT_FEASIBLE,
	UNBOUNDED
}