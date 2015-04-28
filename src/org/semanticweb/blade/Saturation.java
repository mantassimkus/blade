package org.semanticweb.blade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Saturation {

	private List<FRule> fRules = new ArrayList<FRule>();
	private List<ERule> eRules = new ArrayList<ERule>();

	private List<ERule> fJobs = new ArrayList<ERule>();
	private List<Pair<FRule, ERule>> eJobs = new ArrayList<Pair<FRule, ERule>>();

	public boolean insertERule(ERule rule) {

		if (!eRules.contains(rule)) {
			eRules.add(rule);

			fJobs.add(rule);

			for (FRule full_rule : fRules)
				eJobs.add(new Pair<FRule, ERule>(full_rule, rule));

			return true;
		}

		return false;
	}

	public boolean insertFRule(FRule rule) {

		if (!fRules.contains(rule)) {
			fRules.add(rule);

			for (ERule erule : this.eRules) {
				// System.out.println("Adding pair : " + erule.toString() +
				// " AND "+rule.toString());
				eJobs.add(new Pair<FRule, ERule>(rule, erule));
			}

			return true;
		}

		return false;
	}

	public void compute() throws SyntaxErrorException {

		while (!fJobs.isEmpty() || !eJobs.isEmpty()) {

			if (!fJobs.isEmpty()) {
				// System.out.println("applying full rule on " + fJobs.get(0));

				for (FRule rule : this.applyFRule(fJobs.remove(0)))
					this.insertFRule(rule);

			}

			// System.out.println("Jobs left    : " +eJobs.size());

			if (!eJobs.isEmpty()) {

				FRule frule = eJobs.get(0).left;
				ERule erule = eJobs.get(0).right;
				eJobs.remove(0);

				// System.out.println("applying    : " + erule.toString() +
				// " on "
				// + frule.toString());
				for (ERule res : this.applyERule(erule, frule))
					this.insertERule(res);

			}
		}

	}

	private FRule OLDapplyFRule(ERule erule) throws SyntaxErrorException {

		List<Atom> new_head = new ArrayList<Atom>();
		List<Atom> new_body = new ArrayList<Atom>();

		new_body = new ArrayList<Atom>(erule.body);

		for (Atom atom : erule.head) {
			if (atom.getEVars().isEmpty())
				new_head.add(atom);
		}

		return new_head.isEmpty() ? null : (FRule) Rule.createFromAtomLists(
				new_head, new_body);
	}

	private List<FRule> applyFRule(ERule erule) throws SyntaxErrorException {

		List<FRule> ret = new ArrayList<FRule>();
		List<Atom> new_body = new ArrayList<Atom>(erule.body);

		for (Atom atom : erule.head) {
			if (atom.getEVars().isEmpty()) {
				List<Atom> new_head = new ArrayList<Atom>();
				new_head.add(atom);
				ret.add((FRule) Rule.createFromAtomLists(new_head, new_body));

			}
		}

		return ret;
	}

	private List<ERule> applyERule(ERule erule, FRule frule)
			throws SyntaxErrorException {

		/*
		 * We take the first guard in frule. We try to unify it with an atom in
		 * the head of erule. For all atoms in the body of frule it must holds:
		 * if the atom is not present in the head of erule, then it has no
		 * existential variables. such atoms are then stated in the body of
		 * erule and the head is added.
		 */

		int guard_pos = frule.isGuarded();
		List<ERule> ret = new ArrayList<ERule>();

		for (int index = 0; index < erule.head.size(); index++)
			if (erule.head.get(index).getPredicate() == frule.body.get(
					guard_pos).getPredicate()) {

				ERule new_erule = (ERule) erule.copyKeepTerms();
				FRule new_frule = (FRule) frule.copyDualTerms();

				Atom e_atom = new_erule.head.get(index);
				Atom f_atom = new_frule.body.get(guard_pos);

				HashSet<Term> used = new HashSet<Term>();

				for (int pos = 0; pos < e_atom.getPredicate().getArity(); pos++) {

					Term t1 = e_atom.getTermList().get(pos);
					Term t2 = f_atom.getTermList().get(pos);

					if (t1 != t2 && !used.contains(t2)) {
						// replace in new_frule t2 by t1

						for (Atom at : new_frule.body)
							Collections.replaceAll(at.getTermList(), t2, t1);
						for (Atom at : new_frule.head)
							Collections.replaceAll(at.getTermList(), t2, t1);

						used.add(t1);

					}

					if (t1 != t2 && used.contains(t2) && !used.contains(t1)) {
						// replace in new_erule t1 by t2

						for (Atom at : new_erule.body)
							Collections.replaceAll(at.getTermList(), t1, t2);
						for (Atom at : new_erule.head)
							Collections.replaceAll(at.getTermList(), t1, t2);

					}

					if (t1 != t2 && used.contains(t2) && used.contains(t1)) {
						// replace in both new_erule and new_frule t1 by t2

						for (Atom at : new_frule.body)
							Collections.replaceAll(at.getTermList(), t1, t2);
						for (Atom at : new_frule.head)
							Collections.replaceAll(at.getTermList(), t1, t2);
						for (Atom at : new_erule.body)
							Collections.replaceAll(at.getTermList(), t1, t2);
						for (Atom at : new_erule.head)
							Collections.replaceAll(at.getTermList(), t1, t2);

					}

				}

				boolean unify = true;

				if (unify) {

					for (Atom addition : new_frule.head)
						if (!new_erule.head.contains(addition))
							new_erule.head.add(addition);

					boolean fail = false;

					while (!new_frule.body.isEmpty() && !fail) {

						Atom at = new_frule.body.remove(0);

						if (!new_erule.head.contains(at)) {
							if (at.getEVars().isEmpty()) {
								if (!new_erule.body.contains(at))
									new_erule.body.add(at);
							} else {
								fail = true;
							}
						}

					}

					if (!fail) {
						ret.add((ERule) Rule.createFromAtomLists(
								new_erule.head, new_erule.body));
						// System.out.println(ret.size());
					}
				}

			}

		return ret;
	}

	static Saturation saturate(Program input) throws SyntaxErrorException {

		Saturation rewriter = new Saturation();

		for (Rule rule : input.rules)
			if (rule instanceof ERule) {
				rewriter.insertERule((ERule) rule);
			} else {
				rewriter.insertFRule((FRule) rule);
			}

		rewriter.compute();

		return rewriter;

	}

	static Program toSaturatedProgram(Program input)
			throws SyntaxErrorException {

		Saturation sat = Saturation.saturate(input);

		Program ret = new Program();

		ret.rules.addAll(sat.eRules);

		ret.rules.addAll(sat.fRules);

		return ret;

	}

	static Program toDatalog(Program input) throws SyntaxErrorException {

		Saturation sat = Saturation.saturate(input);

		Program ret = new Program();

		// ret.rules.addAll(sat.eRules);

		ret.rules.addAll(sat.fRules);

		return ret;

	}

}
