FROM eclipse-temurin:21

COPY /out/artifacts/checkinn_jar/checkinn.jar app.jar

EXPOSE 5000

ENTRYPOINT ["java" , "-jar" , "/app.jar"]