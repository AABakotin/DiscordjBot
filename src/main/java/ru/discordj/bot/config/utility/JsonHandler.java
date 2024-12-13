package ru.discordj.bot.config.utility;

import ru.discordj.bot.config.utility.pojo.Root;

/**
 * Интерфейс для работы с JSON конфигурацией бота.
 * Определяет методы для чтения и записи конфигурации.
 */
public interface JsonHandler {
    /**
     * Читает конфигурацию из JSON файла.
     *
     * @return объект конфигурации
     * @throws RuntimeException если возникла ошибка при чтении
     */
    Root read();

    /**
     * Записывает конфигурацию в JSON файл.
     *
     * @param root объект конфигурации для записи
     * @throws RuntimeException если возникла ошибка при записи
     */
    void write(Root root);
}
