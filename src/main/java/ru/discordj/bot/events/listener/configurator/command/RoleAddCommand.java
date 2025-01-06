package ru.discordj.bot.events.listener.configurator.command;

import org.springframework.beans.factory.annotation.Autowired;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.events.listener.configurator.ConfiguratorError;
import ru.discordj.bot.utility.pojo.Roles;
import ru.discordj.bot.utility.pojo.Root;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Команда для добавления новой роли.
 * Позволяет создать новое правило автоматической выдачи роли.
 */
@Component
public class RoleAddCommand extends BaseCommand {
    @Autowired
    private IJsonHandler jsonHandler;
    
    @Autowired
    private EmbedFactory embedFactory;

    /**
     * Добавляет новое правило выдачи роли.
     *
     * @param args аргументы команды: args[1] - ID канала, args[2] - ID роли, args[3] - ID эмодзи
     * @param event событие сообщения Discord
     * @param root корневой объект конфигурации
     */
    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 4) {
            sendMessage(event, ConfiguratorError.ROLE_FORMAT.getMessage());
            return;
        }

        List<Roles> rolesList = root.getRoles();
        
        // Проверяем наличие empty значений и перезаписываем их
        for (Roles role : rolesList) {
            if ("empty".equals(role.getChannelId()) && 
                "empty".equals(role.getRoleId()) && 
                "empty".equals(role.getEmojiId())) {
                role.setChannelId(args[1]);
                role.setRoleId(args[2]);
                role.setEmojiId(args[3]);
                jsonHandler.write(root);
                sendEmbed(event, embedFactory.createConfigEmbed().embedConfiguration());
                return;
            }
        }

        // Если нет empty значений, добавляем новое правило
        rolesList.add(new Roles(args[1], args[2], args[3]));
        root.setRoles(rolesList);
        jsonHandler.write(root);
        sendEmbed(event, embedFactory.createConfigEmbed().embedConfiguration());
    }
} 