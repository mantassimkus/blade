package org.semanticweb.blade;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Atom {
	
	private Predicate predicate;
	private List<Term> termList;

	public Atom(Predicate pred, List<Term> termList) {
		this.predicate = pred;
		this.termList = termList;
	}

	public Predicate getPredicate() {
		return predicate;
	}

	public List<Term> getTermList() {
		return termList;
	}

	Set<UVariable> getUVars() {

		Set<UVariable> ret = new HashSet<UVariable>();

		for (Term term : this.getTermList()) {
			if (term.isuvar())
				ret.add((UVariable) term);
		}

		return ret;

	}

	
	Set<EVariable> getEVars() {

		Set<EVariable> ret = new HashSet<EVariable>();

		for (Term term : this.getTermList()) {
			if (term.isevar())
				ret.add((EVariable) term);
		}

		return ret;

	}
	
	Set<Constant> getConstants() {

		Set<Constant> ret = new HashSet<Constant>();

		for (Term term : this.getTermList()) {
			if (term.isconst())
				ret.add((Constant) term);
		}

		return ret;

	}


	public String toString() {
		
		return this.predicate.toString() + "(" + this.termList.stream().map(Term::toString).collect(Collectors.joining(",")) + ")";
	}

	@Override
	public int hashCode() {
		return 31 * predicate.hashCode() + termList.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof Atom)) 
			return false;
		
		Atom atom = (Atom) obj;
		
		return atom.predicate.equals(this.predicate) && atom.termList.equals(this.termList);
		}

	Atom copyKeepTerms() {
		return new Atom(this.predicate, new ArrayList<Term>(this.termList));
	}
	
	Atom copyDualTerms() {
		
		List<Term> termList = new ArrayList<Term>(this.termList);
		for (int pos = 0 ; pos < termList.size() ; pos ++) 
			termList.set(pos, termList.get(pos).getDual());
		
		return new Atom(this.predicate, termList);
	}
	
	
}
