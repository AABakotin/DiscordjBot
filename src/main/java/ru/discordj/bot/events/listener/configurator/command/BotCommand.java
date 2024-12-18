package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;
import ru.discordj.bot.utility.pojo.Root;

/**
 * Enum для управления командами бота.
 * Содержит все доступные команды и их исполнителей.
 */
public enum BotCommand {
    READ_CONF("!read_conf", new ReadConfigCommand()),
    ID("!id", new SetIdCommand()),
    ID_DEL("!id_del", new DeleteIdCommand()),
    ROLE("!role", new RoleAddCommand()),
    TOKEN("!token", new TokenSetCommand()),
    LINK("!link", new LinkSetCommand()),
    DEL_ROLE("!del_role", new RoleDeleteCommand());

    private final String command;
    private final BotCommandExecutor executor;

    /**
     * Создает новую команду с заданным текстовым идентификатором и исполнителем.
     *
     * @param command текстовая команда
     * @param executor исполнитель команды
     */
    BotCommand(String command, BotCommandExecutor executor) {
        this.command = command;
        this.executor = executor;
    }

    /**
     * Выполняет команду с заданными параметрами.
     *
     * @param args аргументы команды
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        executor.execute(args, event, root);
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
