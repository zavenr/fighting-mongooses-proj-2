
import java.util.ArrayList;

// Breaks the program text into tokens.

public class Tokenizer {
    private String sourceCode;  // the whole program as one string
    private ArrayList<Token> tokens = new ArrayList<>();

    public Tokenizer(String sourceCode){
        this.sourceCode = sourceCode;

    }

    //goes through the program and returns all the tokens
    public ArrayList<Token> tokenize(){
        // We split into lines so we always know the line number for error messages.
        String[] lines = sourceCode.split("\n");
        int lineNum = 0;

        for(int i = 0; i < lines.length; i++){
            lineNum = i + 1; // arrays start at 0 but line numbers start at 1
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

    /**
     *   Decides if a word is a keyword or a variable name.
     *   We read the WHOLE word first and then check it, so something like "printer"
     *   becomes one identifier instead of the keyword "print" followed by "er".
     */
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

    // Turns a one-character symbol into its token.
    // Anything that isn't a MiniLang symbol (like @ or $) is a lexical error.
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
    
    // Goes through one line and adds its tokens to the list.
    private void tokenizeLine(String text, int line){
        int i = 0;

        while(i < text.length()){
            char c = text.charAt(i);
            // Spaces and tabs just separate tokens, so skip them.
            if (c == ' ' || c == '\t' || c == '\r') {
                i++;
            }
            /**  Starts with a letter: keep grabbing letters, digits and underscores
             *   until the word ends.
             */
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
