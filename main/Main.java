// Main.java
// Starts the interpreter: reads the .mini file and runs it through each stage.
//
// The stages always run in this order, and each one uses what the last one made:
//   Tokenizer -> Parser -> SemanticAnalyzer -> Interpreter

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Ask which .mini file to run (type the path if it's in another folder).
        Scanner keyboard = new Scanner(System.in);
        System.out.print("Enter the .mini file name: ");
        String fileName = keyboard.nextLine();

        // Debug mode shows every stage's work: tokens, tree, output, then symbol table.
        System.out.print("Debug mode? (y/n): ");
        String answer = keyboard.nextLine();
        boolean debug = answer.equalsIgnoreCase("y");

        // Read the whole file into one String.
        // nextLine() drops the line break, so we add "\n" back. The tokenizer needs
        // those to know where each line ends (that's how it gets line numbers).
        String sourceCode = "";
        try {
            Scanner fileReader = new Scanner(new File(fileName));
            while (fileReader.hasNextLine()) {
                sourceCode = sourceCode + fileReader.nextLine() + "\n";
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("Error: could not find file '" + fileName + "'");
            return;
        }

        // We make the interpreter out here so we can still print its symbol table
        // in debug mode, even if a runtime error stops the program partway through.
        Interpreter interpreter = new Interpreter();

        // Run the stages. Every stage throws a MiniLangError when it finds a problem,
        // so this one try/catch handles errors from ALL of them. That's how we print
        // a clean message instead of crashing with a stack trace.
        try {
            // Stage 1: text -> tokens
            Tokenizer tokenizer = new Tokenizer(sourceCode);
            ArrayList<Token> tokens = tokenizer.tokenize();

            if (debug) {
                System.out.println("TOKENS");
                System.out.println("------");
                for (int i = 0; i < tokens.size(); i++) {
                    System.out.println(tokens.get(i));
                }
                System.out.println();
            }

            // Stage 2: tokens -> tree (AST)
            Parser parser = new Parser(tokens);
            AST.Program program = parser.parse();

            if (debug) {
                System.out.println("AST");
                System.out.println("---");
                System.out.println(AST.render(program));
            }

            // Stage 3: check the rules BEFORE running anything
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            analyzer.analyze(program);

            // Stage 4: run it
            if (debug) {
                System.out.println("PROGRAM OUTPUT");
                System.out.println("--------------");
            }
            interpreter.execute(program);

        } catch (MiniLangError e) {
            System.out.println(e.getErrorReport());
        }

        if (debug) {
            System.out.println();
            System.out.println("SYMBOL TABLE");
            System.out.println("------------");
            interpreter.getSymbols().print();
        }
    }
}