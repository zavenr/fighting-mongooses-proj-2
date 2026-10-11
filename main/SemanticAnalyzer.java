// Checks the program follows MiniLang's rules before it runs.

import java.util.HashMap;
import java.util.Map;

public class SemanticAnalyzer {
    private static class Symbol {
        final TokenType type;
        boolean initialized = false;

        Symbol(TokenType type) {
            this.type = type;
        }
    }

    private final Map<String, Symbol> symbols = new HashMap<>();

    public void analyze(AST.Program program) {
        symbols.clear();

        for (AST.Statement statement : program.statements) {
            if (statement instanceof AST.Declaration) {
                AST.Declaration declaration = (AST.Declaration) statement;

                if (symbols.containsKey(declaration.name)) {
                    throw error(declaration.line,
                            "Variable '" + declaration.name
                                    + "' is already declared.");
                }

                symbols.put(declaration.name, new Symbol(declaration.type));

            } else if (statement instanceof AST.Assignment) {
                AST.Assignment assignment = (AST.Assignment) statement;
                Symbol target = lookup(assignment.target);
                TokenType valueType = typeOf(assignment.value);

                if (target.type == TokenType.INT
                        && valueType == TokenType.REAL) {
                    throw error(assignment.line,
                            "Cannot assign real value to int variable '"
                                    + assignment.target.name + "'.");
                }

                // Check the expression before marking the target initialized.
                target.initialized = true;

            } else if (statement instanceof AST.PrintStatement) {
                AST.PrintStatement print = (AST.PrintStatement) statement;
                typeOf(print.expression);

            } else {
                throw new IllegalStateException("Unknown statement node.");
            }
        }
    }

    private TokenType typeOf(AST.Expression expression) {
        if (expression instanceof AST.IntegerLiteral) {
            return TokenType.INT;
        }

        if (expression instanceof AST.RealLiteral) {
            return TokenType.REAL;
        }

        if (expression instanceof AST.Identifier) {
            AST.Identifier identifier = (AST.Identifier) expression;
            Symbol symbol = lookup(identifier);

            if (!symbol.initialized) {
                throw error(identifier.line,
                        "Variable '" + identifier.name
                                + "' is not initialized.");
            }

            return symbol.type;
        }

        if (expression instanceof AST.BinaryExpr) {
            AST.BinaryExpr binary = (AST.BinaryExpr) expression;
            TokenType left = typeOf(binary.left);
            TokenType right = typeOf(binary.right);

            return left == TokenType.REAL || right == TokenType.REAL
                    ? TokenType.REAL
                    : TokenType.INT;
        }

        throw new IllegalStateException("Unknown expression node.");
    }

    private Symbol lookup(AST.Identifier identifier) {
        Symbol symbol = symbols.get(identifier.name);

        if (symbol == null) {
            throw error(identifier.line,
                    "Variable '" + identifier.name + "' is not declared.");
        }

        return symbol;
    }

    private MiniLangException error(int line, String message) {
        return new MiniLangException(
                MiniLangException.Category.SEMANTIC, line, message);
    }
}