package ru.discordj.bot.events.listener.configurator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.utility.pojo.Root;

/**
 * Интерфейс для выполнения команд бота.
 * Определяет контракт для всех команд конфигуратора.
 */
public interface BotCommandExecutor {
    /**
     * Выполняет команду с заданными параметрами.
     *
     * @param args аргументы команды
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    void execute(String[] args, MessageReceivedEvent event, Root root);
} 