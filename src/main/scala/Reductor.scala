object Reducer {
  private def alphaConversion(ast: AST, oldName: String, newName: String): AST = ast match {
    case Variable(name) if name == oldName => Variable(newName)
    case Variable(name) => Variable(name)
    case Abstraction(Variable(param), body) if param == oldName =>
      Abstraction(Variable(newName), alphaConversion(body, oldName, newName))
    case Abstraction(Variable(param), body) =>
      if (freeVariables(body).contains(newName)) {
        val newParam = newName + "*"
        Abstraction(Variable(newParam), alphaConversion(body, oldName, newParam))
      } else {
        Abstraction(Variable(param), alphaConversion(body, oldName, newName))
      }
    case Application(func, arg) =>
      Application(alphaConversion(func, oldName, newName), alphaConversion(arg, oldName, newName))
  }
  
  private def substitute(ast: AST, param: String, replacement: AST): AST = ast match {
    case Variable(name) if name == param => replacement
    case Variable(name) => Variable(name)
    case Abstraction(Variable(name), body) if name == param => Abstraction(Variable(name), body)
    case Abstraction(Variable(name), body) =>
      if (freeVariables(replacement).contains(name)) {
        val newName = name + "*"
        val renamedBody = alphaConversion(body, name, newName)
        Abstraction(Variable(newName), substitute(renamedBody, param, replacement))
      } else {
        Abstraction(Variable(name), substitute(body, param, replacement))
      }
    case Application(func, arg) =>
      Application(substitute(func, param, replacement), substitute(arg, param, replacement))
  }
  
  private def betaReduce(ast: AST): AST = ast match {
    case Application(Abstraction(Variable(name), body), arg) =>
      if (freeVariables(arg).contains(name)) {
        val newName = name + "*"
        val renamedBody = alphaConversion(body, name, newName)
        substitute(renamedBody, newName, arg)
      } else {
        substitute(body, name, arg)
      }
    case _ => ast
  }
  
  def callByName(ast: AST): AST = ast match {
    case Application(Abstraction(Variable(param), body), arg) =>
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
  
  def callByValue(ast: AST): AST = ast match {
    case Application(func, arg) =>
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
  
  def freeVariables(ast: AST): Set[String] = ast match {
    case Variable(name) => Set(name)
    case Abstraction(Variable(param), body) => freeVariables(body) - param
    case Application(func, arg) => freeVariables(func) ++ freeVariables(arg)
  }
}

private def generateUniqueName(base: String, usedNames: Set[String]): String = {
  var newName = base + "*"
  var counter = 1
  while (usedNames.contains(newName)) {
    newName = base + "*" + counter
    counter += 1
  }
  newName
}