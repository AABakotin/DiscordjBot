package ru.discordj.bot.events.listener.configurator;

public enum ConfiguratorError {
    ADMIN_NOT_FOUND("В базе нет ID администратора, введите !id."),
    ADMIN_EXISTS("Уже установлен ID администратора: "),
    ROLE_FORMAT("Неверная команда! Формат: !role id-канала id-роли id-эмодзи"),
    DEL_ROLE_FORMAT("Неверные данные! Должно быть число после !del_role 1 или all"),
    INVALID_INDEX("Неверное число!");

    private final String message;

    ConfiguratorError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
} 