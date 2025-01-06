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
 * Команда для установки токена Discord бота.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class TokenSetCommand implements BotCommandExecutor {
    private static final String SUCCESS_MESSAGE = "✅ Токен успешно установлен";
    private static final String ERROR_MESSAGE = "❌ Ошибка при установке токена";
    private static final String INVALID_ARGS = "❌ Использование: !token <bot_token>";
    private static final String UNAUTHORIZED = "❌ У вас нет прав для установки токена";
    private static final String EMPTY_ID = "empty";
    private static final int REQUIRED_ARGS = 2;
    private static final int MIN_TOKEN_LENGTH = 50;

    private final IJsonHandler jsonHandler;
    private final EmbedFactory embedFactory;

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (!validateArgs(args, event)) {
            return;
        }

        if (!isAuthorized(event.getAuthor().getId(), root)) {
            event.getChannel().sendMessage(UNAUTHORIZED).queue();
            return;
        }

        try {
            setToken(args[1], event, root);
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

        if (args[1].length() < MIN_TOKEN_LENGTH) {
            event.getChannel().sendMessage("❌ Некорректный формат токена").queue();
            return false;
        }

        return true;
    }

    /**
     * Проверяет права пользователя на выполнение команды
     */
    private boolean isAuthorized(String userId, Root root) {
        return root.getOwner() != null && 
               !root.getOwner().equals(EMPTY_ID) && 
               root.getOwner().equals(userId);
    }

    /**
     * Устанавливает новый токен бота
     */
    private void setToken(String token, MessageReceivedEvent event, Root root) {
        root.setToken(token);
        updateConfigAndNotify(event, root);
        log.info("Bot token updated by user: {}", event.getAuthor().getName());
        
        // Удаляем сообщение с токеном для безопасности
        event.getMessage().delete().queue();
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
        log.error("Error while setting bot token", e);
        event.getChannel().sendMessage(ERROR_MESSAGE + ": " + e.getMessage()).queue();
    }
} 