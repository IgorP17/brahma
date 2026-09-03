FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*-runner.jar app.jar
EXPOSE 8080
USER 1001
ENTRYPOINT ["java", "-jar", "app.jar"]