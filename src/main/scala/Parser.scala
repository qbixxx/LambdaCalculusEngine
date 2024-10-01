sealed trait AST

case class Variable(name: String) extends AST

case class Abstraction(param: Variable, body: AST) extends AST

case class Application(func: AST, arg: AST) extends AST

object Parser {
  
  private def parseExpr(tokens: List[Token]): (AST, List[Token]) = tokens match {
    case TokenLambda :: TokenVariable(name) :: TokenDot :: rest =>
      val (body, remainingTokens) = parseExpr(rest)
      (Abstraction(Variable(name), body), remainingTokens)
    case TokenLeftParen :: rest =>
      val (func, expr) = parseExpr(rest)
      expr match {
        case TokenSpace :: afterSpace =>
          val (arg, afterArg) = parseExpr(afterSpace)
          afterArg match {
            case TokenRightParen :: remainingTokens => (Application(func, arg), remainingTokens)
            case _ => throw new Exception("Syntax error")
          }
        case _ => throw new Exception("Syntax error")
      }
    case TokenVariable(name) :: rest => (Variable(name), rest)
    case _ => throw new Exception("Syntax error")
  }
  
  def buildWithAST(ast: AST): String = ast match {
    case Variable(name) => name
    case Abstraction(param, body) =>
      val paramStr = param.name
      val bodyStr = buildWithAST(body)
      s"λ$paramStr.$bodyStr"
    case Application(func, arg) =>
      val funcStr = buildWithAST(func)
      val argStr = buildWithAST(arg)
      s"($funcStr $argStr)"
  }
  
  def parse(tokens: List[Token]): AST = {
    val (ast, remainingTokens) = parseExpr(tokens)
    ast
  }
  
}