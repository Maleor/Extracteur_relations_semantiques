package extractor;

import java.io.IOException;

/**
 * 
 * @author Mathieu Dodard
 * @author Jordan Guillonneau
 *
 */
public class Runner {

	
	public static void main(String[] args) throws IOException {
		
		Parser parser = new Parser();
		
		System.out.println("#################################");
		System.out.println("####### PROGRAM EXECUTION #######");
		System.out.println("#################################\n");
		
		parser.run();
		
		System.out.println("\n##################################");
		System.out.println("####### END OF THE PROGRAM #######");
		System.out.println("##################################");
		
	}
	
}
