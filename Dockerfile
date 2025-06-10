FROM amazoncorretto:21-alpine-jdk

COPY /out/artifacts/checkinn_jar/checkinn.jar app.jar

ENTRYPOINT ["java" , "-jar" , "/app.jar"]