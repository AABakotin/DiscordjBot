# Discord Bot

Многофункциональный Discord бот с возможностями мониторинга игровых серверов.

## Основные возможности

### Мониторинг серверов
- Поддержка Source протокола (CS:GO, Rust, TF2 и др.)
- Отображение статуса серверов
- Количество игроков
- Текущая карта
- Автоматическое обновление информации

## Установка и запуск

### Через Docker (рекомендуется)
1. Установите [Docker](https://www.docker.com/products/docker-desktop/) и Docker Compose
2. Склонируйте репозиторий:
```bash
git clone https://github.com/yourusername/DiscordjBot.git
cd DiscordjBot
```

3. Создайте файл `.env` и укажите токен бота:
```env
DISCORD_TOKEN=your-discord-bot-token-here
```

4. Запустите бота:
```bash
docker-compose up -d
```

Для остановки:
```bash
docker-compose down
```

### Стандартная установка
1. Требования:
   - Java 11 или выше
   - Maven

2. Сборка проекта:
```bash
mvn clean package
```

3. Запуск:
```bash
java -jar target/DiscordjBot-1.0-jar-with-dependencies.jar
```

## Конфигурация

При первом запуске автоматически создается файл `config.json` в директории `json/`:

```json
{
  "token": "your-bot-token",
  "owner": "your-discord-id",
  "servers": [
    {
      "ip": "server-ip",
      "port": server-port,
      "name": "server-name",
      "game": "game-type",
      "enabled": true
    }
  ]
}
```

## Команды
- `!monitor channel <id>` - установка канала для мониторинга
- `!monitor add <ip:port> <тип>` - добавление сервера
- `!monitor remove <name>` - удаление сервера
- `!monitor list` - список серверов
- `!monitor start` - запуск мониторинга
- `!monitor stop` - остановка мониторинга

## Используемые технологии
- JDA (Java Discord API)
- Docker для контейнеризации
- Source Query Protocol для опроса серверов
- Maven для сборки проекта

## Лицензия
MIT License