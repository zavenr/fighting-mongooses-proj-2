// One exception type for every user-facing MiniLang error (lexical, syntax, semantic, runtime).
// Each stage throws it with the right category; Main catches it and prints the message
// instead of letting a raw stack trace reach the user.

public class MiniLangException extends RuntimeException {

    public enum Category {
        LEXICAL("Lexical Error"),
        SYNTAX("Syntax Error"),
        SEMANTIC("Semantic Error"),
        RUNTIME("Runtime Error");

        private final String label;

        Category(String label) {
            this.label = label;
        }
    }

    private final Category category;
    private final int line;

    public MiniLangException(Category category, int line, String detail) {
        // Produces e.g. "Syntax Error on line 2: Expected ';' after assignment."
        super(category.label + " on line " + line + ": " + detail);
        this.category = category;
        this.line = line;
    }

    public Category getCategory() {
        return category;
    }

    public int getLine() {
        return line;
    }
}
