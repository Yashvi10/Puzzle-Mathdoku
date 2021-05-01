/**
 *
 * @author Yashvi
 * 
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Mathdoku_Main {
    
	public static void main(String[] args) throws IOException {
		Mathdoku md = new Mathdoku();
		
		String Filepath = "";
		
                //Add your input text filepath into Filepath
		BufferedReader br = new BufferedReader(new FileReader(Filepath));
		
		System.out.println("loadPuzzle returned: "+md.loadPuzzle(br));
		System.out.println("validate returned: "+md.validate());
		System.out.println("solve returned: "+md.solve());
		System.out.println("choices returned: "+md.choices);
		System.out.println("print returned: \n"+md.print());
		
	}
}
