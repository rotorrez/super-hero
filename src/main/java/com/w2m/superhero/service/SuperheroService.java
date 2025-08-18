En IntelliJ, basta con marcar una carpeta como Test Sources Root y él sabe qué es código de test.
En Visual Studio Code (VS Code) el manejo es diferente porque no tiene esa opción gráfica, depende de la configuración y de la extensión de Java que uses.

Pasos para ejecutar tests en VS Code

Extensiones necesarias

Instala las extensiones oficiales de Java:

Extension Pack for Java

Dentro de este pack está Test Runner for Java (fundamental para ejecutar tests JUnit).

Estructura de carpetas esperada (Maven/Gradle)

Si tu proyecto es Maven o Gradle (que parece ser tu caso), VS Code ya reconoce por convención:

src/main/java      → código principal
src/test/java      → código de test


No necesitas marcar manualmente las carpetas como en IntelliJ.

VS Code detecta automáticamente que src/test/java contiene tests.

Cómo ejecutar

Abre tu clase de test en VS Code (ej: CardProcessServiceImplUnitTest.java).

Encima de cada método anotado con @Test, VS Code muestra un botón verde ▶ para Run Test o Debug Test.

También puedes hacer clic derecho en el nombre de la clase de test → Run Tests in Current File.

O desde la vista lateral “Testing” (icono del beaker 🧪) → aparecen todos los tests detectados, ahí puedes ejecutarlos en bloque.

Ejecución por línea de comandos (alternativa)

Si quieres ejecutar desde terminal (independiente de VS Code):

./mvnw test        # si usas Maven Wrapper
mvn test           # si usas Maven
./gradlew test     # si usas Gradle Wrapper
gradle test        # si usas Gradle


✅ Resumen:
En VS Code no marcas carpetas como en IntelliJ.
Si tienes src/test/java, las extensiones de Java lo reconocen automáticamente como carpeta de tests.
Después puedes lanzar tus @Test desde los botones verdes, desde la vista “Testing” o con Maven/Gradle en terminal.
