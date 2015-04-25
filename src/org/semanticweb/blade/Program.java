package org.semanticweb.blade;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Program {

	public final List<Rule> rules = new ArrayList<Rule>();
	

	boolean isSafe() {
		for (Rule rule : this.rules) {
			if (!rule.isSafe()) {
				return false;
			}
		}
		return true;
	}

	

	
	boolean isGuarded() {
		for (Rule rule : this.rules) {
			if (rule.isGuarded() == -1) {
				return false;
			}
		}
		return true;
	}

	boolean isFrontierGuarded() {
		for (Rule rule : this.rules) {
			if (!rule.isFrontierGuarded()) {
				return false;
			}
		}
		return true;
	}

	Map<Predicate, Set<Integer>> getAffectedPositions() {

		Map<Predicate, Set<Integer>> ret = new HashMap<Predicate, Set<Integer>>();

		// 1. go over all rules
		// 2. go over all head atoms
		// 3. if an existential variable occurs in some position i, then i is an
		// affected position
		// in the relation of the atom

		for (Rule rule : this.rules) {
			for (Atom atom : rule.head) {
				int pos = -1;
				for (Term term : atom.getTermList()) {
					pos = pos + 1;
					if (term.isevar()) {

						if (!ret.containsKey(atom.getPredicate())) {
							ret.put(atom.getPredicate(), new HashSet<Integer>());
						}

						ret.get(atom.getPredicate()).add(pos);
					}
				}
			}
		}

		/*
		 * fix point computation until no new affected positions are inferred.
		 * We look at a rule and a head atom R(...). If the atom has a universal
		 * variable x in position i such that x only occurs in the body only in
		 * affected positions that i in R is also an affected position.
		 */

		boolean change = true;

		while (change) {
			change = false;

			for (Rule rule : this.rules) {
				for (Atom atom : rule.head) {
					int pos = -1;
					for (Term term : atom.getTermList()) {
						pos = pos + 1;
						if (term.isuvar()
								&& (!ret.containsKey(atom.getPredicate()) || !ret
										.get(atom.getPredicate()).contains(pos))) {

							boolean new_affected = true;

							for (Atom body_atom : rule.body) {
								int body_pos = -1;
								for (Term variable : body_atom.getTermList()) {
									body_pos = body_pos + 1;
									if (variable == term
											&& (!ret.containsKey(body_atom
													.getPredicate()) || !ret
													.get(body_atom
															.getPredicate())
													.contains(body_pos))) {
										new_affected = false;

									}

									if (new_affected) {
										
										change = true;
										
										if (!ret.containsKey(atom
												.getPredicate())) {
											ret.put(atom.getPredicate(),
													new HashSet<Integer>());
										}

										ret.get(atom.getPredicate()).add(pos);
									}
								}
							}

						}

					}
				}
			}

		}

		return ret;

	}




	
	public String toString() {
		
		String ret = "";
		
		for (Rule rule : this.rules) {
			ret += rule.toString()  + "\n";

//			ret += "  guarded:" + rule.isGuarded()  + " ";
	//		ret += " f-guarded:" + rule.isFrontierGuarded() + " \n";
			
		//	ret += "w-guarded:" + rule.isWeaklyGuarded(this.getAffectedPositions()) +  " " ;
			//ret += "wf-guarded:" + rule.isWeaklyFrontierGuarded(this.getAffectedPositions()) + "\n";
		}
		
		return  ret;
		
	}

	
	
}
