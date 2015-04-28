package org.semanticweb.blade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reasoner {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("Error: expecting two arguments");
			System.out
					.println("Usage: blade COMMAND FILE \n where COMMAND is \n\t datalog - translates FILE into a datalog program "
							+ "\n\t saturate - dumps the result of the saturation of FILE");
			System.exit(1);
		}

		String test = null;
		try {
			test = new String(Files.readAllBytes(Paths.get(args[1])));
		} catch (IOException e1) {
			//e1.printStackTrace();
			System.out.println("Error: couldn't open the file " + args[1]);
			System.exit(1);
		}

		Program program = null;

		try {
			program = Parser.parseProgram(test);
		} catch (SyntaxErrorException e) {
			System.out.println(e.getMessage());
			//e.printStackTrace();
			System.exit(1);
		}

		if (args[0].equals("datalog")) {
			try {

				System.out.println(Saturation.toDatalog(program).toString());
			} catch (SyntaxErrorException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		

		if (args[0].equals("saturate")) {
			try {

				System.out.println(Saturation.toSaturatedProgram(program).toString());
			} catch (SyntaxErrorException e) {
				System.out.println(e.getMessage());
				System.exit(1);
			}
		}
		
		

	}

}
