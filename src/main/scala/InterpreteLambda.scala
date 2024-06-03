import scala.io.StdIn.readLine

object InterpreteLambda {

  sealed trait Modo

  case object ModoNormal extends Modo

  case object CallByValue extends Modo

  case object VariablesLibres extends Modo

  var modoActual: Modo = ModoNormal

  def main(): Unit = {
    val input = readLine("$ ")
    input match {
      case "mode" => println(modoActual)
      case "exit" => System.exit(0)
      case "set call-by-name" =>
        modoActual = ModoNormal
      case "set call-by-value" =>
        modoActual = CallByValue
      case "set free-variables" => modoActual = VariablesLibres
      case expresion =>
        val tokens = Lexer.tokenize(expresion)
        val ast = Parser.parse(tokens)
        modoActual match {
          case ModoNormal =>
            val astReducido = Reductor.callByName(ast)
            val expReducida = Parser.buildFromAst(astReducido)
            println(expReducida)
          case CallByValue =>
            val astReducido = Reductor.callByValue(ast)
            val expReducida = Parser.buildFromAst(astReducido)
            println(expReducida)
          case VariablesLibres =>
            val variablesLibres = Reductor.variablesLibres(ast)
            println(variablesLibres)
        }
    }
    main()
  }
}
