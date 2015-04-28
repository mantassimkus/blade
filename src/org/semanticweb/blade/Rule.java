package org.semanticweb.blade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Rule {

	public List<Atom> head;
	public List<Atom> body;

	protected Rule() {

	}

	static Rule createFromAtomLists(List<Atom> head, List<Atom> body)
			throws SyntaxErrorException {

		for (Atom atom : body) {
			if (!atom.getEVars().isEmpty()) {
				throw new SyntaxErrorException(
						"Syntax error: existential variables cannot occur in rule bodies. Offending atom:"
								+ atom.toString());
			}
		}

		boolean existential = false;

		for (Atom atom : head) {
			if (!atom.getEVars().isEmpty()) {
				existential = true;
			}
		}

		Rule ret = existential ? new ERule() : new FRule();

		ret.head = head;
		ret.body = body;
		return ret;
	}

	boolean isSafe() {
		return getBodyUVars().containsAll(getHeadUVars());
	}

	int isGuarded() {

		for (int pos = 0; pos < this.body.size(); pos++)
			if (this.body.get(pos).getUVars().containsAll(this.getAllUVars()))
				return pos;

		return -1;
	}

	boolean isWeaklyGuarded(Map<Predicate, Set<Integer>> affectedPos) {

		for (Atom atom : this.body) {
			if (atom.getUVars().containsAll(this.getUnsafeUVars(affectedPos)))
				return true;
		}

		return false;
	}

	boolean isFrontierGuarded() {
		// for every atom h in the head, there exists a body atom that contains
		// all
		// universal variables of h

		boolean failure;

		for (Atom h : this.head) {

			failure = true;

			for (Atom b : this.body) {
				if (b.getUVars().containsAll(h.getUVars())) {
					failure = false;
				}
			}

			if (failure == true) {
				return false;
			}

		}

		return true;
	}

	boolean isWeaklyFrontierGuarded(Map<Predicate, Set<Integer>> affectedPos) {
		// for every atom h in the head, there exists a body atom that contains
		// all unsafe universal variables of h

		boolean failure;

		for (Atom h : this.head) {

			Set<UVariable> unsafeUVars = new HashSet<UVariable>();
			for (UVariable variable : h.getUVars()) {
				if (this.getUnsafeUVars(affectedPos).contains(variable)) {
					unsafeUVars.add(variable);
				}
			}

			failure = true;

			for (Atom b : this.body) {
				if (b.getUVars().containsAll(unsafeUVars)) {
					failure = false;
				}
			}

			if (failure == true) {
				return false;
			}

		}

		return true;
	}

	Set<UVariable> getBodyUVars() {

		Set<UVariable> uVars = new HashSet<UVariable>();

		for (Atom atom : this.body) {
			uVars.addAll(atom.getUVars());
		}

		return uVars;

	}

	Set<UVariable> getHeadUVars() {

		Set<UVariable> uVars = new HashSet<UVariable>();

		for (Atom atom : this.head) {
			uVars.addAll(atom.getUVars());
		}

		return uVars;

	}

	Set<UVariable> getAllUVars() {

		Set<UVariable> uVars = new HashSet<UVariable>();

		uVars.addAll(getBodyUVars());

		uVars.addAll(getHeadUVars());

		return uVars;

	}

	Set<UVariable> getUnsafeUVars(Map<Predicate, Set<Integer>> affectedPos) {

		/*
		 * A universal variable is unsafe if it only appears in affected
		 * positions in the body
		 */

		Set<UVariable> ret = new HashSet<UVariable>();

		for (UVariable candidate : this.getBodyUVars()) {

			boolean unsafe = true;

			for (Atom body_atom : this.body) {
				int body_pos = -1;
				for (Term variable : body_atom.getTermList()) {
					body_pos = body_pos + 1;

					if (variable == candidate
							&& (!affectedPos.containsKey(body_atom
									.getPredicate()) || !affectedPos.get(
									body_atom.getPredicate())
									.contains(body_pos))) {
						unsafe = false;

					}
				}

			}

			if (unsafe) {
				ret.add(candidate);
			}
		}

		return ret;
	}

	public String toString() {

		String ret = this.head.stream().map(Atom::toString)
				.collect(Collectors.joining(","))
				+ ":-"
				+ this.body.stream().map(Atom::toString)
						.collect(Collectors.joining(","));

		return ret;
	}

	public Rule copyKeepTerms() throws SyntaxErrorException {

		List<Atom> new_head = new ArrayList<Atom>(this.head);
		for (int pos = 0; pos < new_head.size(); pos++)
			new_head.set(pos, new_head.get(pos).copyKeepTerms());

		List<Atom> new_body = new ArrayList<Atom>(this.body);
		for (int pos = 0; pos < new_body.size(); pos++)
			new_body.set(pos, new_body.get(pos).copyKeepTerms());

		return Rule.createFromAtomLists(new_head, new_body);
	}

	public Rule copyDualTerms() throws SyntaxErrorException {

		List<Atom> new_head = new ArrayList<Atom>(this.head);
		for (int pos = 0; pos < new_head.size(); pos++)
			new_head.set(pos, new_head.get(pos).copyDualTerms());

		List<Atom> new_body = new ArrayList<Atom>(this.body);
		for (int pos = 0; pos < new_body.size(); pos++)
			new_body.set(pos, new_body.get(pos).copyDualTerms());

		return Rule.createFromAtomLists(new_head, new_body);

	}

	
	

	@Override
	public int hashCode() {

		return 31 * body.hashCode() + head.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (!(obj instanceof Rule))
			return false;

		Rule rule = (Rule) obj;

		// return this.head.equals(rule.head) && this.body.equals(rule.body);
		
		return this.head.containsAll(rule.head) && rule.head.containsAll(this.head) && this.body.containsAll(rule.body) && rule.body.containsAll(this.body); 
 
				
	}
	
	
	
	
}
