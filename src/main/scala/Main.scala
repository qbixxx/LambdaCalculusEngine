object Main {
  def main(args: Array[String]): Unit = {
    println("Ingrese una expresión lambda:")
    val input = scala.io.StdIn.readLine()
    val tokens = Lexer.tokenize(input)
    println("Tokens:")
    //tokens.foreach(println)
    println(tokens)
  }
}