package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;
import ru.discordj.bot.utility.pojo.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Команда для чтения и отображения текущей конфигурации бота.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ReadConfigCommand implements BotCommandExecutor {
    private static final String SUCCESS_MESSAGE = "✅ Конфигурация успешно загружена";
    private static final String ERROR_MESSAGE = "❌ Ошибка при чтении конфигурации";

    private final EmbedFactory embedFactory;

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        try {
            displayConfiguration(event, root);
            log.info("Configuration read by user: {}", event.getAuthor().getName());
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    /**
     * Отображает текущую конфигурацию в канале
     */
    private void displayConfiguration(MessageReceivedEvent event, Root root) {
        event.getChannel().sendMessageEmbeds(
            embedFactory.createConfigEmbed()
                .embedConfiguration()
        ).queue(success -> 
            event.getChannel().sendMessage(SUCCESS_MESSAGE).queue()
        );
    }

    /**
     * Обрабатывает ошибки при выполнении команды
     */
    private void handleError(MessageReceivedEvent event, Exception e) {
        log.error("Error while reading configuration", e);
        event.getChannel().sendMessage(ERROR_MESSAGE + ": " + e.getMessage()).queue();
    }
} 