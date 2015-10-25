package org.ytheohar.lp.model

import groovy.transform.CompileStatic

@CompileStatic
public enum ConstraintRelationship {
	EQ(' = '), LEQ(' <= '), GEQ(' >= ');
	
	private final String label
	
	private ConstraintRelationship(String label) {
		this.label = label
	}
	
	@Override
	String toString() {
		label
	}
}