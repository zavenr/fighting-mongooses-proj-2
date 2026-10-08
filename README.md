# Fighting Mongooses — Project 2S

### 1. Project Overview

In this project, you will design and implement an interpreter for a small programming language called MiniLang. You will implement the same core language using the official grammar and language rules provided below. The purpose is not to build a production compiler, but to apply the major concepts studied in programming language implementation in one coherent system.

Your completed interpreter will read a MiniLang source file, convert the source text into tokens, verify that the token sequence follows the grammar, build an internal representation, perform static semantic checks, and execute valid programs. Invalid programs must be rejected with meaningful diagnostic messages rather than causing the interpreter to crash.

### 2. Required Processing

Your interpreter must process each source program through the following logical stages. These stages may be implemented as separate classes/modules or another clearly organized design.

```text
MiniLang Source File
        |
        v
+-------------------+
| Lexical Analyzer  | -> token stream
+-------------------+
        |
        v
+-------------------+
| Parser            | -> AST / parse tree
+-------------------+
        |
        v
+-------------------+
| Semantic Analyzer | -> declaration/type checks
+-------------------+
        |
        v
+-------------------+
| Interpreter       | -> program output
+-------------------+
```

### 3. MiniLang Grammar

The grammar below defines the required core language. Implement it exactly as specified. Do not remove or change required productions. Any optional extension must preserve compatibility with this grammar and must be clearly separated from the required implementation.

```text
<program>         -> { <statement> }
<statement>       -> <declaration>
                   | <assignment>
                   | <print_statement>
<declaration>     -> <type> identifier ";"
<type>            -> "int" | "real"
<assignment>      -> identifier "=" <expression> ";"
<print_statement> -> "print" "(" <expression> ")" ";"
<expression>      -> <term> { ("+" | "-") <term> }
<term>            -> <factor> { ("*" | "/") <factor> }
<factor>          -> identifier
                   | integer_literal
                   | real_literal
                   | "(" <expression> ")"
```

### 4. Lexical Specification

#### 4.1 Keywords

```text
int real print
```

Keywords are reserved and cannot be used as identifiers.

#### 4.2 Identifiers

An identifier must begin with a letter. After the first character, it may contain letters, digits, and underscores. Identifiers are case-sensitive.

| Valid          | Invalid                    |
| -------------- | -------------------------- |
| `x`            | `2value`                   |
| `total_score`  | `$total`                   |
| `student1`     | `real` (reserved keyword)  |
| `temperature2` | `print` (reserved keyword) |

#### 4.3 Numeric Literals

- **Integer literal:** one or more digits, such as `0`, `10`, or `250`.
- **Real literal:** digits followed by a decimal point and one or more digits, such as `3.14` or `10.5`.

#### 4.4 Operators and Delimiters

| Symbol | Token / Role  | Meaning              |
| ------ | ------------- | -------------------- |
| `+`    | `PLUS`        | addition             |
| `-`    | `MINUS`       | subtraction          |
| `*`    | `MULTIPLY`    | multiplication       |
| `/`    | `DIVIDE`      | division             |
| `=`    | `ASSIGN`      | assignment           |
| `(`    | `LEFT_PAREN`  | left parenthesis     |
| `)`    | `RIGHT_PAREN` | right parenthesis    |
| `;`    | `SEMICOLON`   | statement terminator |

Spaces, tabs, and new lines separate tokens but otherwise do not affect program meaning. Your lexer should track line numbers so later stages can report useful errors.

### 5. Operator Precedence and Associativity

The parser must implement the precedence encoded by the grammar. From highest to lowest:

1. Parentheses
2. Multiplication (`*`) and division (`/`)
3. Addition (`+`) and subtraction (`-`)

Operators at the same precedence level are evaluated from left to right.

```text
10 - 5 - 2  -> (10 - 5) - 2   -> 3
2 + 3 * 4   -> 2 + (3 * 4)    -> 14
(2 + 3) * 4 -> 20
```

### 6. Lexical Analyzer

Implement a lexer/tokenizer that reads the source program and produces a sequence of tokens. At minimum, recognize `INT`, `REAL`, `PRINT`, `IDENTIFIER`, `INTEGER_LITERAL`, `REAL_LITERAL`, `ASSIGN`, `PLUS`, `MINUS`, `MULTIPLY`, `DIVIDE`, `LEFT_PAREN`, `RIGHT_PAREN`, and `SEMICOLON`.

