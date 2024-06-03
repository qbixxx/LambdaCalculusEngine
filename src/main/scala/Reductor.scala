object Reductor {
  private def conversionAlpha(ast: AST, nombreViejo: String, nombreNuevo: String): AST = ast match {
    case Variable(nombre) if nombre == nombreViejo => Variable(nombreNuevo)
    case Variable(nombre) => Variable(nombre)
    case Abstraccion(Variable(parametro), cuerpo) if parametro == nombreViejo =>
      Abstraccion(Variable(nombreNuevo), conversionAlpha(cuerpo, nombreViejo, nombreNuevo))
    case Abstraccion(Variable(parametro), cuerpo) =>
      Abstraccion(Variable(parametro), conversionAlpha(cuerpo, nombreViejo, nombreNuevo))
    case Aplicacion(funcion, argumento) =>
      Aplicacion(conversionAlpha(funcion, nombreViejo, nombreNuevo), conversionAlpha(argumento, nombreViejo, nombreNuevo))
  }
  private def sustituir(ast: AST, parametro: String, reemplazo: AST): AST = ast match {
    case Variable(nombre) if nombre == parametro => reemplazo
    case Variable(nombre) => Variable(nombre)
    case Abstraccion(Variable(nombre), cuerpo) if nombre == parametro => Abstraccion(Variable(nombre), cuerpo)
    case Abstraccion(Variable(nombre), cuerpo) =>
      if (variablesLibres(reemplazo).contains(nombre)) {
        val nuevoNombre = nombre + "*"
        val cuerpoRenombrado = conversionAlpha(cuerpo, nombre, nuevoNombre)
        Abstraccion(Variable(nuevoNombre), sustituir(cuerpoRenombrado, parametro, reemplazo))
      } else {
        Abstraccion(Variable(nombre), sustituir(cuerpo, parametro, reemplazo))
      }
    case Aplicacion(func, argumento) =>
      Aplicacion(sustituir(func, parametro, reemplazo), sustituir(argumento, parametro, reemplazo))
  }
  private def betaReduce(ast: AST): AST = ast match {
    case Aplicacion(Abstraccion(Variable(nombre), cuerpo), argumento) =>
      if (variablesLibres(argumento).contains(nombre)) {
        val nuevoNombre = nombre + "*"
        val cuerpoRenombrado = conversionAlpha(cuerpo, nombre, nuevoNombre)
        sustituir(cuerpoRenombrado, nuevoNombre, argumento)
      } else {
        sustituir(cuerpo, nombre, argumento)
      }
    case _ => ast
  }
  def callByName(ast: AST): AST = ast match {
    case Aplicacion(Abstraccion(Variable(parametro), cuerpo), argumento) =>
      callByName(betaReduce(Aplicacion(Abstraccion(Variable(parametro), cuerpo), argumento)))
    case Aplicacion(func, argumento) =>
      val funcionReducida = callByName(func)
      funcionReducida match {
        case Abstraccion(Variable(parametro), cuerpo) =>
          callByName(Aplicacion(funcionReducida, argumento))
        case _ => Aplicacion(funcionReducida, argumento)
      }
    case Abstraccion(Variable(parametro), cuerpo) =>
      Abstraccion(Variable(parametro), callByName(cuerpo))
    case Variable(nombre) => Variable(nombre)
  }
  def callByValue(ast: AST): AST = ast match {
    case Aplicacion(func, argumento) =>
      val funcionReducida = callByValue(func)
      val argumentoReducido = callByValue(argumento)
      funcionReducida match {
        case Abstraccion(Variable(parametro), cuerpo) =>
          callByValue(betaReduce(Aplicacion(funcionReducida, argumentoReducido)))
        case _ => Aplicacion(funcionReducida, argumentoReducido)
      }
    case Abstraccion(Variable(parametro), cuerpo) =>
      Abstraccion(Variable(parametro), callByValue(cuerpo))
    case Variable(nombre) => Variable(nombre)
  }
  def variablesLibres(ast: AST): Set[String] = ast match {
    case Variable(nombre) => Set(nombre)
    case Abstraccion(Variable(parametro), cuerpo) => variablesLibres(cuerpo) - parametro
    case Aplicacion(func, argumento) => variablesLibres(func) ++ variablesLibres(argumento)
  }
}
