FROM openjdk:11-jdk-slim
VOLUME /tmp
COPY target/*.jar app.jar
ENV LANG=ru_RU.UTF-8
ENV LANGUAGE=ru_RU:ru
ENV LC_ALL=ru_RU.UTF-8
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-Dspring.output.ansi.enabled=ALWAYS", "-jar", "/app.jar"]


