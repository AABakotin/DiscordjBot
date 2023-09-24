FROM openjdk:11.0.8-jre-slim
COPY target/DiscordjBot-1.0-jar-with-dependencies.jar/ /app/
WORKDIR /app/
ENTRYPOINT ["java", "-jar", "/app/DiscordjBot-1.0-jar-with-dependencies.jar"]


