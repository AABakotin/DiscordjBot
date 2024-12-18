package ru.discordj.bot.events.listener.configurator;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.concurrent.CompletableFuture;
import net.dv8tion.jda.api.entities.Message;

/**
 * Базовый абстрактный класс для команд бота.
 * Предоставляет общую функциональность для всех команд.
 */
public abstract class BaseCommand implements BotCommandExecutor {
    /**
     * Отправляет встроенное сообщение в канал.
     *
     * @param event событие сообщения Discord
     * @param embed встроенное сообщение для отправки
     */
    protected void sendEmbed(MessageReceivedEvent event, MessageEmbed embed) {
        event.getChannel().sendMessageEmbeds(embed).queue();
    }

    /**
     * Отправляет текстовое сообщение в канал.
     *
     * @param event событие сообщения Discord
     * @param message текст сообщения
     * @return future результат отправки сообщения
     */
    protected CompletableFuture<Message> sendMessage(MessageReceivedEvent event, String message) {
        return event.getChannel().sendMessage(message).submit();
    }
} 