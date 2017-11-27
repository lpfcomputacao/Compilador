package compilador;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
	
	public static ArrayList<Token> tokenList = new ArrayList<>();
	public static HashMap<Integer, String> tableOfSymbols = new HashMap<>();
	
	public static void main(String[] args) throws IOException {
		
		LexicalAnalyzerSolver lexicalAnalyzerSolver = new LexicalAnalyzerSolver("file.txt");
		lexicalAnalyzerSolver.run();
	}
}
