# Используем официальный образ Maven для сборки на Java 25
FROM maven:3.9-eclipse-temurin-25 AS builder

# Копируем файлы проекта
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Собираем проект
RUN mvn clean package -DskipTests

# Минимальный образ JRE 25 для запуска
FROM eclipse-temurin:25-jre

# Создаем рабочую директорию
WORKDIR /app

# Копируем собранный jar из предыдущего этапа
COPY --from=builder /app/target/DiscordjBot-jar-with-dependencies.jar app.jar

# Создаем директорию для конфигурации
RUN mkdir -p /app/json

# Запускаем бота
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "app.jar"]


