# TP2-Interprete Lambda.

Grupo 12 - Los Devaluados:
- Valentin Cedeño
- Valentin Calomino
- Luciano Salerno
- Damian Lima

El proyecto utiliza Scala 3.3.3 y sbt.

Para ejecutar hay que abrir el interprete sbt utilizando:
````shell
sbt
````
- luego para compilar:
````shell
compile
````
- y para ejecutar:
````shell
run
````
- finalmente para terminar el programa usar:
````shell
exit
````
Se deja a continuación una expresion lambda de ejemplo que demuestra la reducción beta y la conversión alfa:
````shell
(λy.λb.b ((λf.λb.f b) b))
````

Nota: si compilas el proyecto desde windows utilizar el simbolo "/" en vez del simbolo "λ" ya que powershell no lo 
soporta.