**Source:**

```text
int x; x = 10 + 5;
```

**Tokens:**

```text
INT
IDENTIFIER(x)
SEMICOLON
IDENTIFIER(x)
ASSIGN
INTEGER_LITERAL(10)
PLUS
INTEGER_LITERAL(5)
SEMICOLON
```

#### Lexical Error Requirement

Unknown or invalid characters must produce a clear lexical error containing, whenever possible, the line number and offending character. The interpreter must terminate gracefully or recover in a controlled manner; it must not crash with an unhandled exception.

**Input:**

```text
x = 10 @ 5;
```

**Output:**

```text
Lexical Error on line 1: Unknown character '@'
```

### 7. Syntax Analyzer / Parser

Implement a parser that determines whether the token stream follows the official MiniLang grammar. A recursive-descent parser is a natural choice for this grammar, although another appropriate strategy may be used if it preserves the required behavior and precedence rules.

The parser should consume the entire input. A program is not valid if unconsumed or unexpected tokens remain after parsing.

**Valid:**

```text
int x;
x = 10 + 5;
```

**Invalid:**

```text
int x
x = + 10;
```

Syntax errors should identify the line and, whenever practical, the expected token or construct. Example:

```text
Syntax Error on line 1: Expected ';' after variable declaration.
```

### 8. AST / Internal Program Representation

After parsing, represent the program in a structured form such as an Abstract Syntax Tree (AST). The representation should preserve the grammatical structure needed by the semantic analyzer and interpreter without depending on the original source text.

At minimum, your representation should distinguish declarations, assignments, print statements, identifiers, numeric literals, and binary arithmetic expressions. Expression nodes must preserve precedence and associativity established by the parser.

**Example source:**

```text
y = x + 5 * 2;
```

**AST:**

```text
Assignment
    |
    =
   / \
Identifier(y)   Binary(+)
                 /     \
        Identifier(x)   Binary(*)
                         /     \
                  Integer(5)   Integer(2)
```

Your project must provide a way to display the AST or parse tree for demonstration and testing.

### 9. Symbol Table

Maintain a symbol table for declared variables. The semantic analyzer and interpreter will use this table to track information about identifiers.

| Field       | Required Information                  |
| ----------- | ------------------------------------- |
| Name        | Identifier name                       |
| Type        | `int` or `real`                       |
| Initialized | Whether a value has been assigned     |
| Value       | Current runtime value, when available |

The final symbol table must be displayable in debug/display mode.

### 10. Static Semantic Analysis

A program may be syntactically correct while still violating MiniLang language rules. Before normal execution, your semantic analyzer must enforce all rules below.

#### Rule 1 — Variables Must Be Declared Before Use

Any identifier used in an assignment or expression must already have a declaration.

#### Rule 2 — Duplicate Declarations Are Not Allowed

A variable name may be declared only once in the program.

#### Rule 3 — Assignment Types Must Be Compatible

An `int` value may be assigned to a `real` variable. A `real` value may not be assigned to an `int` variable.

#### Rule 4 — Arithmetic Expression Types

```text
int  op int  -> int
int  op real -> real
real op int  -> real
real op real -> real
```

Here, `op` is `+`, `-`, `*`, or `/`.

#### Rule 5 — Variables Must Be Initialized Before Their Values Are Used

A declared variable cannot be read in an expression until a value has been assigned to it.

**Examples of semantic errors:**

```text
x = 10; int x;            // use before declaration
int x; real x;            // duplicate declaration
int x; x = 10.5;          // incompatible assignment
int x; int y; y = x + 5;  // x is uninitialized
```

### 11. Interpreter / Program Execution

Execute programs that successfully pass lexical, syntax, and semantic analysis. The interpreter must evaluate expressions according to the AST structure, update variable values in the symbol table, and execute print statements in source order.

- Evaluate integer and real literals.
- Read initialized variable values from the symbol table.
- Evaluate `+`, `-`, `*`, and `/` with the required precedence and left associativity.
- Apply MiniLang numeric promotion rules when an expression mixes `int` and `real` values.
- Update the assigned variable after a successful assignment.
- Display the value of the expression supplied to `print(...)`.
- Detect division by zero and report a meaningful runtime error instead of crashing.

