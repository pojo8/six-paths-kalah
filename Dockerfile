FROM openjdk:8-jdk-alpine

COPY target/kalah-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-Xmx2048m -Xms1024m"

ENTRYPOINT ["java", "-jar", "/app.jar"]