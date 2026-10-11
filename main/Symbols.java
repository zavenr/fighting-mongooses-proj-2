// Stores every variable's name, type, and value.

import java.util.ArrayList;

class Symbol {
    String name;
    TokenType type; // TokenType.INT or TokenType.REAL
    boolean initialized; // true once the variable has been given a value
    Number value;

    Symbol(String name, TokenType type) {
        this.name = name;
        this.type = type;
        this.initialized = false;
        this.value = null;
    }
}

public class Symbols {
    private ArrayList<Symbol> table = new ArrayList<>();

    // Adds a new variable to the table.
    public void declare(String name, TokenType type) {
        Symbol newSymbol = new Symbol(name, type);
        table.add(newSymbol);
    }

    // Finds a variable by its name. Returns null if it isn't in the table.
    public Symbol lookup(String name) {
        for (int i = 0; i < table.size(); i++) {
            Symbol current = table.get(i);
            if (current.name.equals(name)) {
                return current;
            }
        }
        return null;
    }

    // Prints every variable for debug mode, like: width int 10
    public void print() {
        for (int i = 0; i < table.size(); i++) {
            Symbol s = table.get(i);

            String typeName;
            if (s.type == TokenType.INT) {
                typeName = "int";
            } else {
                typeName = "real";
            }

            String valueText;
            if (!s.initialized) {
                valueText = "(no value)";
            } else if (s.type == TokenType.INT) {
                valueText = "" + (int) s.value.longValue(); // show 10, not 10.0
            } else {
                valueText = "" + s.value;
            }

            System.out.println(s.name + "\t" + typeName + "\t" + valueText);
        }
    }
}