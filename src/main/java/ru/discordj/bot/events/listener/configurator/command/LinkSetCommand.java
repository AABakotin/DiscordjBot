package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.utility.pojo.Root;

/**
 * Команда для установки пригласительной ссылки.
 * Позволяет задать ссылку для приглашения на сервер.
 */
public class LinkSetCommand extends BaseCommand {
    private final IJsonHandler jsonHandler = JsonParse.getInstance();

    /**
     * Устанавливает новую пригласительную ссылку.
     *
     * @param args аргументы команды, где args[1] - новая ссылка
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 2) return;
        
        root.setInviteLink(args[1]);
        jsonHandler.write(root);
        sendEmbed(event, EmbedFactory.getInstance().createConfigEmbed().embedConfiguration());
    }
} 