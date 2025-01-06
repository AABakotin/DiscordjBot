package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.IJsonHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Команда для удаления ID администратора.
 * Позволяет сбросить права администратора бота.
 */
@Component
public class DeleteIdCommand extends BaseCommand {
    private static final String EMPTY = "empty";
    
    private final IJsonHandler jsonHandler;
    private final EmbedFactory embedFactory;

    @Autowired
    public DeleteIdCommand(IJsonHandler jsonHandler, EmbedFactory embedFactory) {
        this.jsonHandler = jsonHandler;
        this.embedFactory = embedFactory;
    }

    /**
     * Выполняет удаление ID администратора.
     * Проверяет права текущего пользователя перед выполнением.
     *
     * @param args аргументы команды
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (event.getAuthor().getId().equals(root.getOwner())) {
            root.setOwner(EMPTY);
            jsonHandler.write(root);
        }
        sendEmbed(event, embedFactory.createConfigEmbed().embedConfiguration());
    }
}