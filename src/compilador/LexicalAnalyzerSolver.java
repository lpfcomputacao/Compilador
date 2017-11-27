package compilador;

import java.io.IOException;
import java.util.ArrayList;

public class LexicalAnalyzerSolver implements Runnable{

	private String stringFile; 
	
	private ArrayList<String> keywords;
	private ArrayList<String> splitters;
	private ArrayList<String> operators;
	
	private int globalLine = 1;
	private int globalColumn = 1;
	private Index index = new Index(0);
	
	public LexicalAnalyzerSolver(String fileName) throws IOException {
		this.stringFile = new LoadFile(fileName).getStringFile();
		this.keywords = new LoadFile("keywords").getFileFeatures();
		this.splitters = new LoadFile("splitter").getFileFeatures();
		this.operators = new LoadFile("operator").getFileFeatures();
	}
	
	@Override
	public void run() {
			
		sourceProgram();
		coreMethod();
		printerInformations();

	}

	private void sourceProgram() {
		System.out.println("PROGRAMA TEXTO:\n");
		System.out.println(stringFile);
	}

	private void printerInformations() {
		
		System.out.println("PALAVRAS RESERVADAS: \n");
		System.out.println(keywords + "\n");
		
		System.out.println("SEPARADORES: \n");
		System.out.println(splitters + "\n");
		
		System.out.println("OPERADORES: \n");
		System.out.println(operators + "\n");
		
		System.out.println("TABELA DE SIMBOLOS");
		for (int j = 0; j < Main.tableOfSymbols.size(); j++) {
			System.out.println("("+j+")"+ " " +Main.tableOfSymbols.get(j));
		}
		
		System.out.println("\nTOKENS");
		System.out.println("\n"+Main.tokenList);
	}

	private void coreMethod() {
		System.out.println("LEXEMAS EM PARTES");
		
		char character;
		StringBuilder lexeme = new StringBuilder();
		boolean tokenFound = false;
		
		while(index.i < stringFile.length()) {
			globalColumn++;
			character = stringFile.charAt(index.i);
			tokenFound = false;

			if((character != ' ') && (character != '\t') && (character != '/') && (character != '\n') && (Character.isLetter(character)
				|| Character.isDigit(character) || operators.contains(Character.toString(character))
				|| (operators.contains(Character.toString(character)+Character.toString(stringFile.charAt(index.i + 1)))) 
				|| splitters.contains(Character.toString(character)) || character == '"' || character == '\''
				|| character == '_' || character == '$')){
				
				while(index.i < stringFile.length() && stringFile.charAt(index.i) != '\n' && tokenFound == false && stringFile.charAt(index.i) != '/' && stringFile.charAt(index.i) != ' '){
					
					character = stringFile.charAt(index.i);
					lexeme = lexeme.append(character);
					
					System.out.println(lexeme);
					
					tokenFound = keywordFound(lexeme, index.i, tokenFound);
					tokenFound = splitterFound(lexeme, index.i, tokenFound);
					tokenFound = operatorFound(lexeme, index, tokenFound);
					tokenFound = identiferFound(lexeme, index.i, tokenFound);
					tokenFound = intLiteralFound(lexeme, index.i, tokenFound);
					tokenFound = charLiteralFound(lexeme, index, tokenFound);
					tokenFound = stringLiteralFound(lexeme, index, tokenFound);
					tokenFound = commentFound(index, tokenFound);
					
					resetLexeme(character, lexeme);
					
					if(tokenFound == false) {
						index.i++;
						globalColumn++;
					}
				}
			}else if((character == '/') && (stringFile.charAt(index.i + 1) == '/')){
				character = commentOnLine(character,index);
			}else if(character == '\n') {
				globalLine++;
				globalColumn = 0;
			}else if(!Character.isLetter(character) 
			   && !Character.isDigit(character)
			   && !operators.contains(Character.toString(character))
			   && index.i + 1 < stringFile.length()
			   && !operators.contains(Character.toString(character)+Character.toString(stringFile.charAt(index.i + 1)))
			   && !splitters.contains(Character.toString(character))
			   && character != ' ' && character != '\n'){
				
				errorReport(Character.toString(character));
			}
			
			lexeme.setLength(0);
			index.i++;
		}
	}

	private boolean commentFound(Index index, boolean tokenFound) {
		if(index.i + 2 < stringFile.length() && stringFile.charAt(index.i + 1) == '/' && stringFile.charAt(index.i + 2) == '/') {
			tokenFound = true;
		}
		return tokenFound;
	}

