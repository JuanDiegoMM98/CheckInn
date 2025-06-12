FROM openjdk:21-jdk-slim

COPY /out/artifacts/checkinn_jar/checkinn-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java" , "-jar" , "/app.jar"]

