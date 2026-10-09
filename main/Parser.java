// Checks the tokens follow the grammar and builds the AST.
//
// This is a recursive-descent parser: every grammar rule from the spec is one method,
// and each method calls the methods for the rules it contains.
//
//   <program>         -> { <statement> }                    parse()
//   <statement>       -> declaration | assignment | print   parseStatement()
//   <declaration>     -> <type> identifier ";"              parseDeclaration()
//   <assignment>      -> identifier "=" <expression> ";"    parseAssignment()
//   <print_statement> -> "print" "(" <expression> ")" ";"  parsePrint()
//   <expression>      -> <term> { ("+" | "-") <term> }      parseExpression()
//   <term>            -> <factor> { ("*" | "/") <factor> }  parseTerm()
//   <factor>          -> identifier | integer | real | "(" <expression> ")"   parseFactor()
//
// The parser stops at the first syntax error (it throws a MiniLangException).

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens; // always ends with an EOF token
    private int current = 0;          // index of the token we are looking at

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // <program> -> { <statement> }
    // Looping until EOF is also what guarantees the whole input is consumed: a stray token
    // such as ")" is not the start of any statement, so parseStatement() rejects it.
    public AST.Program parse() {
        List<AST.Statement> statements = new ArrayList<>();
        while (!check(TokenType.EOF)) {
            statements.add(parseStatement());
        }
        return new AST.Program(statements);
    }

    // ---------- statements ----------

    // The first token tells us which kind of statement this is (no backtracking needed).
    private AST.Statement parseStatement() {
        switch (peek().type) {
            case INT:
            case REAL:
                return parseDeclaration();
            case IDENTIFIER:
                return parseAssignment();
            case PRINT:
                return parsePrint();
            default:
                throw errorAtCurrent("Expected a statement (declaration, assignment, or print) but found "
                        + describe(peek()) + ".");
        }
    }

    // <declaration> -> <type> identifier ";"
    private AST.Statement parseDeclaration() {
        Token typeToken = advance(); // "int" or "real"
        Token name = expect(TokenType.IDENTIFIER, "Expected a variable name after '" + typeToken.text + "'.");
        expect(TokenType.SEMICOLON, "Expected ';' after variable declaration.");
        return new AST.Declaration(typeToken.type, name.text, typeToken.line);
    }

    // <assignment> -> identifier "=" <expression> ";"
    private AST.Statement parseAssignment() {
        Token name = advance();
        expect(TokenType.ASSIGN, "Expected '=' after '" + name.text + "' in assignment.");
        AST.Expression value = parseExpression();
        expect(TokenType.SEMICOLON, "Expected ';' after assignment.");
        return new AST.Assignment(new AST.Identifier(name.text, name.line), value, name.line);
    }

    // <print_statement> -> "print" "(" <expression> ")" ";"
    private AST.Statement parsePrint() {
        Token printToken = advance();
        expect(TokenType.LEFT_PAREN, "Expected '(' after 'print'.");
        AST.Expression value = parseExpression();
        expect(TokenType.RIGHT_PAREN, "Expected ')' after expression in print statement.");
        expect(TokenType.SEMICOLON, "Expected ';' after print statement.");
        return new AST.PrintStatement(value, printToken.line);
    }

    // ---------- expressions ----------
    //
    // Precedence comes from the grammar's layering: expression (+ -) calls term (* /),
    // which calls factor. Because "*" and "/" are handled deeper in the call chain,
    // they bind tighter, so  2 + 3 * 4  becomes  2 + (3 * 4).
    //
    // Left associativity comes from the while-loops: each pass wraps what we have so far
    // as the LEFT child of a new node, so  10 - 5 - 2  becomes  (10 - 5) - 2.

    // <expression> -> <term> { ("+" | "-") <term> }
    private AST.Expression parseExpression() {
        AST.Expression left = parseTerm();
        while (check(TokenType.PLUS) || check(TokenType.MINUS)) {
            Token op = advance();
            AST.Expression right = parseTerm();
            left = new AST.BinaryExpr(op.text.charAt(0), left, right, op.line);
        }
        return left;
    }

    // <term> -> <factor> { ("*" | "/") <factor> }
    private AST.Expression parseTerm() {
        AST.Expression left = parseFactor();
        while (check(TokenType.MULTIPLY) || check(TokenType.DIVIDE)) {
            Token op = advance();
            AST.Expression right = parseFactor();
            left = new AST.BinaryExpr(op.text.charAt(0), left, right, op.line);
        }
        return left;
    }

    // <factor> -> identifier | integer_literal | real_literal | "(" <expression> ")"
    private AST.Expression parseFactor() {
        Token token = peek();
        switch (token.type) {
            case IDENTIFIER:
                advance();
                return new AST.Identifier(token.text, token.line);

            case INTEGER_LITERAL:
                advance();
                try {
                    return new AST.IntegerLiteral(Long.parseLong(token.text), token.line);
                } catch (NumberFormatException e) {
                    throw new MiniLangException(MiniLangException.Category.SYNTAX, token.line,
                            "Integer literal '" + token.text + "' is too large.");
                }

            case REAL_LITERAL:
                advance();
                double value = Double.parseDouble(token.text);
                if (Double.isInfinite(value)) {
                    throw new MiniLangException(MiniLangException.Category.SYNTAX, token.line,
                            "Real literal '" + token.text + "' is too large.");
                }
                return new AST.RealLiteral(value, token.line);

            case LEFT_PAREN:
                advance();
                AST.Expression inner = parseExpression();
                expect(TokenType.RIGHT_PAREN, "Expected ')' to close the parenthesized expression.");
                return inner; // no node for parentheses; the tree shape already encodes them

            default:
                throw errorAtCurrent("Expected an expression but found " + describe(token) + ".");
        }
    }

    // ---------- token helpers ----------

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private boolean check(TokenType type) {
        return peek().type == type;
    }

    // Returns the current token and moves on. Never moves past EOF.
    private Token advance() {
        Token token = tokens.get(current);
        if (token.type != TokenType.EOF) {
            current++;
        }
        return token;
    }

    // Consumes a token of the required type, or reports a syntax error.
    // A "missing token" is reported on the line of the LAST token we did accept, because
    // that is where the programmer forgot it. For "int x" (newline) "x = 5;", the missing
    // ';' belongs to line 1, not to line 2 where the next token happens to be.
    private Token expect(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }
        throw new MiniLangException(MiniLangException.Category.SYNTAX, previous().line, message);
    }

    // For "this token should not be here" errors: report the line of the offending token.
    private MiniLangException errorAtCurrent(String message) {
        return new MiniLangException(MiniLangException.Category.SYNTAX, peek().line, message);
    }

    private static String describe(Token token) {
        return token.type == TokenType.EOF ? "end of file" : "'" + token.text + "'";
    }
}
