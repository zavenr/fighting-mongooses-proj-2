# MiniLang Interpreter

**Team:** Fighting Mongooses 
**Members:** Ramin Akbari, Keith Schoen, Zaven Ranum

This is an interpreter for MiniLang, a tiny programming language with int and real variables, arithmetic, and print. You give it a .mini file, and it checks the program for errors and runs it.

Every program goes through four stages, in this order:

...

.mini file -> Tokenizer -> Parser -> SemanticAnalyzer -> Interpreter -> output
...

**Tokenizer** (lexical analyzer): chops the program text into tokens like INT, IDENTIFIER(x) and SEMICOLON.
**Parser** (syntax analyzer): checks the tokens follow the MiniLang grammar and builds a tree (the AST).
**SemanticAnalyzer**: checks the program follows the language rules (variables declared, types match, and so on) before anything runs.
**Interpreter**: runs the program and prints the output.

If something is wrong at any stage, the interpreter prints a clear error message with the line number instead of crashing.

---

## How to build and run

From the top folder of the repo:

```
cd main
javac *.java
java Main
```

The program asks which file to run. Type the file name and press Enter:

```
Enter the .mini file name: program1.mini
```

For files inside the `tests` folder, include the folder name:

```
Enter the .mini file name: tests/lexical_error.mini
```

> File names are relative to the `main` folder, since that's where you run the program from.

### Example

`program1.mini`:

```
int width;
int height;
int area;
width = 10;
height = 5;
area = width * height;
print(area);
```

Expected output:

```
50
```

---

## Debug mode

TODO: explain how to turn on debug mode once it's added. It should show:

- the token stream
- the AST
- the final symbol table
- the program output

---

