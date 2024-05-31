object Reductor {
  def conversionAlpha(ast: AST, nombreViejo: String, nombreNuevo: String): AST = ast match {
    case Variable(nombre) if nombre == nombreViejo => Variable(nombreNuevo)
    case Variable(nombre) => Variable(nombre)
    case Abstraccion(Variable(parametro), cuerpo) if parametro == nombreViejo =>
      Abstraccion(Variable(nombreNuevo), conversionAlpha(cuerpo, nombreViejo, nombreNuevo))
    case Abstraccion(Variable(parametro), cuerpo) =>
      Abstraccion(Variable(parametro), conversionAlpha(cuerpo, nombreViejo, nombreNuevo))
    case Aplicacion(funcion, argumento) =>
      Aplicacion(conversionAlpha(funcion, nombreViejo, nombreNuevo), conversionAlpha(argumento, nombreViejo, nombreNuevo))
  }
}