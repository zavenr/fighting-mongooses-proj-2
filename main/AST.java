// The tree structure that represents the program after parsing.
//
// Every node remembers the source line it came from so the semantic analyzer and the
// interpreter can report useful errors without ever looking at the original text.
//
// Shape of the tree (matches section 8 of the project spec):
//   Program -> Statement*
//   Statement  = Declaration | Assignment | PrintStatement
//   Expression = Identifier | IntegerLiteral | RealLiteral | BinaryExpr
// Parentheses do not get a node of their own: they only change how the tree is shaped,
// and the shape is what carries precedence and associativity.

import java.util.ArrayList;
import java.util.List;

public class AST {

    // ---------- base classes ----------

    public abstract static class Node {
        public final int line;

        Node(int line) {
            this.line = line;
        }

        // One-line description used when printing the tree.
        abstract String label();

        List<? extends Node> children() {
            return List.of();
        }
    }

    public abstract static class Statement extends Node {
        Statement(int line) {
            super(line);
        }
    }

    public abstract static class Expression extends Node {
        Expression(int line) {
            super(line);
        }
    }

    // ---------- program ----------

    public static class Program extends Node {
        public final List<Statement> statements;

        public Program(List<Statement> statements) {
            super(1);
            this.statements = statements;
        }

        @Override String label() { return "Program"; }
        @Override List<? extends Node> children() { return statements; }
    }

    // ---------- statements ----------

    // int x;   /   real y;
    public static class Declaration extends Statement {
        public final TokenType type;   // TokenType.INT or TokenType.REAL
        public final String name;

        public Declaration(TokenType type, String name, int line) {
            super(line);
            this.type = type;
            this.name = name;
        }

        @Override String label() {
            return "Declaration(" + (type == TokenType.INT ? "int" : "real") + " " + name + ")";
        }
    }

    // x = <expression>;
    public static class Assignment extends Statement {
        public final Identifier target;
        public final Expression value;

        public Assignment(Identifier target, Expression value, int line) {
            super(line);
            this.target = target;
            this.value = value;
        }

        @Override String label() { return "Assignment(=)"; }
        @Override List<? extends Node> children() { return List.of(target, value); }
    }

    // print(<expression>);
    public static class PrintStatement extends Statement {
        public final Expression expression;

        public PrintStatement(Expression expression, int line) {
            super(line);
            this.expression = expression;
        }

        @Override String label() { return "Print"; }
        @Override List<? extends Node> children() { return List.of(expression); }
    }

    // ---------- expressions ----------

    public static class Identifier extends Expression {
        public final String name;

        public Identifier(String name, int line) {
            super(line);
            this.name = name;
        }

        @Override String label() { return "Identifier(" + name + ")"; }
    }

    public static class IntegerLiteral extends Expression {
        public final long value;

        public IntegerLiteral(long value, int line) {
            super(line);
            this.value = value;
        }

        @Override String label() { return "Integer(" + value + ")"; }
    }

    public static class RealLiteral extends Expression {
        public final double value;

        public RealLiteral(double value, int line) {
            super(line);
            this.value = value;
        }

        @Override String label() { return "Real(" + value + ")"; }
    }

    // left <operator> right, where operator is one of + - * /
    public static class BinaryExpr extends Expression {
        public final char operator;
        public final Expression left;
        public final Expression right;

        public BinaryExpr(char operator, Expression left, Expression right, int line) {
            super(line);
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        @Override String label() { return "Binary(" + operator + ")"; }
        @Override List<? extends Node> children() { return List.of(left, right); }
    }

    // ---------- display ----------

    // Returns the tree as indented text, e.g.
    //   Program
    //   `-- Assignment(=)
    //       |-- Identifier(y)
    //       `-- Binary(+)
    //           |-- Identifier(x)
    //           `-- Integer(5)
    public static String render(Node root) {
        StringBuilder sb = new StringBuilder();
        sb.append(root.label()).append('\n');
        renderChildren(root, "", sb);
        return sb.toString();
    }

    private static void renderChildren(Node node, String prefix, StringBuilder sb) {
        List<? extends Node> kids = new ArrayList<>(node.children());
        for (int i = 0; i < kids.size(); i++) {
            boolean last = (i == kids.size() - 1);
            Node kid = kids.get(i);
            sb.append(prefix).append(last ? "`-- " : "|-- ").append(kid.label()).append('\n');
            renderChildren(kid, prefix + (last ? "    " : "|   "), sb);
        }
    }
}
