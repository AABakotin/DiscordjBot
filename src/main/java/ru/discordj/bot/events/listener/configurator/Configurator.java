package ru.discordj.bot.events.listener.configurator;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.config.utility.JsonHandler;
import ru.discordj.bot.config.utility.JsonParse;
import ru.discordj.bot.config.utility.pojo.Root;

/**
 * Основной класс для обработки команд конфигурации бота.
 * Обрабатывает входящие сообщения и делегирует их соответствующим командам.
 */
public class Configurator extends ListenerAdapter {
    private final JsonHandler jsonHandler = JsonParse.getInstance();
    private static final String EMPTY = "empty";

    /**
     * Обрабатывает входящие сообщения и выполняет соответствующие команды.
     *
     * @param event событие получения сообщения
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        Root root = jsonHandler.read();
        if (!isAuthorized(event, root)) return;
        
        String[] commandArgs = event.getMessage().getContentRaw().split(" ");
        BotCommand command = BotCommand.fromString(commandArgs[0]);
        if (command != null) {
            try {
                command.execute(commandArgs, event, root);
            } catch (Exception e) {
                event.getChannel().sendMessage("Произошла ошибка: " + e.getMessage()).queue();
            }
        }
    }

    /**
     * Проверяет права пользователя на выполнение команд.
     *
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     * @return true если пользователь имеет права, false в противном случае
     */
    private boolean isAuthorized(MessageReceivedEvent event, Root root) {
        String ownerId = root.getOwner();
        String authorId = event.getAuthor().getId();
        return ownerId.equals(EMPTY) || 
               ownerId.isEmpty() || 
               ownerId.equals(authorId);
    }
}



