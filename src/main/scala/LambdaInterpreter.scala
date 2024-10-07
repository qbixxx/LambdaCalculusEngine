import scala.io.StdIn.readLine

object LambdaInterpreter {
  
  // Definimos el nuevo modo
  sealed trait Mode
  
  case object CallByName extends Mode
  
  case object CallByValue extends Mode
  
  case object FreeVariables extends Mode
  
  case object PrintAST extends Mode // Nuevo modo para imprimir el AST
  
  var currentMode: Mode = CallByName
  
  def main(): Unit = {
    val input = readLine("(" + currentMode + ")$ ")
    input match {
      case "mode" => println(currentMode)
      case "exit" => System.exit(0)
      case "set call-by-name" =>
        currentMode = CallByName
      case "set call-by-value" =>
        currentMode = CallByValue
      case "set free-variables" =>
        currentMode = FreeVariables
      case "set ast" =>
        currentMode = PrintAST // Cambia al modo PrintAST
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
            Reducer.printFreeVariables(ast)
          case PrintAST =>
            // Imprime el AST como árbol en el modo PrintAST
            Reducer.printASTAsTree(ast)
        }
    }
    main() // Loop recursivo
  }
}
