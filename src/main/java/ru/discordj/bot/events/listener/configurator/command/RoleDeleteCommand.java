package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.config.utility.JsonHandler;
import ru.discordj.bot.config.utility.JsonParse;
import ru.discordj.bot.config.utility.pojo.Root;
import ru.discordj.bot.config.utility.pojo.Roles;
import ru.discordj.bot.embed.EmbedCreation;
import ru.discordj.bot.events.listener.configurator.ConfiguratorError;

import java.util.List;

/**
 * Команда для удаления правила выдачи роли.
 * Позволяет удалить существующее правило или все правила сразу.
 */
public class RoleDeleteCommand extends BaseCommand {
    private final JsonHandler jsonHandler = JsonParse.getInstance();

    /**
     * Удаляет правило выдачи роли по индексу или все правила.
     *
     * @param args аргументы команды: args[1] - индекс правила или "all" для удаления всех
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 2) return;

        List<Roles> rolesList = root.getRoles();
        
        if (args[1].equalsIgnoreCase("all")) {
            // Очищаем список, оставляя один элемент с empty значениями
            rolesList.clear();
            rolesList.add(new Roles("empty", "empty", "empty"));
            root.setRoles(rolesList);
            jsonHandler.write(root);
            sendEmbed(event, EmbedCreation.get().embedConfiguration());
            return;
        }

        try {
            int index = Integer.parseInt(args[1]) - 1;
            if (index < 0 || index >= rolesList.size()) {
                sendMessage(event, ConfiguratorError.INVALID_INDEX.getMessage() + rolesList.size());
                return;
            }

            // Если это последний элемент, заменяем его на empty значения
            if (rolesList.size() == 1) {
                rolesList.set(0, new Roles("empty", "empty", "empty"));
            } else {
                rolesList.remove(index);
            }
            
            root.setRoles(rolesList);
            jsonHandler.write(root);
            sendEmbed(event, EmbedCreation.get().embedConfiguration());
        } catch (NumberFormatException e) {
            sendMessage(event, ConfiguratorError.DEL_ROLE_FORMAT.getMessage());
        }
    }
} 