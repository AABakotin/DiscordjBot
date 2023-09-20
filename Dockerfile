FROM openjdk:12-alpine
COPY target/DiscordjBot-1.0-jar-with-dependencies.jar/ /app/
COPY /.env/ /app/
WORKDIR /app/
ENTRYPOINT ["java", "-jar", "/app/DiscordjBot-1.0-jar-with-dependencies.jar"]