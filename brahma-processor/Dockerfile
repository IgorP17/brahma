FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*-runner.jar app.jar

# Указываем порты Processor: 8081 (HTTP) и 9000 (gRPC)
EXPOSE 8081
EXPOSE 9000

USER 1001
ENTRYPOINT ["java", "-jar", "app.jar"]