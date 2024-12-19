# Discord Bot
Многофункциональный Discord бот с возможностями музыкального плеера и системой автоматической выдачи ролей.
## Основные возможности
### Музыкальный плеер
- `/play [URL/название]` - воспроизведение музыки с YouTube
- `/skip` - пропуск текущего трека
- `/stop` - остановка воспроизведения
- `/queue` - просмотр очереди воспроизведения
- `/repeat` - включение/выключение повтора
- `/nowplaying` - информация о текущем треке
- `/clearplaylist` - очистка плейлиста
### Система ролей
- `!role [channel_id] [role_id] [emoji_id]` - добавление правила автовыдачи роли
- `!del_role [index]` - удаление правила по индексу
- `!del_role all` - удаление всех правил
- Автоматическая выдача ролей при реакции на сообщение
### Общие команды
- `/ping` - проверка работоспособности бота
- `/rules` - отображение правил сервера
- `/info` - информация о сервере
- `/hello` - приветственное сообщение
- `/invite` - получение ссылки-приглашения
### Команды конфигурации
- `!token [token]` - установка токена бота
- `!id` - установка ID администратора
- `!id_del` - удаление ID администратора
- `!link [url]` - установка ссылки приглашения
- `!read_conf` - просмотр текущей конфигурации
### Команды конфигурации мониторинга
- `!monitor channel <id>` - установка канала для мониторинга
- `!monitor add <ip:port> <тип>` - добавление сервера
- `!monitor remove <name>` - удаление сервера
- `!monitor list` - список серверов
- `!monitor start` - запуск мониторинга
- `!monitor stop` - остановка мониторинга
### Мониторинг серверов
- Поддержка Source протокола (CS:GO, Rust, TF2 и др.)
- Отображение статуса серверов
- Количество игроков
- Текущая карта
- Автоматическое обновление информации
## Установка и настройка
### Системные требования
- Java 11 или выше
- Maven для сборки проекта
- Discord Bot Token
### Сборка проекта
```bash
mvn clean package
```
Собранный JAR-файл будет находиться в директории `target/DiscordjBot-1.0-jar-with-dependencies.jar`
### Конфигурация
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
#### Способы настройки токена (в порядке приоритета):
1. Аргумент командной строки при запуске
2. Переменная окружения `TOKEN`
3. Значение в `config.json`
### Запуск
```bash
java -jar target/DiscordjBot-1.0-jar-with-dependencies.jar [token]
```
### Через Docker (рекомендуется)
1. Установите [Docker](https://www.docker.com/products/docker-desktop/) и Docker Compose
2. Склонируйте репозиторий:
```bash
git clone https://github.com/megabart/DiscordjBot.git
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
## Настройка автовыдачи ролей
1. Создайте роль на сервере Discord
2. Получите необходимые ID:
   - ID канала (ПКМ по каналу → Копировать ID)
   - ID роли (Настройки сервера → Роли → Копировать ID)
   - ID эмодзи (Поставьте эмодзи и скопируйте его)
3. Добавьте правило командой:
   ```
   !role channel_id role_id emoji_id
   ```
   ## Используемые технологии
### Основные библиотеки
- JDA (Java Discord API) 5.2.1 - основной фреймворк для работы с Discord
- LavaPlayer 2.2.2 - библиотека для воспроизведения аудио
- Jackson 2.13.4.2 - работа с JSON
- SLF4J и Logback - логирование
### Функциональные возможности
- Воспроизведение музыки с YouTube
- Система автоматической выдачи ролей
- Конфигурация через JSON
- Управление через slash-команды и текстовые команды
- Мониторинг серверов
- Поддержка Source протокола (CS:GO, Rust, TF2 и др.)
- Автоматическое обновление информации
### Особенности реализации
- Паттерн Singleton для работы с конфигурацией
- Потокобезопасная обработка конфигурации
- Автоматическое создание и обновление конфигурации
- Форматированный вывод через Embed сообщения
## Разработка
### Структура проекта
```
src/
├── main/
│   ├── java/ru/discordj/bot/
│   │   ├── config/
│   │   ├── events/
│   │   │   └── slashcommands/
│   │   │   └── listener/
│   │   │        └── configurator/
│   │   │             └── command/
│   │   ├── lavaplayer/
│   │   ├── monitor/
│   │   │   └── parser/
│   │   │   └── sender/
│   │   │   └── tools/
│   │   │   └── query/
│   │   └── utility/
│   │        └── pojo/
│   └── resources/
│         └── META-INF/
```
### Сборка проекта
Проект использует Maven для управления зависимостями и сборки:
- Автоматическое включение зависимостей
- Копирование ресурсов
- Создание исполняемого JAR-файла
## Примечания
- Конфигурационный файл создается автоматически при первом запуске
- Поддерживается ручное редактирование config.json
- Поддерживается ручное редактирование rules.json
- Все изменения конфигурации сохраняются автоматически

