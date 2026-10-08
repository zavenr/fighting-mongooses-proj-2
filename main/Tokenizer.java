
import java.util.ArrayList;

// Breaks the program text into tokens.

public class Tokenizer {
    private String sourceCode;
    private ArrayList<Token> tokens = new ArrayList<>();

    public Tokenizer(String sourceCode){
        this.sourceCode = sourceCode;

    }

    public ArrayList<Token> tokenize(){
        String[] lines = sourceCode.split("\n");
        int lineNum = 0;

        for(int i = 0; i < lines.length; i++){
            lineNum = i + 1;
            tokenizeLine(lines[i], lineNum);
        }
        tokens.add(new Token(TokenType.EOF, "", lines.length));
        return tokens;
    }

    private boolean isLetter(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private void tokenizeWord(String word, int line){
         if (word.equals("int")) {
            tokens.add(new Token(TokenType.INT, word, line));
        } else if (word.equals("real")) {
            tokens.add(new Token(TokenType.REAL, word, line));
        } else if (word.equals("print")) {
            tokens.add(new Token(TokenType.PRINT, word, line));
        } else {
            tokens.add(new Token(TokenType.IDENTIFIER, word, line));
        }
    }

    private void tokenizeSymbol(char c, int line){
        switch(c){
            case '=':
                tokens.add(new Token(TokenType.ASSIGN, "=", line));
                break;
            case '+':
                tokens.add(new Token(TokenType.PLUS, "+", line));
                break;
            case '-':
                tokens.add(new Token(TokenType.MINUS, "-", line));
                break;
            case '*':
                tokens.add(new Token(TokenType.MULTIPLY, "*", line));
                break;
            case '/':
                tokens.add(new Token(TokenType.DIVIDE, "/", line));
                break;
            case '(':
                tokens.add(new Token(TokenType.LEFT_PAREN, "(", line));
                break;
            case ')':
                tokens.add(new Token(TokenType.RIGHT_PAREN, ")", line));
                break;
            case ';':
                tokens.add(new Token(TokenType.SEMICOLON, ";", line));
                break;
            default:
                throw new MiniLangError("Lexical", line, "Unexpected character: " + c + "'");
        }
    }
    
    
    private void tokenizeLine(String text, int line){
        int i = 0;

        while(i < text.length()){
            char c = text.charAt(i);
            if (c == ' ' || c == '\t' || c == '\r') {
                i++;
            }
            else if(isLetter(c)){
                String word = "";
                while (i < text.length() && (isLetter(text.charAt(i))
                        || isDigit(text.charAt(i)) || text.charAt(i) == '_')) {
                    word = word + text.charAt(i);
                    i++;
                }
                tokenizeWord(word, line);
            }
            else if(isDigit(c)){
                String number = "";
                while(i < text.length() && isDigit(text.charAt(i))){
                    number = number + text.charAt(i);
                    i++;
                }
                if(i < text.length() && text.charAt(i) == '.'){
                    number = number + ".";
                    i++;

                    if(i >= text.length() || !isDigit(text.charAt(i))){
                        throw new MiniLangError("Lexical", line,
                            "Invalid real number '" + number + "'");
                    }
                    while(i < text.length() && isDigit(text.charAt(i))){
                        number = number + text.charAt(i);
                        i++;
                    }
                    tokens.add(new Token(TokenType.REAL_LITERAL, number, line));
                } 
                else{
                    tokens.add(new Token(TokenType.INTEGER_LITERAL, number, line));
                }
            }

            // Anything else should be a one-character symbol like + or ;
            else{
                tokenizeSymbol(c, line);
                i++;
            }
        }
    }

    

}
