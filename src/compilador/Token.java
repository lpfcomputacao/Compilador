package compilador;

public class Token {
    private TokenType name;
    private String lexeme;
    private int column;
    private int line;
    
    public Token(TokenType nome, String lexema, int line, int column) {
        this.name = nome;
        this.lexeme = lexema;
        this.line = line;
        this.column = column;
    }
    
    @Override
    public String toString() {
    	return "<"+name+","+lexeme+","+column+","+line+">\n";
    }
}
