#!/usr/bin/env bash

#export MAVEN_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
java $JAVA_OPTS -jar target/quarkus-app/quarkus-run.jar
