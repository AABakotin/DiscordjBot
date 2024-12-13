package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.config.utility.JsonHandler;
import ru.discordj.bot.config.utility.JsonParse;
import ru.discordj.bot.config.utility.pojo.Root;
import ru.discordj.bot.embed.EmbedCreation;

/**
 * Команда для установки токена бота.
 * Позволяет обновить токен авторизации Discord бота.
 */
public class TokenSetCommand extends BaseCommand {
    private final JsonHandler jsonHandler = JsonParse.getInstance();

    /**
     * Устанавливает новый токен бота.
     *
     * @param args аргументы команды: args[1] - новый токен
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 2) return;
        
        root.setToken(args[1]);
        jsonHandler.write(root);
        sendEmbed(event, EmbedCreation.get().embedConfiguration());
    }
} 