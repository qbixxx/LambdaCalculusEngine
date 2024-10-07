object Reducer {
  
  // ANSI escape codes for colors (you can disable them if not supported)
  val RED = "\u001b[31m"
  val GREEN = "\u001b[32m"
  val YELLOW = "\u001b[33m"
  val BLUE = "\u001b[34m"
  val CYAN = "\u001b[36m"
  val RESET = "\u001b[0m"
  
  // Alpha conversion - renaming bound variables to avoid collision
  private def alphaConversion(ast: AST, oldName: String, newName: String): AST = ast match {
    case Variable(name) if name == oldName =>
      println(s"${YELLOW}Alpha conversion: renaming $oldName to $newName${RESET}")
      Variable(newName)
    case Variable(name) => Variable(name)
    case Abstraction(Variable(param), body) if param == oldName =>
      println(s"${YELLOW}Alpha conversion: renaming $param to $newName${RESET}")
      Abstraction(Variable(newName), alphaConversion(body, oldName, newName))
    case Abstraction(Variable(param), body) =>
      if (freeVariables(body).contains(newName)) {
        val newParam = newName + "*"
        println(s"${YELLOW}Alpha conversion: renaming $newName to avoid collision, new name: $newParam${RESET}")
        Abstraction(Variable(newParam), alphaConversion(body, oldName, newParam))
      } else {
        Abstraction(Variable(param), alphaConversion(body, oldName, newName))
      }
    case Application(func, arg) =>
      Application(alphaConversion(func, oldName, newName), alphaConversion(arg, oldName, newName))
  }
  
  // Substitution - replaces the variable with the argument in the body
  private def substitute(ast: AST, param: String, replacement: AST): AST = ast match {
    case Variable(name) if name == param => replacement
    case Variable(name) => Variable(name)
    case Abstraction(Variable(name), body) if name == param => Abstraction(Variable(name), body)
    case Abstraction(Variable(name), body) =>
      if (freeVariables(replacement).contains(name)) {
        val newName = name + "*"
        println(s"${YELLOW}Alpha conversion needed during substitution: renaming $name to $newName${RESET}")
        val renamedBody = alphaConversion(body, name, newName)
        Abstraction(Variable(newName), substitute(renamedBody, param, replacement))
      } else {
        Abstraction(Variable(name), substitute(body, param, replacement))
      }
    case Application(func, arg) =>
      Application(substitute(func, param, replacement), substitute(arg, param, replacement))
  }
  
  // Beta reduction
  private def betaReduce(ast: AST): AST = ast match {
    case Application(Abstraction(Variable(name), body), arg) =>
      println(s"${GREEN}Beta reduction: applying argument ${Parser.buildWithAST(arg)} to function λ$name.${Parser.buildWithAST(body)}${RESET}")
      if (freeVariables(arg).contains(name)) {
        val newName = name + "*"
        val renamedBody = alphaConversion(body, name, newName)
        println(s"${GREEN}After alpha conversion: ${Parser.buildWithAST(renamedBody)}${RESET}")
        val result = substitute(renamedBody, newName, arg)
        println(s"${GREEN}After beta reduction: ${Parser.buildWithAST(result)}${RESET}")
        result
      } else {
        val result = substitute(body, name, arg)
        println(s"${GREEN}After beta reduction: ${Parser.buildWithAST(result)}${RESET}")
        result
      }
    case _ => ast
  }
  
  // Call-by-name reduction
  def callByName(ast: AST): AST = ast match {
    case Application(Abstraction(Variable(param), body), arg) =>
      println(s"${RED}Reducing (λ$param.${Parser.buildWithAST(body)} ${Parser.buildWithAST(arg)}) by Call-by-Name${RESET}")
      callByName(betaReduce(Application(Abstraction(Variable(param), body), arg)))
    case Application(func, arg) =>
      val reducedFunc = callByName(func)
      reducedFunc match {
        case Abstraction(Variable(param), body) =>
          callByName(Application(reducedFunc, arg))
        case _ => Application(reducedFunc, callByName(arg))
      }
    case Abstraction(Variable(param), body) =>
      Abstraction(Variable(param), callByName(body))
    case Variable(name) => Variable(name)
  }
  
  // Call-by-value reduction
  def callByValue(ast: AST): AST = ast match {
    case Application(func, arg) =>
      println(s"${RED}Reducing (Application ${Parser.buildWithAST(func)} ${Parser.buildWithAST(arg)}) by Call-by-Value${RESET}")
      val reducedFunc = callByValue(func)
      val reducedArg = callByValue(arg)
      reducedFunc match {
        case Abstraction(Variable(param), body) =>
          callByValue(betaReduce(Application(reducedFunc, reducedArg)))
        case _ => Application(reducedFunc, reducedArg)
      }
    case Abstraction(Variable(param), body) =>
      Abstraction(Variable(param), callByValue(body))
    case Variable(name) => Variable(name)
  }
  
  // Finding free variables in the expression
  def freeVariables(ast: AST): Set[String] = ast match {
    case Variable(name) => Set(name)
    case Abstraction(Variable(param), body) => freeVariables(body) - param
    case Application(func, arg) => freeVariables(func) ++ freeVariables(arg)
  }
  
  // Print free variables of the expression (only called when mode is set to FreeVariables)
  def printFreeVariables(ast: AST): Unit = {
    val freeVars = freeVariables(ast)
    if (freeVars.nonEmpty) {
      println(s"${BLUE}Free variables: ${freeVars.mkString(", ")}${RESET}")
    } else {
      println(s"${BLUE}No free variables found${RESET}")
    }
  }
  
  // Función para imprimir el AST en forma de árbol usando caracteres ASCII con colores
  private def printAST(node: AST, prefix: String = "", isLast: Boolean = true): List[String] = {
    node match {
      case Variable(name) =>
        List(prefix + (if (isLast) "└─" else "├─") + s"${CYAN}Variable($name)${RESET}")
      
      case Abstraction(param, body) =>
        val abstrNode = prefix + (if (isLast) "└─" else "├─") + s"${GREEN}λ${RESET}"
        val paramNode = prefix + (if (isLast) "   " else "│  ") + s"${CYAN}└─Bound(${param.name})${RESET}"
        val bodyTree = printAST(body, prefix + (if (isLast) "   " else "│  "), true)
        List(abstrNode, paramNode) ++ bodyTree
      
      case Application(func, arg) =>
        val currentLine = List(prefix + (if (isLast) "└─" else "├─") + s"${YELLOW}app${RESET}")
        val nextPrefix = prefix + (if (isLast) "   " else "│  ")
        val leftSubtree = printAST(func, nextPrefix, false)
        val rightSubtree = printAST(arg, nextPrefix, true)
        currentLine ++ leftSubtree ++ rightSubtree
    }
  }
  
  // Función principal para invocar la impresión
  def printASTAsTree(root: AST): Unit = {
    val lines = printAST(root)
    lines.foreach(println)
  }
}
