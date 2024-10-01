sealed trait Token
case object TokenLambda extends Token
case object TokenDot extends Token
case object TokenLeftParen extends Token
case object TokenRightParen extends Token
case object TokenSpace extends Token
case class TokenVariable(name: String) extends Token

object Lexer {
  def tokenize(input: String): List[Token] = input.toList match {
    case Nil => List.empty
    case ('λ' | '/') :: tail => TokenLambda :: tokenize(tail.mkString)
    case '.' :: tail => TokenDot :: tokenize(tail.mkString)
    case '(' :: tail => TokenLeftParen :: tokenize(tail.mkString)
    case ')' :: tail => TokenRightParen :: tokenize(tail.mkString)
    case ' ' :: tail => TokenSpace :: tokenize(tail.mkString)
    case _ =>
      val (variable, rest) = input.span(c => c != ' ' && c != '/' && c != 'λ' && c != '.' && c != '(' && c != ')')
      TokenVariable(variable) :: tokenize(rest)
  }
}
