package ru.discordj.bot.events.listener.configurator;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.RestAction;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.CompletableFuture;

/**
 * Базовый класс для всех команд конфигуратора.
 * Предоставляет общие методы для работы с сообщениями Discord.
 */
@Slf4j
public abstract class BaseCommand implements BotCommandExecutor {

    private static final String ERROR_MESSAGE = "❌ Произошла ошибка при отправке сообщения";

    /**
     * Отправляет текстовое сообщение в канал
     * @param event событие сообщения
     * @param message текст сообщения
     * @return CompletableFuture для цепочки действий
     */
    protected CompletableFuture<Void> sendMessage(MessageReceivedEvent event, String message) {
        return event.getChannel()
            .sendMessage(message)
            .submit()
            .whenComplete((msg, error) -> {
                if (error != null) {
                    log.error("Failed to send message: {}", error.getMessage());
                    handleMessageError(event);
                }
            })
            .thenApply(msg -> null);
    }

    /**
     * Отправляет embed сообщение в канал
     * @param event событие сообщения
     * @param embed embed для отправки
     * @return CompletableFuture для цепочки действий
     */
    protected CompletableFuture<Void> sendEmbed(MessageReceivedEvent event, MessageEmbed embed) {
        return event.getChannel()
            .sendMessageEmbeds(embed)
            .submit()
            .whenComplete((msg, error) -> {
                if (error != null) {
                    log.error("Failed to send embed: {}", error.getMessage());
                    handleMessageError(event);
                }
            })
            .thenApply(msg -> null);
    }

    /**
     * Отправляет сообщение об ошибке в канал
     * @param event событие сообщения
     */
    protected void handleMessageError(MessageReceivedEvent event) {
        event.getChannel()
            .sendMessage(ERROR_MESSAGE)
            .queue(null, error -> 
                log.error("Failed to send error message: {}", error.getMessage())
            );
    }

    /**
     * Создает RestAction для отправки сообщения
     * @param event событие сообщения
     * @param message текст сообщения
     * @return RestAction для отправки сообщения
     */
    protected RestAction<Message> createMessageAction(MessageReceivedEvent event, String message) {
        return event.getChannel().sendMessage(message);
    }

    /**
     * Создает RestAction для отправки embed сообщения
     * @param event событие сообщения
     * @param embed embed для отправки
     * @return RestAction для отправки embed сообщения
     */
    protected RestAction<Message> createEmbedAction(MessageReceivedEvent event, MessageEmbed embed) {
        return event.getChannel().sendMessageEmbeds(embed);
    }
} 