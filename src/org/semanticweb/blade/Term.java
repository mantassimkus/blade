package org.semanticweb.blade;

import java.util.HashMap;
import java.util.Map;

public abstract class Term {
	
	
	protected String name;

	private Term dual;
	
	private final static  Map<String,Term> pool = new HashMap<String,Term>();

	
	boolean isuvar() {
		return (this instanceof UVariable);
	}

	boolean isevar() {
		return (this instanceof EVariable);
	}

	boolean isconst() {
		return (this instanceof Constant);
	}

	
	public String toString() {
		
		return this.name;
	}

	public void setDual(Term dual) {
		this.dual = dual;		
	}

    public Term getDual() {
		return dual;
	}

	static Term termFromString(String termStr) throws SyntaxErrorException {
		
    	termStr = termStr.trim();
    	
		if (pool.containsKey(termStr)) {
			return pool.get(termStr);
		} else {			
			Term ret;

			if (termStr.matches("^\\041\\w+$")) {
				
				EVariable ev_main = new EVariable(termStr);
				EVariable ev_dual = new EVariable("DUAL_"+termStr);
				
				ev_main.setDual(ev_dual);
				ev_dual.setDual(ev_main);
				ret = ev_main;
				
			} else			
				if (termStr.matches("^\\w+$")) {

					UVariable uv_main = new UVariable(termStr);
					UVariable uv_dual = new UVariable("DUAL_"+termStr);
					
					uv_main.setDual(uv_dual);
					uv_dual.setDual(uv_main);
					ret = uv_main;
						
					
				
				} else
					if (termStr.matches("^\\042.*\\042$")) {

						Constant const_main = new Constant(termStr);
						Constant const_dual = new Constant("DUAL_"+termStr);
						
						const_main.setDual(const_dual);
						const_dual.setDual(const_main);
						ret = const_main;
				
					} else {
						throw new SyntaxErrorException("Syntax error: cannot parse a term. Offending text: " + termStr);
					}

			pool.put(termStr, ret);
			return ret;
		}
			
	}		
	
	
}
