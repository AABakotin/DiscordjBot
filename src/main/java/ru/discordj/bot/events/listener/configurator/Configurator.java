package ru.discordj.bot.events.listener.configurator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.events.listener.configurator.command.BotCommand;
import ru.discordj.bot.events.listener.configurator.command.BotCommandFactory;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Обработчик команд конфигурации бота.
 * Управляет выполнением команд конфигурации и проверкой прав доступа.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Configurator {
    private static final String COMMAND_PREFIX = "!";
    private static final String EMPTY_ID = "empty";
    private static final String UNAUTHORIZED_MESSAGE = "❌ У вас нет прав для выполнения этой команды";
    private static final String COMMAND_ERROR = "❌ Ошибка при выполнении команды: {}";

    private final IJsonHandler jsonHandler;
    private final BotCommandFactory commandFactory;

    /**
     * Обрабатывает входящее сообщение и выполняет соответствующую команду
     */
    public void handleMessage(MessageReceivedEvent event) {
        String content = event.getMessage().getContentRaw();
        
        if (!isCommand(content)) {
            return;
        }

        try {
            processCommand(event, content);
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    /**
     * Проверяет, является ли сообщение командой
     */
    private boolean isCommand(String content) {
        return content.startsWith(COMMAND_PREFIX);
    }

    /**
     * Обрабатывает команду
     */
    private void processCommand(MessageReceivedEvent event, String content) {
        String[] args = content.split("\\s+");
        Root config = jsonHandler.read();

        if (!isAuthorized(event, config)) {
            event.getChannel().sendMessage(UNAUTHORIZED_MESSAGE).queue();
            return;
        }

        BotCommand command = BotCommand.fromString(args[0]);
        if (command != null && commandFactory.hasCommand(command)) {
            executeCommand(command, args, event, config);
        }
    }

    /**
     * Проверяет права пользователя на выполнение команд
     */
    private boolean isAuthorized(MessageReceivedEvent event, Root config) {
        String userId = event.getAuthor().getId();
        return config.getOwner() == null || 
               config.getOwner().equals(EMPTY_ID) || 
               config.getOwner().equals(userId);
    }

    /**
     * Выполняет команду с заданными параметрами
     */
    private void executeCommand(BotCommand command, String[] args, MessageReceivedEvent event, Root config) {
        try {
            BotCommandExecutor executor = commandFactory.getCommand(command);
            if (executor.canExecute(event, config)) {
                executor.execute(args, event, config);
                log.info("Command {} executed by user: {}", command, event.getAuthor().getName());
            }
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    /**
     * Обрабатывает ошибки при выполнении команд
     */
    private void handleError(MessageReceivedEvent event, Exception e) {
        log.error(COMMAND_ERROR, e.getMessage());
        event.getChannel().sendMessage(COMMAND_ERROR.replace("{}", e.getMessage())).queue();
    }
}



