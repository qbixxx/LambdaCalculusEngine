# Lambda Interpreter.
*  Lambda interpreter built in Scala. Supports CbN, CbV and Free Variables calculations.
*  Lambda Expression Symbols available:
- **"λ"**: The lambda symbol represents the beginning of an abstraction.
- **" "**: The space allows for separating the argument from the function in an application.
- **"."**: The dot allows for separating the argument from the body of an abstraction.
- **"("**: The left parenthesis allows for representing the beginning of an application.
- **")"**:" The right parenthesis allows for representing the end of an application.
- **string**: Any other string, different from the ones above, will be interpreted as a variable.

Contributors:
- Valentin Cedeño [@qbixxx](https://github.com/qbixxx/)
- Valentin Calomino [@vcalomi](https://github.com/vcalomi)
- Luciano Salerno [@SalernoLuciano](https://github.com/SalernoLuciano)

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
The following Lambda Expression gives an example for beta reduction and alpha conversion:
````shell
(λy.λb.b ((λf.λb.f b) b))
````

Note: If you compile the project from Windows, use the symbol "/" instead of the symbol "λ" as PowerShell does not support it.
