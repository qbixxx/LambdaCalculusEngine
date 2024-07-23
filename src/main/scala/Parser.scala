sealed trait AST

case class Variable(name: String) extends AST

case class Abstraccion(param: Variable, body: AST) extends AST

case class Aplicacion(func: AST, arg: AST) extends AST

object Parser {

  private def parseExpr(tokens: List[Token]): (AST, List[Token]) = tokens match {
    case TokenLambda :: TokenVariable(nombre) :: TokenPunto :: resto =>
      val (cuerpo, restoTokens) = parseExpr(resto)
      (Abstraccion(Variable(nombre), cuerpo), restoTokens)
    case TokenParIzq :: resto =>
      val (funcion, expresion) = parseExpr(resto)
      expresion match {
        case TokenEspacio :: despEspacio =>
          val (argumento, despArg) = parseExpr(despEspacio)
          despArg match {
            case TokenParDer :: tokensRestantes => (Aplicacion(funcion, argumento), tokensRestantes)
            case _ => throw new Exception("Error de sintaxis")
          }
        case _ => throw new Exception("Error de sintaxis")
      }
    case TokenVariable(nombre) :: resto => (Variable(nombre), resto)
    case _ => throw new Exception("Error de sintaxis")
  }

  def construirConAST(ast: AST): String = ast match {
    case Variable(name) => name
    case Abstraccion(param, body) =>
      val paramStr = param.name
      val bodyStr = construirConAST(body)
      s"λ$paramStr.$bodyStr"
      
    
    case Aplicacion(func, arg) =>
      val funcStr = construirConAST(func)
      val argStr = construirConAST(arg)
      s"($funcStr $argStr)"
  }

  def parse(tokens: List[Token]): AST = {
    val (ast, tokensRestantes) = parseExpr(tokens)
    ast
  }

}