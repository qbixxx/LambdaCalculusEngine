import scala.io.StdIn.readLine

object InterpreteLambda {

  sealed trait Modo

  case object CallByName extends Modo

  case object CallByValue extends Modo

  case object VariablesLibres extends Modo

  var modoActual: Modo = CallByName

  def main(): Unit = {
    val input = readLine("("+modoActual+")$ ")
    input match {
      case "mode" => println(modoActual)
      case "exit" => System.exit(0)
      case "set call-by-name" =>
        modoActual = CallByName
      case "set call-by-value" =>
        modoActual = CallByValue
      case "set free-variables" => modoActual = VariablesLibres
      case expresion =>
        val tokens = Lexer.tokenize(expresion)
        val ast = Parser.parse(tokens)
        modoActual match {
          case CallByName =>
            val astReducido = Reductor.callByName(ast)
            val expReducida = Parser.construirConAST(astReducido)
            println(expReducida)
          case CallByValue =>
            val astReducido = Reductor.callByValue(ast)
            val expReducida = Parser.construirConAST(astReducido)
            println(expReducida)
          case VariablesLibres =>
            val variablesLibres = Reductor.variablesLibres(ast)
            println(variablesLibres)
        }
    }
    main()
  }
}
