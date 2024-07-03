# Lambda Interpreter.
Lambda interpreter built in Scala. Supports CbN, CbV and Free Variables calculations.

# Lambda Expression Symbols available:
- **"λ"**: The lambda symbol represents the beginning of an abstraction.
- **" "**: The space allows for separating the argument from the function in an application.
- **"."**: The dot allows for separating the argument from the body of an abstraction.
- **"("**: The left parenthesis allows for representing the beginning of an application.
- **")"**:" The right parenthesis allows for representing the end of an application.
- **string**: Any other string, different from the ones above, will be interpreted as a variable.

# IO Examples:

| Expression | Call-by-Name Result | Call-by-Value Result |
| ---------- | ------------------- | -------------------- |
| `(λx.λy.y (λx.(x x) λx.(x x)))` | `λy.y` | Infinite recursion |
| `(λx.λy.x y)` | `λy*.y` | `λy*.y` |
| `(λf.(f λx.λy.x) ((λx.λy.λf.((f x) y) a) b))` | `a` | `a` |
| `(λx.λx.(y x) z)` | `λx.(y x)` | `λx.(y x)` |
| `(λy.λb.b ((λf.λb.f b) b))` |  `λb*.b*` | `λb*.b*` |

# Commands:
- <λexp> ::= | <var> #variable | <Lambda> <var> <DOT> <λexp> #Abstraction | <LPAR> <λexp> <SPACE> <λexp> <RPAR> #Aplication
- set <reduction strategy>
-- set call-by-name (default strategy)
-- set call-by-value
-- set free-variables (returns the free variables of the expression)
- exit  

# Compilation:

The project uses Scala 3.3.3 and sbt.

For running the program open the sbt interpreter using:
````shell
sbt
````
- Compile with:
````shell
compile
````
- And execute with:
````shell
run
````
- For exiting the program:
````shell
exit
````

Note: If you compile the project on Windows, use the symbol "/" instead of the symbol "λ" as PowerShell does not support it.

Contributors:
- Valentin Cedeño [@qbixxx](https://github.com/qbixxx/)
- Valentin Calomino [@vcalomi](https://github.com/vcalomi)
- Luciano Salerno [@SalernoLuciano](https://github.com/SalernoLuciano)
