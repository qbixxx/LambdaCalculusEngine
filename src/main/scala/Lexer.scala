sealed trait Token
case object TokenLambda extends Token
case object TokenPunto extends Token
case object TokenParIzq extends Token
case object TokenParDer extends Token
case object TokenEspacio extends Token
case class TokenVariable(name: String) extends Token

object Lexer {
  def tokenize(input: String): List[Token] = input.toList match {
    case Nil => List.empty
    case 'λ' :: tail => TokenLambda :: tokenize(tail.mkString)
    case '/' :: tail => TokenLambda :: tokenize(tail.mkString)
    case '.' :: tail => TokenPunto :: tokenize(tail.mkString)
    case '(' :: tail => TokenParIzq :: tokenize(tail.mkString)
    case ')' :: tail => TokenParDer :: tokenize(tail.mkString)
    case ' ' :: tail => TokenEspacio :: tokenize(tail.mkString)
    case _ =>
      val (variable, rest) = input.span(c => c != ' ' && c != 'λ' && c != '.' && c != '(' && c != ')')
      TokenVariable(variable) :: tokenize(rest)
  }
}