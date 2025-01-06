package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Команда для установки ID владельца бота.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class SetIdCommand implements BotCommandExecutor {
    private static final String SUCCESS_MESSAGE = "✅ ID владельца успешно установлен";
    private static final String ERROR_MESSAGE = "❌ Ошибка при установке ID владельца";
    private static final String INVALID_ARGS = "❌ Использование: !id <userId>";
    private static final String ALREADY_SET = "❌ ID владельца уже установлен. Сначала удалите текущий ID";
    private static final int REQUIRED_ARGS = 2;
    private static final String EMPTY_ID = "empty";

    private final IJsonHandler jsonHandler;
    private final EmbedFactory embedFactory;

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (!validateArgs(args, event)) {
            return;
        }

        try {
            if (canSetOwnerId(root)) {
                setOwnerId(args[1], event, root);
            } else {
                event.getChannel().sendMessage(ALREADY_SET).queue();
            }
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    /**
     * Проверяет корректность аргументов команды
     */
    private boolean validateArgs(String[] args, MessageReceivedEvent event) {
        if (args.length < REQUIRED_ARGS) {
            event.getChannel().sendMessage(INVALID_ARGS).queue();
            return false;
        }
        return true;
    }

    /**
     * Проверяет, можно ли установить новый ID владельца
     */
    private boolean canSetOwnerId(Root root) {
        return root.getOwner() == null || 
               root.getOwner().equals(EMPTY_ID);
    }

    /**
     * Устанавливает новый ID владельца
     */
    private void setOwnerId(String userId, MessageReceivedEvent event, Root root) {
        root.setOwner(userId);
        updateConfigAndNotify(event, root);
        log.info("Owner ID set to {} by user: {}", userId, event.getAuthor().getName());
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
        log.error("Error while setting owner ID", e);
        event.getChannel().sendMessage(ERROR_MESSAGE + ": " + e.getMessage()).queue();
    }
} 