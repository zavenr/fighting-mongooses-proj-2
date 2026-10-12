# MiniLang Interpreter

**Team:** Fighting Mongooses
**Members:** Ramin Akbari, Keith Schoen, Zaven Ranum

This is an interpreter for **MiniLang**, a tiny programming language with `int` and `real` variables, arithmetic (`+ - * /`), parentheses, and `print`. You give it a `.mini` file, and it checks the program for errors and then runs it.

Every program goes through four stages, in this order:

```
.mini file -> Tokenizer -> Parser -> SemanticAnalyzer -> Interpreter -> output
```

1. **Tokenizer** (lexical analyzer): chops the program text into tokens like `INT`, `IDENTIFIER(x)` and `SEMICOLON`.
2. **Parser** (syntax analyzer): checks that the tokens follow the MiniLang grammar and builds a tree (the AST).
3. **SemanticAnalyzer**: checks that the program follows the language rules *before* anything runs (variables declared once and before use, assigned before being read, and no real values stored in int variables).
4. **Interpreter**: runs the program and prints the output.

If something is wrong at any stage, the interpreter prints a clear error message with the line number instead of crashing.

---

## How to build and run

From the top folder of the repo:

```
cd main
javac *.java
java Main
```

The program asks two questions. Type your answer and press Enter after each one:

```
Enter the .mini file name: program1.mini
Debug mode? (y/n): n
```

- **File name:** the path to the `.mini` file, starting from the `main` folder. Test programs are in the `tests` folder, so type them like `tests/precedence.mini`.
- **Debug mode:** `y` shows every stage's work (see below). `n` shows only the program's output or an error message.

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

Output:

```
Enter the .mini file name: program1.mini
Debug mode? (y/n): n
50
```

---

## Debug mode

Answer `y` to the debug question to see what each stage produced, in this order:

1. **TOKENS**: every token from the tokenizer
2. **AST**: the tree built by the parser
3. **PROGRAM OUTPUT**: what the program printed
4. **SYMBOL TABLE**: every variable's name, type, and final value

Example with `program1.mini` (token list shortened):

```
Enter the .mini file name: program1.mini
Debug mode? (y/n): y
TOKENS
------
INT
IDENTIFIER(width)
SEMICOLON
...
RIGHT_PAREN
SEMICOLON
EOF

AST
---
Program
|-- Declaration(int width)
|-- Declaration(int height)
|-- Declaration(int area)
|-- Assignment(=)
|   |-- Identifier(width)
|   `-- Integer(10)
|-- Assignment(=)
|   |-- Identifier(height)
|   `-- Integer(5)
|-- Assignment(=)
|   |-- Identifier(area)
|   `-- Binary(*)
|       |-- Identifier(width)
|       `-- Identifier(height)
`-- Print
    `-- Identifier(area)

PROGRAM OUTPUT
--------------
50

SYMBOL TABLE
------------
width   int     10
height  int     5
area    int     50
```

If the program has an error, debug mode shows the stages that finished before the error, then the error message.

---

## Project files

All the code is in the `main` folder. Each stage has its own file.

| File | What it does |
|---|---|
| `Main.java` | Starts everything: asks for the file, reads it, runs each stage in order, handles debug mode, and prints errors |
| `MiniLangError.java` | The one error class every stage uses, so all errors look the same |
| `Token.java` | Defines a token and the list of all token types |
| `Tokenizer.java` | Stage 1: turns program text into tokens |
| `AST.java` | The tree classes that represent a parsed program, plus the code that draws the tree for debug mode |
| `Parser.java` | Stage 2: checks the grammar and builds the AST |
| `SemanticAnalyzer.java` | Stage 3: checks the language rules before running |
| `Symbols.java` | The symbol table: each variable's name, type, whether it has a value, and its value |
| `Interpreter.java` | Stage 4: runs the program |
| `program1.mini` | Sample program from the project spec |
| `tests/` | Our test programs (see the test suite below) |

---

## Error messages

Every error shows its category, the line, and what went wrong:

| Category | Example |
|---|---|
| Lexical | `Lexical Error on line 3: Unknown character '@'` |
| Syntax | `Syntax Error on line 1: Expected ';' after variable declaration.` |
| Semantic | `Semantic Error on line 1: Variable 'x' is not declared.` |
| Runtime | `Runtime Error on line 5: Division by zero.` |

---


## Test suite

All test programs are in `main/tests/` (except `program1.mini`, which is in `main/`). Run each one the same way: type the file name when asked and answer `n` for debug.

| Test file | What it tests | Expected result |
|---|---|---|
| `program1.mini` | Declarations, assignment, `int` variables, `*`, print | `50` |
| `tests/precedence.mini` | Arithmetic, operator precedence, left-to-right order | `14`<br>`3`<br>`2` |
| `tests/parentheses.mini` | Parentheses (including nested) | `20`<br>`12` |
| `tests/real_mixed.mini` | `real` variables, mixed `int` and `real` math | `20`<br>`3`<br>`3.5`<br>`2.5` |
| `tests/lexical_error.mini` | Lexical error: unknown character | `Lexical Error on line 3: Unknown character '@'` |
| `tests/bad_real.mini` | Lexical error: invalid real number | `Lexical Error on line 2: Invalid real number '10.'` |
| `tests/syntax_error.mini` | Syntax error: missing `;` | `Syntax Error on line 1: Expected ';' after variable declaration.` |
| `tests/undeclared.mini` | Variable used before it's declared | `Semantic Error on line 1: Variable 'x' is not declared.` |
| `tests/duplicate_declaration.mini` | Variable declared twice | `Semantic Error on line 1: Variable 'x' is already declared.` |
| `tests/uninitialized.mini` | Variable read before it has a value | `Semantic Error on line 3: Variable 'x' is not initialized.` |
| `tests/type_error.mini` | Real value assigned to an int variable | `Semantic Error on line 2: Cannot assign real value to int variable 'count'.` |
| `tests/division_by_zero.mini` | Division by zero | `Runtime Error on line 5: Division by zero.` |

---