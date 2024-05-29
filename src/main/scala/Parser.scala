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
          }
      }
    case TokenVariable(nombre) :: resto => (Variable(nombre), resto)
  }


}