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
 * Команда для удаления роли из конфигурации бота.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class RoleDeleteCommand implements BotCommandExecutor {
    private static final String SUCCESS_MESSAGE = "✅ Роль успешно удалена";
    private static final String ERROR_MESSAGE = "❌ Ошибка при удалении роли";
    private static final String INVALID_ARGS = "❌ Использование: !del_role <channelId> <roleId>";
    private static final String ROLE_NOT_FOUND = "❌ Роль не найдена";
    private static final int REQUIRED_ARGS = 3;

    private final IJsonHandler jsonHandler;
    private final EmbedFactory embedFactory;

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (!validateArgs(args, event)) {
            return;
        }

        try {
            if (deleteRole(args, root)) {
                updateConfigAndNotify(event, root);
                log.info("Role deleted by user: {} (Channel: {}, Role: {})", 
                    event.getAuthor().getName(), args[1], args[2]);
            } else {
                event.getChannel().sendMessage(ROLE_NOT_FOUND).queue();
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
     * Удаляет роль из конфигурации
     * @return true если роль была найдена и удалена
     */
    private boolean deleteRole(String[] args, Root root) {
        String channelId = args[1];
        String roleId = args[2];

        return root.getRoles().removeIf(role -> 
            role.getChannelId().equals(channelId) && 
            role.getRoleId().equals(roleId)
        );
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
        log.error("Error while deleting role", e);
        event.getChannel().sendMessage(ERROR_MESSAGE + ": " + e.getMessage()).queue();
    }
} 