	private boolean stringLiteralFound(StringBuilder lexeme, Index index, boolean tokenFound) {
		if(lexeme.length() > 0 && lexeme.charAt(0) == '"') {
			
			index.i++;
			lexeme.append(stringFile.charAt(index.i));

			while(index.i + 1 < stringFile.length() && stringFile.charAt(index.i) != '"' && stringFile.charAt(index.i) != '\n') {
				index.i++;
				lexeme.append(stringFile.charAt(index.i));
				System.out.println(lexeme+"<");
			}
			
			if(index.i < stringFile.length() && lexeme.charAt(lexeme.length() - 1) == '"' && lexeme.charAt(0) == '"' && lexeme.length() > 1) {
				Main.tokenList.add(new Token(TokenType.StringLiteral, Integer.toString(Main.tableOfSymbols.size()), globalLine, globalColumn));
				Main.tableOfSymbols.put(Main.tableOfSymbols.size(), lexeme.toString());
				lexeme.setLength(0);
				tokenFound = true;
			}else {
				errorReport(lexeme.toString());
				lexeme.setLength(0);
			}
		}
		return tokenFound;
	}

	private void errorReport(String wordError) {
		System.out.println("ERROR: Palavra/Caracter não faz parte da linguagem:" + wordError +"\n");
	}

	private  char commentOnLine(char character, Index index) {
		while(character != '\n' && index.i < stringFile.length()) {
			index.i++;
			character = stringFile.charAt(index.i);
		}
		return character;
	}

	private boolean charLiteralFound(StringBuilder lexeme, Index index, boolean tokenFound) {
		
		if((stringFile.length() > index.i + 2 &&  stringFile.charAt(index.i) == '\'' && stringFile.charAt(index.i + 1) != '\'' 
			&& stringFile.charAt(index.i + 2) == '\'')
			|| (stringFile.length() > index.i + 1 && stringFile.charAt(index.i) == '\'' && stringFile.charAt(index.i + 1) == '\'')) {
			
			if(stringFile.charAt(index.i + 2) == '\'' && stringFile.charAt(index.i + 1) != '\''){
				Main.tokenList.add(new Token(TokenType.CharLiteral, Integer.toString(Main.tableOfSymbols.size()), globalLine, globalColumn));
				Main.tableOfSymbols.put(Main.tableOfSymbols.size(), "'" + Character.toString(stringFile.charAt(index.i + 1)) + "'");
				lexeme.setLength(0);
				tokenFound = true;
				index.i += 2;
			}else {
				Main.tokenList.add(new Token(TokenType.CharLiteral, Integer.toString(Main.tableOfSymbols.size()), globalLine, globalColumn));
				Main.tableOfSymbols.put(Main.tableOfSymbols.size(), "''");
				lexeme.setLength(0);
				tokenFound = true;
				index.i += 1;
			}
		}
		
		if(lexeme.length() != 0 && stringFile.charAt(index.i) == '\'' && ((index.i + 2 < stringFile.length() 
			&& stringFile.charAt(index.i + 2) != '\'') || stringFile.length() <= index.i + 2)){
			errorReport(lexeme.toString());
			tokenFound = true;
			lexeme.setLength(0);
		}
		return tokenFound;
	}

	private boolean intLiteralFound(StringBuilder lexeme, int i, boolean tokenFound) {
		if(lexeme.length() > 0 &&  stringFile.length() > i + 1 && Character.isDigit(lexeme.charAt(0)) 
		&& !Character.isDigit(stringFile.charAt(index.i + 1))) {
			int j = 0;
			boolean flag = false;
			
			while(j < lexeme.length() && flag == false) {
				if(!Character.isDigit(lexeme.charAt(j))) {
					flag = true;
				}else {
					j++;
				}
			}

			if(flag == false && (lexeme.charAt(0) != '0' || (lexeme.charAt(0) == '0' && lexeme.length() == 1))) {
				Main.tokenList.add(new Token(TokenType.IntLiteral, Integer.toString(Main.tableOfSymbols.size()), globalLine, globalColumn - lexeme.length()));
				Main.tableOfSymbols.put(Main.tableOfSymbols.size(), lexeme.toString());
				lexeme.setLength(0);
				tokenFound = true;
			}else if(flag == false && lexeme.charAt(0) == '0') {
				errorReport(lexeme.toString());
				tokenFound = true;
			}
		}
		return tokenFound;
	}

	private void resetLexeme(char character, StringBuilder lexeme) {
		if(character == ' ' || character == '\n') {
			lexeme.setLength(0);
		}
	}

