package org.semanticweb.blade;

import java.util.HashSet;
import java.util.Set;

public class ERule extends Rule {



	Set<EVariable> getHeadEVars() {

		Set<EVariable> EVars = new HashSet<EVariable>();

		for (Atom atom : this.head) {
			EVars.addAll(atom.getEVars());
		}

		return EVars;

	}

}