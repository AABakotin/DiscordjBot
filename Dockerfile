
FROM openjdk:22
COPY target/DiscordjBot-1.0-jar-with-dependencies.jar/ /app/
WORKDIR /app/
ENTRYPOINT ["java", "-jar", "/app/DiscordjBot-1.0-jar-with-dependencies.jar"]


