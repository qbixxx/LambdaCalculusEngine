import scala.io.StdIn.readLine

object LambdaInterpreter {
  
  sealed trait Mode
  
  case object CallByName extends Mode
  
  case object CallByValue extends Mode
  
  case object FreeVariables extends Mode
  
  var currentMode: Mode = CallByName
  
  def main(): Unit = {
    val input = readLine("("+currentMode+")$ ")
    input match {
      case "mode" => println(currentMode)
      case "exit" => System.exit(0)
      case "set call-by-name" =>
        currentMode = CallByName
      case "set call-by-value" =>
        currentMode = CallByValue
      case "set free-variables" => currentMode = FreeVariables
      case expression =>
        val tokens = Lexer.tokenize(expression)
        val ast = Parser.parse(tokens)
        currentMode match {
          case CallByName =>
            val reducedAst = Reducer.callByName(ast)
            val reducedExp = Parser.buildWithAST(reducedAst)
            println(reducedExp)
          case CallByValue =>
            val reducedAst = Reducer.callByValue(ast)
            val reducedExp = Parser.buildWithAST(reducedAst)
            println(reducedExp)
          case FreeVariables =>
            // Only print free variables when mode is set to FreeVariables
            Reducer.printFreeVariables(ast)
        }
    }
    main()
  }
}
