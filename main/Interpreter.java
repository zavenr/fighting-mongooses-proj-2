// Runs the program and prints the output.

public class Interpreter {
    private Symbols symbols = new Symbols();

    public void execute(AST.Program program) {
        // Start each program with an empty runtime table.
        symbols = new Symbols();

        for (AST.Statement statement : program.statements) {
            if (statement instanceof AST.Declaration) {
                AST.Declaration declaration = (AST.Declaration) statement;
                symbols.declare(declaration.name, declaration.type);

            } else if (statement instanceof AST.Assignment) {
                AST.Assignment assignment = (AST.Assignment) statement;
                Symbol target = requireSymbol(assignment.target);
                Number value = evaluate(assignment.value);

                // Promote integer values when assigning to real variables.
                if (target.type == TokenType.REAL) {
                    target.value = value.doubleValue();
                } else {
                    target.value = value.longValue();
                }

                target.initialized = true;

            } else if (statement instanceof AST.PrintStatement) {
                AST.PrintStatement print = (AST.PrintStatement) statement;
                Number result = evaluate(print.expression);

                // Whole-number reals print without ".0" so the spec's example prints 20, not 20.0.
                if (result instanceof Double && result.doubleValue() == Math.floor(result.doubleValue())) {
                    System.out.println(result.longValue());
                } else {
                    System.out.println(result);
                }

            } else {
                throw new IllegalStateException("Unknown statement node.");
            }
        }
    }

    // Return a Long for an integer result or a Double for a real result.
    private Number evaluate(AST.Expression expression) {
        if (expression instanceof AST.IntegerLiteral) {
            return ((AST.IntegerLiteral) expression).value;
        }

        if (expression instanceof AST.RealLiteral) {
            return ((AST.RealLiteral) expression).value;
        }

        if (expression instanceof AST.Identifier) {
            AST.Identifier identifier = (AST.Identifier) expression;
            Symbol symbol = requireSymbol(identifier);

            if (!symbol.initialized) {
                throw error(identifier.line,
                        "Variable '" + identifier.name
                                + "' is not initialized.");
            }

            return symbol.value;
        }

        if (expression instanceof AST.BinaryExpr) {
            AST.BinaryExpr binary = (AST.BinaryExpr) expression;

            // evaluate calls itself for both children, following the AST precedence.
            Number left = evaluate(binary.left);
            Number right = evaluate(binary.right);

            // Report division by zero before performing the calculation.
            if (binary.operator == '/' && right.doubleValue() == 0.0) {
                throw error(binary.line, "Division by zero.");
            }

            // If either operand is real, perform the operation using doubles.
            if (left instanceof Double || right instanceof Double) {
                double a = left.doubleValue();
                double b = right.doubleValue();

                switch (binary.operator) {
                    case '+':
                        return a + b;
                    case '-':
                        return a - b;
                    case '*':
                        return a * b;
                    case '/':
                        return a / b;
                    default:
                        throw new IllegalStateException("Unknown operator.");
                }
            } else {
                // Integer arithmetic: 7 / 2 gives 3.
                long a = left.longValue();
                long b = right.longValue();

                switch (binary.operator) {
                    case '+':
                        return a + b;
                    case '-':
                        return a - b;
                    case '*':
                        return a * b;
                    case '/':
                        return a / b;
                    default:
                        throw new IllegalStateException("Unknown operator.");
                }
            }
        }

        throw new IllegalStateException("Unknown expression node.");
    }

    private Symbol requireSymbol(AST.Identifier identifier) {
        Symbol symbol = symbols.lookup(identifier.name);

        if (symbol == null) {
            throw error(identifier.line,
                    "Variable '" + identifier.name + "' is not declared.");
        }

        return symbol;
    }

    private MiniLangError error(int line, String message) {
        return new MiniLangError("Runtime", line, message);
    }

    public Symbols getSymbols() {
        return symbols;
    }
}
