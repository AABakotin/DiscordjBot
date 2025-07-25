# Discord Bot
Многофункциональный Discord бот с возможностями музыкального плеера, системой автоматической выдачи ролей и потоковым воспроизведением радиостанций.

---

## Быстрый старт через DockerHub

Готовый образ доступен на DockerHub: [megabart/discordjbot](https://hub.docker.com/r/megabart/discordjbot)

### Пример запуска:
```bash
docker run -d \
  --name discordjbot \
  -e DISCORD_TOKEN=your-discord-bot-token \
  -v $(pwd)/json:/app/json \
  megabart/discordjbot:latest
```

Или через docker-compose:
```yaml
version: '3.8'
services:
  discordjbot:
    image: megabart/discordjbot:latest
    container_name: discordjbot
    environment:
      - DISCORD_TOKEN=your-discord-bot-token
    volumes:
      - ./json:/app/json
    restart: unless-stopped
```

---

## Основные возможности
### Музыкальный плеер
- `/play [URL/название]` — воспроизведение музыки с YouTube, Twitch, Bandcamp
- Автоматическое подключение к вашему голосовому каналу
- Управление воспроизведением через кнопки
- Отображение прогресса воспроизведения

### Радио
- `/radio name:[название]` — воспроизведение радиостанции
- `/radio_list` — просмотр всех доступных радиостанций
- `/radio_add name:[название] url:[URL]` — добавление новой радиостанции (только для администраторов)
- `/radio_remove name:[название]` — удаление радиостанции из списка (только для администраторов)
- `/radio_reload` — обновление списка радиостанций до стандартного набора (только для администраторов)
- Автоматическое подключение к голосовому каналу
- Визуальное отображение списка радиостанций через эмбеды

### Система ролей
- `/guildconfig role-add` — добавление правила автовыдачи роли
- `/guildconfig role-remove` — удаление правила
- Автоматическая выдача ролей при реакции на сообщение

### Общие команды
- `/ping` — проверка работоспособности бота
- `/rules` — отображение правил сервера
- `/info` — информация о сервере
- `/hello` — приветственное сообщение
- `/invite` — получение ссылки-приглашения

### Команды конфигурации
- `/guildconfig read` — отображение текущей конфигурации
- `/guildconfig link` — установка ссылки-приглашения на сервер
- `/guildconfig role-add` — добавление правила автоматической выдачи роли
- `/guildconfig role-remove` — удаление правила автоматической выдачи роли

### Команды мониторинга
- `/guildconfig monitor-channel` — установка канала для мониторинга
- `/guildconfig monitor-add` — добавление сервера
- `/guildconfig monitor-remove` — удаление сервера
- `/guildconfig monitor-list` — список серверов
- `/guildconfig monitor-start` — запуск мониторинга
- `/guildconfig monitor-stop` — остановка мониторинга

### Мониторинг серверов
- Поддержка Source протокола (CS:GO, Rust, TF2 и др.)
- Отображение статуса серверов
- Количество игроков
- Текущая карта
- Автоматическое обновление информации

---

## Важно
**Все команды теперь доступны только как slash-команды (начинаются с /) через интерфейс Discord.**

---

## Список доступных slash-команд

### Основные
- `/ping` — проверка работоспособности бота
- `/rules` — отображение правил сервера
- `/info information:<пользователь>` — информация о пользователе
- `/hello` — приветственное сообщение
- `/invite` — получение ссылки-приглашения

### Музыка и радио
- `/play query:<URL/название>` — воспроизведение музыки или радиостанции
- `/radio play name:<название>` — воспроизведение радиостанции
- `/radio stop` — остановить воспроизведение
- `/radio list` — показать список радиостанций
- `/radio add name:<название> url:<URL>` — добавить радиостанцию (админ)
- `/radio remove name:<название>` — удалить радиостанцию (админ)
- `/radio_reload` — обновить список радиостанций (админ)
- `/radio_list` — показать список радиостанций (альтернативная команда)

### Конфигурация сервера
- `/guild-config view` — просмотр текущей конфигурации
- `/guild-config set-invite link:<ссылка>` — установка ссылки-приглашения
- `/guild-config add-role role:<роль> channel:<канал> emoji:<эмодзи>` — добавить правило автовыдачи роли
- `/guild-config remove-role role:<роль>` — удалить правило автовыдачи роли
- `/guild-config monitoring action:<enable/disable> channel:<канал>` — включить/выключить мониторинг
- `/guild-config add-server name:<имя> host:<ip> port:<порт>` — добавить сервер для мониторинга
- `/guild-config remove-server name:<имя>` — удалить сервер из мониторинга
- `/guild-config edit-rules ...` — редактировать правила сервера

### Служебные
- `/update_commands` — обновить slash-команды (только для администратора)

---

## Пример настройки автовыдачи ролей
1. Создайте роль на сервере Discord
2. Получите необходимые ID:
   - ID канала (ПКМ по каналу → Копировать ID)
   - ID роли (Настройки сервера → Роли → Копировать ID)
   - Эмодзи (можно вставить как Unicode или custom emoji)
3. Добавьте правило через slash-команду:
   ```
   /guild-config add-role role:<роль> channel:<канал> emoji:<эмодзи>
   ```

---

## Версия программы
**Текущая версия: 1.2.3**

## Установка и настройка
### Системные требования
- Java 17 или выше
- Maven для сборки проекта
- Discord Bot Token
### Сборка проекта
```bash
mvn clean package
```
Собранный JAR-файл будет находиться в директории `target/DiscordjBot-jar-with-dependencies.jar`
### Конфигурация

Для работы бота необходимы следующие настройки:

- Discord Bot Token (передается как аргумент командной строки или через переменную окружения)

Все настройки сервера (роли, правила, радиостанции и другие параметры) хранятся в отдельных файлах для каждой гильдии в директории `config/`. Имя файла формируется на основе имени сервера.

Пример структуры конфигурации для сервера:
```json
{
  "inviteLink": "your-discord-invite-link",
  "roles": [],
  "radioStations": [],
  "rules": {
    "title": "Правила сервера",
    "welcomeField": "Добро пожаловать!",
    "rulesField": "1. Правило 1\n2. Правило 2",
    "footer": "Обновлено {date} пользователем {author}"
  }
}
```
### Запуск

Существует два способа указания токена:

1. Аргумент командной строки
2. Переменная окружения `DISCORD_TOKEN`

### Через JAR-файл

```bash
java -jar DiscordjBot-jar-with-dependencies.jar [token]
```

### Через скрипты запуска

#### Windows

```bash
run.bat [token]
```

#### Linux/Mac

```bash
chmod +x run.sh
./run.sh [token]
```

### Через Docker (локально)

```bash
docker-compose up -d
```

При использовании Docker необходимо создать файл `.env` с переменной:
```
DISCORD_TOKEN=your-discord-bot-token-here
```

## Настройка радиостанций
Бот поддерживает динамическую конфигурацию радиостанций без необходимости перезапуска:

1. **Просмотр доступных радиостанций**:
   - Используйте команду `/radio_list` для просмотра всех доступных радиостанций

2. **Добавление новой радиостанции**:
   - Используйте команду `/radio_add` с параметрами:
     - `name` - название радиостанции
     - `url` - URL потока (начинается с http:// или https://)
   - Требуются права администратора сервера

3. **Удаление радиостанции**:
   - Используйте команду `/radio_remove` с параметром `name` - название радиостанции для удаления
   - Требуются права администратора сервера

4. **Воспроизведение радиостанции**:
   - Используйте команду `/radio name:название` - бот автоматически подключится к голосовому каналу

5. **Обновление списка радиостанций**:
   - Используйте команду `/radio_reload` для обновления списка до стандартного набора станций
   - Требуются права администратора сервера

6. **Особенности**:
   - Каждый сервер имеет свой собственный список радиостанций
   - При создании конфигурации добавляются стандартные радиостанции
   - Все изменения сохраняются в конфигурации сервера
   - В стандартный набор входят популярные станции Radio Record, Зайцев.FM и другие

## Используемые технологии
### Основные библиотеки
- JDA (Java Discord API) 5.2.1 - основной фреймворк для работы с Discord
- LavaPlayer 2.2.2 - библиотека для воспроизведения аудио
- Jackson 2.13.4.2 - работа с JSON
- SLF4J и Logback - логирование
### Функциональные возможности
- Воспроизведение музыки с YouTube
- Потоковое воспроизведение радиостанций
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
## Архитектура проекта
```
src/
├── main/
│   ├── java/ru/discordj/bot/
│   │   ├── config/
│   │   ├── embed/
│   │   ├── events/
│   │   │   ├── slashcommands/
│   │   │   └── listener/
│   │   ├── lavaplayer/
│   │   ├── monitor/
│   │   │   ├── parser/
│   │   │   ├── query/
│   │   │   ├── sender/
│   │   │   └── tools/
│   │   ├── service/
│   │   └── utility/
│   │        └── pojo/
│   └── resources/
│         └── META-INF/
```

---
### Сборка проекта
Проект использует Maven для управления зависимостями и сборки:
- Автоматическое включение зависимостей
- Копирование ресурсов
- Создание исполняемого JAR-файла
## Примечания
- Конфигурационные файлы для каждого сервера создаются автоматически при первом использовании бота на сервере
- Все настройки сервера (правила, роли, радиостанции) хранятся в одном файле конфигурации
- Для обновления списка радиостанций используйте команду `/radio_reload` (требуются права администратора)
- Все сообщения об успешных действиях и ошибках являются эфемерными (видны только вызвавшему команду пользователю) и автоматически удаляются через 30 секунд
- Список радиостанций включает популярные станции Radio Record, Зайцев.FM, Европа Плюс и другие
- Все изменения конфигурации сохраняются автоматически
- Для запуска бота на Windows рекомендуется использовать скрипт `run.bat`, который решает проблемы с кодировкой и запускает бота в свернутом окне
- Для запуска бота на Linux/Mac используйте скрипт `run.sh` для корректного отображения русских символов в логах

