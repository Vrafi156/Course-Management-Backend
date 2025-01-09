FROM openjdk:17
ADD target/*.jar homework-3.3.5.jar
ENTRYPOINT ["java", "-jar", "homework-3.3.5.jar"]
EXPOSE 8000