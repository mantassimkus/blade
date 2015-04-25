package org.semanticweb.blade;

import java.util.ArrayList;
import java.util.List;

public class Parser {

	

	static Program parseProgram(String programStr) throws SyntaxErrorException {

		/*
		 * The parse rules are as follows: 1. An empty string corresponds to an
		 * empty program. Otherwise, a string that contains visible symbols is
		 * treated according to 2.i and 2.ii. 2.i. If the input string does not
		 * have ";", then the string must encode a single rule 2.ii if the input
		 * string has ";", then everything until the first occurrence of ";" is
		 * a rule and the rest must be a program.
		 */

		if (programStr.trim().isEmpty()) {
			return new Program();
		}

		int posSemiCol = programStr.indexOf(";");

		if (posSemiCol == -1) {

			
			Program program = new Program();
			Rule rule = Parser.parseRule(programStr);
			program.rules.add(rule);
			
			return program;

		} else {

			String lhs = programStr.substring(0, posSemiCol);
			String rhs = programStr.substring(posSemiCol + 1);

			
			Program program = Parser.parseProgram(rhs);
			Rule rule = Parser.parseRule(lhs);
			
			program.rules.add(0,rule);
			return program;

		}
	}

	static Rule parseRule(String ruleStr) throws SyntaxErrorException {

		/*
		 * the parse rule is simple: 1. find ":-" 2.the lhs and rhs are atom
		 * lists
		 */

		int posGets = ruleStr.indexOf(":-");

		if (posGets == -1) {

			throw new SyntaxErrorException(
					"Syntax error: couldn't identify a  rule, ':-' expected. Offending text:"
							+ ruleStr);

		} else {

			String lhs = ruleStr.substring(0, posGets);
			String rhs = ruleStr.substring(posGets + 2);

			List<Atom> head = Parser.parseAtomList(lhs);
			List<Atom> body = Parser.parseAtomList(rhs);

			Rule ret = Rule.createFromAtomLists(head, body);
			
			if (!ret.isSafe()) {
				throw new SyntaxErrorException(
						"Syntax error: a violation of rule safety. Offending rule:"
								+ ruleStr);
			}
			

			return ret;
		}
	}

	static List<Atom> parseAtomList(String atomListStr)
			throws SyntaxErrorException {

		/*
		 * The parse rule is as follows. If the string is empty, then return an
		 * empty list of atoms. Otherwise, search for the first occurrence of
		 * "),". If it exists, then the lhs is an atom and the lhs is again an
		 * list of atoms. If it does not exists, then the string encodes a
		 * single atom.
		 */

		if (atomListStr.trim().isEmpty()) {
			return new ArrayList<Atom>();
		}

		int posBC = atomListStr.indexOf("),");

		if (posBC == -1) {

			Atom atom = Parser.parseAtom(atomListStr);

			List<Atom> ret = new ArrayList<Atom>();
			ret.add(0,atom);

			return ret;

		} else {

			String lhs = atomListStr.substring(0, posBC + 1);
			String rhs = atomListStr.substring(posBC + 2);

			Atom atom = Parser.parseAtom(lhs);
			List<Atom> atomList = Parser.parseAtomList(rhs);

			atomList.add(0,atom);
			return atomList;

		}

	}

	static Atom parseAtom(String atomStr)
			throws SyntaxErrorException {

		/**
		 * The parse rule is as follows. We look for the first occurence "(".
		 * The lhs is the relation name and the rhs is a list of terms.
		 * */

		int pos = atomStr.indexOf("(");

		if (pos == -1) {

			throw new SyntaxErrorException(
					"Syntax error: couldn't find argument list in an atom. Offending text: "
							+ atomStr);

		}

		if (atomStr.charAt(atomStr.length() - 1) != ')') {

			throw new SyntaxErrorException(
					"Syntax error: atom must end with ')'. Offending text: "
							+ atomStr);

		}

		String termListStr = atomStr.substring(pos + 1, atomStr.length() - 1);
		List<Term> termList = Parser.parseTermList(termListStr);

		String relName = atomStr.substring(0, pos).trim();
		if (!relName.matches("^\\w+$")) {
			throw new SyntaxErrorException(
					"Syntax error: relation name should be alphanumeretic + underscore. Offending text:"
							+ relName);
		}

		Predicate pred = Predicate.predicateFromString(relName,
				termList.size());
		
		return new Atom(pred, termList);
	}

	static List<Term> parseTermList(String termListStr)
			throws SyntaxErrorException {

		/*
		 * The parse rule is as follows. If the string is empty, then return an
		 * empty list of terms. Otherwise, search for the first occurrence of
		 * ",". If it exists, then the lhs is a term and the lhs is again a list
		 * of terms. If ',' does not exist, then the string encodes a single
		 * term.
		 */

		if (termListStr.trim().isEmpty()) {
			return new ArrayList<Term>();
		}

		int pos = termListStr.indexOf(",");

		if (pos == -1) {

			Term term = Term.termFromString(termListStr);

			List<Term> ret = new ArrayList<Term>();
			ret.add(0,term);

			return ret;

		} else {

			String lhs = termListStr.substring(0, pos);
			String rhs = termListStr.substring(pos + 1);

			Term term = Term.termFromString(lhs); 
					
				
			List<Term> termList = Parser.parseTermList(rhs);

			termList.add(0,term);
			return termList;

		}

	}

	

}
