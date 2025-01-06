package ru.discordj.bot.events.listener.configurator;
import ru.discordj.bot.events.listener.configurator.command.BotCommand;
import ru.discordj.bot.utility.IJsonHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.events.listener.configurator.command.BotCommandFactory;

/**
 * Основной класс для обработки команд конфигурации бота.
 * Обрабатывает входящие сообщения и делегирует их соответствующим командам.
 */
@Component
public class Configurator extends ListenerAdapter {
    private final IJsonHandler jsonHandler;
    private final BotCommandFactory commandFactory;
    private static final String EMPTY = "empty";
    
    @Autowired
    public Configurator(IJsonHandler jsonHandler, BotCommandFactory commandFactory) {
        this.jsonHandler = jsonHandler;
        this.commandFactory = commandFactory;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        Root root = jsonHandler.read();
        if (!isAuthorized(event, root)) return;
        
        String[] commandArgs = event.getMessage().getContentRaw().split(" ");
        BotCommand command = BotCommand.fromString(commandArgs[0]);
        if (command != null) {
            try {
                command.execute(commandArgs, event, root, commandFactory);
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



