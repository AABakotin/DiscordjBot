package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.events.listener.configurator.ConfiguratorError;
import ru.discordj.bot.utility.pojo.Root;

/**
 * Команда для установки ID администратора.
 * Позволяет назначить пользователя администратором бота.
 */
public class SetIdCommand extends BaseCommand {
    private static final String EMPTY = "empty";
    private final IJsonHandler jsonHandler = JsonParse.getInstance();

    /**
     * Устанавливает ID администратора бота.
     * Если администратор уже назначен, выводит сообщение об ошибке.
     *
     * @param args аргументы команды
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        String authorId = event.getAuthor().getId();
        if (root.getOwner() == null || root.getOwner().equals(EMPTY)) {
            root.setOwner(authorId);
            jsonHandler.write(root);
            sendEmbed(event, EmbedFactory.getInstance().createConfigEmbed().embedConfiguration());
            return;
        }
        
        sendMessage(event, ConfiguratorError.ADMIN_EXISTS.getMessage() + root.getOwner())
            .thenRun(() -> sendEmbed(event, EmbedFactory.getInstance().createConfigEmbed().embedConfiguration()));
    }
} 