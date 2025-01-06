package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.utility.pojo.Root;

/**
 * Enum для управления командами бота.
 * Содержит все доступные команды и их исполнителей.
 */
public enum BotCommand {
    READ_CONF("!read_conf"),
    ID("!id"),
    ID_DEL("!id_del"),
    ROLE("!role"),
    TOKEN("!token"),
    LINK("!link"),
    DEL_ROLE("!del_role"),
    MONITOR("!monitor");

    private final String command;

    BotCommand(String command) {
        this.command = command;
    }

    public void execute(String[] args, MessageReceivedEvent event, Root root, BotCommandFactory factory) {
        factory.getCommand(this).execute(args, event, root);
    }

    public static BotCommand fromString(String text) {
        for (BotCommand cmd : BotCommand.values()) {
            if (cmd.command.equals(text)) {
                return cmd;
            }
        }
        return null;
    }
}
