FROM openjdk:12-jdk-alpine
COPY parsers-manager-service-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
EXPOSE 7003