### 12. Error Handling

Your interpreter must distinguish the major error categories below. Error wording does not need to match these examples exactly, but messages must be specific enough to help a programmer locate and understand the problem.

| Category | Example                                                                       |
| -------- | ----------------------------------------------------------------------------- |
| Lexical  | `Lexical Error on line 1: Unknown character '@'`                              |
| Syntax   | `Syntax Error on line 2: Expected ';' after assignment.`                      |
| Semantic | `Semantic Error on line 3: Variable 'x' is not declared.`                     |
| Semantic | `Semantic Error on line 4: Cannot assign real value to int variable 'count'.` |
| Runtime  | `Runtime Error on line 5: Division by zero.`                                  |

Do not use raw stack traces or unhandled exceptions as the normal user-facing error report.

### 13. Input and Output Requirements

#### 13.1 Input

The program must accept a MiniLang source-code file, for example `program1.mini`. Your README must explain exactly how the instructor should run the interpreter with a source file.

```text
int width;
int height;
int area;
width = 10;
height = 5;
area = width * height;
print(area);
```

#### 13.2 Normal Output

For a valid program, display the output produced by MiniLang print statements.

```text
50
```

#### 13.3 Debug / Display Mode

Your project must provide a debug/display mode that can show the token stream, AST or parse tree, final symbol table, and program output. The exact command-line flag or menu design is your choice, but it must be documented in the README.

```text
TOKENS
---
INT
IDENTIFIER(width)
...

SYMBOL TABLE
---
width int 10
height int 5
area int 50

PROGRAM OUTPUT
---
50
```

### 14. Required Test Suite

Each team must submit at least **10 MiniLang test programs**. Because the list below contains more than 10 required behaviors, a single well-designed test file may cover multiple behaviors. Your complete suite, taken together, must cover every item below and include both valid and invalid programs.

- Basic variable declaration and assignment
- Arithmetic expressions
- Operator precedence
- Parentheses
- `int` variables
- `real` variables
- Mixed `int` and `real` expressions
- Undeclared variable
- Duplicate declaration
- Uninitialized variable
- Syntax error
- Type error
- Lexical error
- Division by zero

For each test, include the expected result (program output or expected error category/message) so the test can be reproduced and verified.

### 15. Implementation and Code Quality Expectations

- Use a clear modular design. Avoid placing the entire interpreter in one large method or class.
- Use meaningful class, method, variable, and token names.
- Separate language-processing responsibilities when practical (for example: lexer, parser, AST, semantic analyzer, symbol table, interpreter).
- Avoid hard-coded behavior that only works for the sample programs. The interpreter will be tested with additional valid and invalid inputs.
- Document non-obvious design decisions and important algorithms.
- Remove unused code, debugging clutter, generated build files, and unrelated files from the final submission.
- The project must build and run using the instructions supplied in your README.

### 16. Git and GitHub Requirements

Your team must use Git and GitHub throughout development. All students must make meaningful contributions and meaningful commits. Do not wait until the project is complete and upload everything in a single commit.

| Good Commit Messages                         | Avoid   |
| -------------------------------------------- | ------- |
| Implement tokenizer for arithmetic operators | update  |
| Add parsing for variable declarations        | stuff   |
| Implement symbol table                       | fix     |
| Add type checking for assignments            | final   |
| Fix multiplication precedence bug            | project |

Commit history should show incremental development across the major project components. Both team members should be able to explain the code and design during demonstration or evaluation.

### 17. Submission

Before submitting, verify that the repository can be cloned into a clean location and that the project can be built and executed using only the instructions in the README.

When you are ready to submit your work, create a Git bundle containing your repository history using the following command:

```sh
git bundle create <team_name>.bundle --all
```

Replace `<team_name>` with your own team names. For example, if I were creating a bundle for my repository, I would use:

```sh
git bundle create team_awesome.bundle --all
```

It is important to use the `<team_name>` format as it helps identify you for grading purposes.

Next, submit your bundle file on Canvas before the deadline.

### 18. Example End-to-End Program

```text
int x;
real y;
x = 10;
y = x + 5 * 2;
print(y);
```

**Expected program output:**

```text
20
```

This example should tokenize successfully, parse according to the grammar, create the appropriate expression structure, pass semantic analysis, promote the integer expression value as needed for assignment to a real variable, and execute successfully.
