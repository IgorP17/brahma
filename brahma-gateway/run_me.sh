#!/usr/bin/env bash

# Опции для Maven (только для сборки, не влияет на запуск JAR)
#export MAVEN_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Сборка всего проекта (включая brahma-common, brahma-gateway, brahma-processor, brahma-webui)
# Это гарантирует, что brahma-common установлен в ~/.m2/repository
# mvn clean install
# mvn clean package

# Проверка, успешна ли сборка
#if [ $? -ne 0 ]; then
#  echo "Build failed!"
#  exit 1
#fi

# Опции для запуска JAR (влияют на JVM приложения brahma-processor)
JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

# Запуск JAR brahma-processor
java $JAVA_OPTS -jar target/quarkus-app/quarkus-run.jar
#java -Dquarkus.log.level=DEBUG -Dquarkus.log.category."io.quarkus.grpc".level=DEBUG -jar target/quarkus-app/quarkus-run.jar
## mvn quarkus:dev