FROM openjdk:17-jdk-slim
COPY target/superhero-1.0.0-SNAPSHOT.jar /superhero.jar
CMD ["java", "-jar", "superhero.jar"]