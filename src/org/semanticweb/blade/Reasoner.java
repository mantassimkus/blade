package org.semanticweb.blade;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Reasoner {

	public static void main(String[] args)  {
		
			if (args.length != 2) {
				System.out.println("Usage: blade COMMAND FILE \n where COMMAND is \n\t info - provides general info about FILE,"
						+ "\n\t datalog - translates FILE into a datalog program "
						+ "\n\t saturate - dumps the result of the saturation of FILE");
				System.exit(0);
			}
		
			
			   String test = null;
				try {
					test = new String(Files.readAllBytes(Paths.get(args[1])));
				} catch (IOException e1) {  
					 e1.printStackTrace();
					System.exit(1);
				}
				
				Program program = null;
		        
				try {
					program = Parser.parseProgram(test);
				} catch (SyntaxErrorException e) {
					
					 e.printStackTrace();
					 System.exit(1);
				}
				
				if (args[0].equals("datalog")) {
					try {
						
						System.out.println(Saturation.rewrite(program).toString());
					} catch (SyntaxErrorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(1);
					}}
				
				
	}

}
