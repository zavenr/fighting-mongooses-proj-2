// Defines what a token is and all the token types.
enum TokenType{
    INT, REAL, PRINT,
    IDENTIFIER, INTEGER_LITERAL, REAL_LITERAL,
    ASSIGN, PLUS, MINUS, MULTIPLY, DIVIDE,
    LEFT_PAREN, RIGHT_PAREN, SEMICOLON,
    EOF
}

public class Token{
    public TokenType type;
    public String text;
    public int line;

    public Token(TokenType type, String text, int line){
        this.type = type;
        this.text = text;
        this.line = line;
    }

}