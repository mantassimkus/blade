package org.semanticweb.blade;

import java.util.HashMap;
import java.util.Map;

public class Predicate {
	
	private String relationName;
	private int arity;
	
	final static private Map<String,Predicate> pool = new HashMap<String,Predicate>();
			
	
	public Predicate(String relationName, int arity) {
		this.relationName = relationName;
		this.arity = arity;
	}

	public String getName() {
		return relationName;
	}

	public int getArity() {
		return arity;
	}
	

	public String toString() {
		
		return this.relationName;
	}

	@Override
	public int hashCode() {	
		return 31 * arity + relationName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return this == obj;	
	}

	
	static Predicate predicateFromString(String predicateStr, int arity) throws SyntaxErrorException {
		
		if (Predicate.pool.containsKey(predicateStr)) {
			
			Predicate pred = Predicate.pool.get(predicateStr);
			
			if (pred.getArity() != arity) {
				throw new SyntaxErrorException("Syntax error: a single relation name cannot be used with different arities.  Offending relation name: " + predicateStr);
			} else {
				return pred;
			}
			 
		} else {			
			Predicate ret = new Predicate(predicateStr, arity);

			pool.put(predicateStr, ret);
			
			return ret;
		}
			
	}

}
