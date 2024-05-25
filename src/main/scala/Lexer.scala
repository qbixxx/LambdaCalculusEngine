sealed trait Token
case class TokenLambda() extends Token
case class TokenDot() extends Token
case class TokenParIzq() extends Token
case class TokenParDer() extends Token
case class TokenEspacio() extends Token
case class TokenVariable(name: String) extends Token

object Lexer {
  def tokenize(input: String): List[Token] = input.toList match {
    case Nil => List.empty
    case 'λ' :: tail => TokenLambda() :: tokenize(tail.mkString)
    case '.' :: tail => TokenDot() :: tokenize(tail.mkString)
    case '(' :: tail => TokenParIzq() :: tokenize(tail.mkString)
    case ')' :: tail => TokenParDer() :: tokenize(tail.mkString)
    case ' ' :: tail => TokenEspacio() :: tokenize(tail.mkString)
    case _ =>
      val (variable, rest) = input.span(c => c != ' ' && c != 'λ' && c != '.' && c != '(' && c != ')')
      TokenVariable(variable) :: tokenize(rest)
  }
}
