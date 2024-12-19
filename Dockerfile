# Используем официальный образ Maven для сборки
FROM maven:3.8.4-openjdk-11-slim AS builder

# Копируем файлы проекта
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Используем минимальный образ JRE для запуска
FROM openjdk:11-jre-slim

# Создаем рабочую директорию
WORKDIR /app

# Копируем собранный jar из предыдущего этапа
COPY --from=builder /app/target/DiscordjBot-1.0-jar-with-dependencies.jar ./bot.jar

# Создаем директорию для конфигурации
RUN mkdir -p /app/json

# Запускаем бота
CMD ["java", "-jar", "bot.jar"]


