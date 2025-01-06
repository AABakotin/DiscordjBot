package ru.discordj.bot.events.listener.configurator;

import lombok.Getter;

/**
 * Перечисление ошибок конфигуратора с соответствующими сообщениями.
 * Предоставляет стандартизированные сообщения об ошибках для различных ситуаций.
 */
@Getter
public enum ConfiguratorError {
    // Ошибки авторизации
    ADMIN_NOT_FOUND("❌ Администратор не назначен. Используйте команду !id для назначения."),
    ADMIN_EXISTS("❌ Администратор уже назначен. Текущий ID: "),
    UNAUTHORIZED("❌ У вас нет прав для выполнения этой команды."),
    
    // Ошибки формата команд
    ROLE_FORMAT("❌ Неверный формат команды. Используйте: !role <channelId> <roleId> <emojiId>"),
    MONITOR_FORMAT("❌ Неверный формат команды. Используйте: !monitor <action> [параметры]"),
    TOKEN_FORMAT("❌ Неверный формат команды. Используйте: !token <bot_token>"),
    
    // Ошибки валидации
    INVALID_CHANNEL("❌ Указан неверный ID канала."),
    INVALID_ROLE("❌ Указана неверная роль."),
    INVALID_EMOJI("❌ Указан неверный эмодзи."),
    INVALID_TOKEN("❌ Указан неверный токен бота."),
    
    // Ошибки конфигурации
    CONFIG_NOT_FOUND("❌ Файл конфигурации не найден."),
    CONFIG_WRITE_ERROR("❌ Ошибка при сохранении конфигурации."),
    CONFIG_READ_ERROR("❌ Ошибка при чтении конфигурации."),
    
    // Общие ошибки
    UNKNOWN_ERROR("❌ Произошла неизвестная ошибка."),
    COMMAND_NOT_FOUND("❌ Команда не найдена.");

    private final String message;

    ConfiguratorError(String message) {
        this.message = message;
    }

    /**
     * Форматирует сообщение об ошибке с дополнительными параметрами
     * @param args параметры для форматирования сообщения
     * @return отформатированное сообщение об ошибке
     */
    public String format(Object... args) {
        return String.format(message, args);
    }

    /**
     * Создает исключение с сообщением об ошибке
     * @return новое исключение IllegalStateException
     */
    public IllegalStateException asException() {
        return new IllegalStateException(message);
    }

    /**
     * Создает исключение с дополнительным описанием
     * @param details дополнительное описание ошибки
     * @return новое исключение IllegalStateException
     */
    public IllegalStateException asException(String details) {
        return new IllegalStateException(message + " " + details);
    }
} 