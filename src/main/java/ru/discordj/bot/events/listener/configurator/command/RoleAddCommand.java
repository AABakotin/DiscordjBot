package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Roles;
import ru.discordj.bot.utility.pojo.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Команда для добавления роли в конфигурацию бота.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RoleAddCommand implements BotCommandExecutor {
    private static final String SUCCESS_MESSAGE = "✅ Роль успешно добавлена";
    private static final String ERROR_MESSAGE = "❌ Ошибка при добавлении роли";
    private static final String INVALID_ARGS = "❌ Использование: !role <channelId> <roleId> <emojiId>";
    private static final int REQUIRED_ARGS = 4;

    private final IJsonHandler jsonHandler;
    private final EmbedFactory embedFactory;

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (!validateArgs(args, event)) {
            return;
        }

        try {
            addRole(args, root);
            updateConfigAndNotify(event, root);
            log.info("Role added by user: {} (Channel: {}, Role: {}, Emoji: {})", 
                event.getAuthor().getName(), args[1], args[2], args[3]);
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
     * Добавляет новую роль в конфигурацию
     */
    private void addRole(String[] args, Root root) {
        Roles role = new Roles();
        role.setChannelId(args[1]);
        role.setRoleId(args[2]);
        role.setEmojiId(args[3]);
        
        root.getRoles().add(role);
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
        log.error("Error while adding role", e);
        event.getChannel().sendMessage(ERROR_MESSAGE + ": " + e.getMessage()).queue();
    }
} 