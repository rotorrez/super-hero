Hola, sí, efectivamente en nuestro caso adoptamos ese cliente porque vimos que vosotros ya lo estabais utilizando en la librería.

De hecho, intentamos inicialmente utilizar el cliente REST nativo de Quarkus con @RegisterRestClient, pero nos daba errores por mezcla de implementaciones (mixing) entre Jersey y RESTEasy, así que lo descartamos para evitar conflictos.

Ahora que vais a migrar a RESTEasy, ¿sabéis si podremos utilizar @RegisterRestClient sin ese problema de mezcla?
Nos gustaría alinearnos con el cliente estándar que Quarkus recomienda.

¡Gracias y genial iniciativa con ese cambio!

