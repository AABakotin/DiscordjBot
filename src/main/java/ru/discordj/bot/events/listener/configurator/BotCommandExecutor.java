package ru.discordj.bot.events.listener.configurator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.utility.pojo.Root;

/**
 * Интерфейс для исполнителей команд бота.
 * Определяет контракт для всех команд конфигуратора.
 */
public interface BotCommandExecutor {

    /**
     * Выполняет команду с заданными параметрами
     *
     * @param args массив аргументов команды:
     *             args[0] - имя команды
     *             args[1..n] - параметры команды
     * @param event событие сообщения Discord, содержащее контекст выполнения
     * @param root корневой объект конфигурации, содержащий текущие настройки
     * @throws IllegalArgumentException если аргументы команды некорректны
     * @throws IllegalStateException если команда не может быть выполнена в текущем состоянии
     * @throws SecurityException если у пользователя недостаточно прав
     */
    void execute(String[] args, MessageReceivedEvent event, Root root);

    /**
     * Проверяет, может ли команда быть выполнена с текущими правами пользователя
     *
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     * @return true если команда может быть выполнена, false в противном случае
     */
    default boolean canExecute(MessageReceivedEvent event, Root root) {
        return true;
    }

    /**
     * Возвращает описание команды и её использования
     *
     * @return строка с описанием команды
     */
    default String getDescription() {
        return "Описание команды не предоставлено";
    }

    /**
     * Возвращает синтаксис использования команды
     *
     * @return строка с синтаксисом команды
     */
    default String getUsage() {
        return "Синтаксис команды не определен";
    }
} 