package org.ytheohar.lp.model

import groovy.transform.TupleConstructor
import groovy.transform.CompileStatic

@TupleConstructor
@CompileStatic
class LPFunction<V> {
	Map<V, Double> coeffMap
	double constant
	
	LPFunction(Map<V, Double> coeffMap, double constant=0) {
		this.coeffMap = coeffMap
		this.constant = constant
	}
	
	@Override
	String toString() {
		String s = coeffMap.collect { k, v ->
			"$v*$k"
		}.join(" + ")
				
		if (constant) {
			s += " + " + constant
		}
		s
	}
}