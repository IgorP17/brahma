export MAVEN_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"
# mvn quarkus:dev
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar