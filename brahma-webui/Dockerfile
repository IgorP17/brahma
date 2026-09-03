FROM eclipse-temurin:21-jre

WORKDIR /app

COPY target/*-runner.jar app.jar

# Quarkus по умолчанию использует порт 8080
EXPOSE 8082

# Запуск от non-root пользователя (уже есть в UBI)
USER 1001

ENTRYPOINT ["java", "-jar", "app.jar"]