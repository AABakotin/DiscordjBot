package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.utility.pojo.Root;
import lombok.Getter;

/**
 * Enum для управления командами бота.
 * Содержит все доступные команды и их описания.
 */
@Getter
public enum BotCommand {
    READ_CONF("!read_conf", "Прочитать текущую конфигурацию"),
    ID("!id", "Установить ID владельца"),
    ID_DEL("!id_del", "Удалить ID владельца"),
    ROLE("!role", "Добавить роль"),
    TOKEN("!token", "Установить токен бота"),
    LINK("!link", "Установить пригласительную ссылку"),
    DEL_ROLE("!del_role", "Удалить роль"),
    MONITOR("!monitor", "Управление мониторингом серверов");

    private final String command;
    private final String description;

    BotCommand(String command, String description) {
        this.command = command;
        this.description = description;
    }

    /**
     * Выполняет команду с заданными параметрами
     */
    public void execute(String[] args, MessageReceivedEvent event, Root root, BotCommandFactory factory) {
        try {
            factory.getCommand(this).execute(args, event, root);
        } catch (Exception e) {
            event.getChannel().sendMessage("❌ Ошибка при выполнении команды: " + e.getMessage()).queue();
        }
    }

    /**
     * Находит команду по её текстовому представлению
     */
    public static BotCommand fromString(String text) {
        for (BotCommand cmd : BotCommand.values()) {
            if (cmd.getCommand().equalsIgnoreCase(text)) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * Возвращает список всех доступных команд с описаниями
     */
    public static String getHelpMessage() {
        StringBuilder help = new StringBuilder("**Доступные команды:**\n");
        for (BotCommand cmd : values()) {
            help.append(String.format("`%s` - %s\n", cmd.getCommand(), cmd.getDescription()));
        }
        return help.toString();
    }
}
