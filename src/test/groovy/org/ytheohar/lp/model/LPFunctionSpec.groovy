package org.ytheohar.lp.model

import org.ytheohar.lp.model.LPFunction;

import spock.lang.Specification

class LPFunctionSpec extends Specification {

	def 'test toString' () {
		given: 
			LPFunction f = new LPFunction(['x0':3.2, 'x1':-1.2, 'x3':5.4], 7.1)
		expect:
			f.toString() == '3.2*x0 + -1.2*x1 + 5.4*x3 + 7.1'
	}
}