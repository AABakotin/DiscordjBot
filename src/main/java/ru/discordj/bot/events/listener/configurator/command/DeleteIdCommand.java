package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.stereotype.Component;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Команда для удаления ID владельца из конфигурации.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DeleteIdCommand implements BotCommandExecutor {
    private static final String SUCCESS_MESSAGE = "✅ ID владельца успешно удален";
    private static final String ERROR_MESSAGE = "❌ Ошибка при удалении ID владельца";
    private static final String EMPTY_ID = "empty";
    
    private final IJsonHandler jsonHandler;
    private final EmbedFactory embedFactory;

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        try {
            deleteOwnerId(root);
            updateConfigAndNotify(event, root);
            log.info("Owner ID deleted by user: {}", event.getAuthor().getName());
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    /**
     * Удаляет ID владельца из конфигурации
     */
    private void deleteOwnerId(Root root) {
        root.setOwner(EMPTY_ID);
    }

    /**
     * Обновляет конфигурацию и отправляет уведомление
     */
    private void updateConfigAndNotify(MessageReceivedEvent event, Root root) {
        jsonHandler.write(root);
        
        event.getChannel().sendMessageEmbeds(
            embedFactory.createConfigEmbed()
                .embedConfiguration()
        ).queue();
        
        event.getChannel().sendMessage(SUCCESS_MESSAGE).queue();
    }

    /**
     * Обрабатывает ошибки при выполнении команды
     */
    private void handleError(MessageReceivedEvent event, Exception e) {
        log.error("Error while deleting owner ID", e);
        event.getChannel().sendMessage(ERROR_MESSAGE + ": " + e.getMessage()).queue();
    }
}