	private boolean identiferFound(StringBuilder lexeme, int i, boolean tokenFound) {

		if(lexeme.length() > 0 && (stringFile.length() > i + 1 && (!Character.isDigit(stringFile.charAt(i + 1)) 
		    && stringFile.charAt(i + 1) != '$' && stringFile.charAt(i + 1) != '_') 
			&& !Character.isLetter(stringFile.charAt(i + 1))) && (Character.isLetter(lexeme.charAt(0)) 
			||  lexeme.charAt(0) == '_'  ||   lexeme.charAt(0) == '$') 
			&& !keywords.contains(lexeme.toString())){


			boolean flag = true;
			int j = 0;
			while(flag == true && j < lexeme.length()) {
				if(Character.isDigit(lexeme.charAt(j)) || Character.isLetter(lexeme.charAt(j)) || 
					lexeme.charAt(j) == '$' || lexeme.charAt(j) == '_') {
					j++;
				}else {
					flag = false;
				}
			}
			if(flag == true) {
				Main.tokenList.add(new Token(TokenType.Identifier, Integer.toString(Main.tableOfSymbols.size()), globalLine, globalColumn - lexeme.length()));
				Main.tableOfSymbols.put(Main.tableOfSymbols.size(), lexeme.toString());
				lexeme.setLength(0);
				tokenFound = true;
			}else {
				errorReport(lexeme.toString());;
				lexeme.setLength(0);
			}
		}
		
		return tokenFound;
	}

	private boolean operatorFound(StringBuilder lexeme, Index index, boolean tokenFound) {

		if(lexeme.length() <= 2 && lexeme.length() > 0) {
			if(operators.contains(lexeme.toString())) {
				Main.tokenList.add(new Token(TokenType.Operator, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
				lexeme.setLength(0);
				tokenFound = true;
			}else if (lexeme.length() == 2 && lexeme.charAt(0) == '=' && lexeme.charAt(1) == '=') {
				Main.tokenList.add(new Token(TokenType.Operator, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
				lexeme.setLength(0);
				tokenFound = true;
			}else if (lexeme.length() == 2 && lexeme.charAt(0) == '+' && lexeme.charAt(1) == '=') {
				Main.tokenList.add(new Token(TokenType.Operator, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
				lexeme.setLength(0);
				tokenFound = true;
			}else if (lexeme.length() == 2 && lexeme.charAt(0) == '<' && lexeme.charAt(1) == '=') {
				Main.tokenList.add(new Token(TokenType.Operator, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
				lexeme.setLength(0);
				tokenFound = true;
			}else if (stringFile.length() > index.i + 1 && lexeme.charAt(0) == '&' && stringFile.charAt(index.i + 1) != '&' ) {
				errorReport(lexeme.toString());
				lexeme.setLength(0);
				tokenFound = true;
			}
		}
		
		return tokenFound;
	}

	private boolean keywordFound(StringBuilder lexeme, int i, boolean tokenFound) {
		if(keywords.contains(lexeme.toString())) {
			Main.tokenList.add(new Token(TokenType.Keyword, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
			lexeme.setLength(0);
			tokenFound = true;
		}
		if(lexeme.toString() == "else" && i + 1 > stringFile.length() && stringFile.charAt(i + 1) == '{' ) {
			Main.tokenList.add(new Token(TokenType.Keyword, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
			lexeme.setLength(0);
			tokenFound = true;
		}
		if((lexeme.toString() == "if" || lexeme.toString() == "while") && i + 1 > stringFile.length() && stringFile.charAt(i + 1) == '(') {
			Main.tokenList.add(new Token(TokenType.Keyword, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
			lexeme.setLength(0);
			tokenFound = true;
		}
		if((lexeme.toString() == "super" || lexeme.toString() == "this" )  && i + 1 > stringFile.length() && stringFile.charAt(i + 1) == '.') {
			Main.tokenList.add(new Token(TokenType.Keyword, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
			lexeme.setLength(0);
			tokenFound = true;
		}
		
		return tokenFound;
	}

	private boolean splitterFound(StringBuilder lexeme, int i, boolean tokenFound) {
		if(lexeme.length() == 1 && splitters.contains(lexeme.toString())) {
			Main.tokenList.add(new Token(TokenType.Splitter, lexeme.toString(), globalLine, globalColumn - lexeme.length()));
			lexeme.setLength(0);
			tokenFound = true;
		}
		return tokenFound;
	}

}
