# Используем официальный образ Maven для сборки на Java 17
FROM maven:3.8.4-openjdk-17-slim AS builder

# Копируем файлы проекта
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Минимальный образ JRE 17 для запуска
FROM eclipse-temurin:17-jre

# Создаем рабочую директорию
WORKDIR /app

# Копируем собранный jar из предыдущего этапа
COPY --from=builder /app/target/DiscordjBot-jar-with-dependencies.jar ./bot.jar

# Создаем директорию для конфигурации
RUN mkdir -p /app/json

# Запускаем бота
CMD ["java", "-jar", "bot.jar"]


