package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.ServerRules;
import ru.discordj.bot.utility.pojo.Roles;

import java.util.Objects;

public class AddRoleListener extends ListenerAdapter {

    private final JsonParse jsonHandler = JsonParse.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AddRoleListener.class);

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        
        Guild guild = event.getGuild();
        String emoji = event.getEmoji().getName();
        String channelByEmoji = getChannelByEmoji(guild, emoji);
        
        if (channelByEmoji == null) {
            return; // Пропускаем если канал не найден
        }
        
        GuildChannel guestGuildChannel = guild.getGuildChannelById(channelByEmoji);
        if (guestGuildChannel == null) {
            return; // Пропускаем если канал не существует
        }

        if (guestGuildChannel == event.getGuildChannel()) {
            try {
                String roleId = getRoleByEmoji(guild, emoji);
                if (roleId == null) {
                    return; // Пропускаем если роль не найдена
                }

                // Проверяем, есть ли у бота права на управление ролями
                if (!guild.getSelfMember().hasPermission(net.dv8tion.jda.api.Permission.MANAGE_ROLES)) {
                    logger.error("Guild {}: У бота отсутствуют права MANAGE_ROLES для управления ролями", 
                        guild.getName());
                    return;
                }

                guild.addRoleToMember(
                    Objects.requireNonNull(event.getMember()).getUser(),
                    Objects.requireNonNull(guild.getRoleById(roleId))
                ).queue();
                
                logger.info("Guild {}: User {} subscribed to role {}",
                    guild.getName(),
                    event.getUser().getName(), 
                    guild.getRoleById(roleId).getName());
            } catch (IllegalArgumentException e) {
                logger.error("Guild {}: onMessageReactionAdd: ID emoji or ID role not found: {}",
                    guild.getName(), e.getMessage());
            }
        }
    }


    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) return;
        
        Guild guild = event.getGuild();
        String emoji = event.getEmoji().getName();
        String channelByEmoji = getChannelByEmoji(guild, emoji);
        
        if (channelByEmoji == null) {
            return; // Пропускаем если канал не найден
        }
        
        GuildChannel guestGuildChannel = guild.getGuildChannelById(channelByEmoji);
        if (guestGuildChannel == null) {
            return; // Пропускаем если канал не существует
        }

        if (guestGuildChannel == event.getGuildChannel()) {
            try {
                String roleId = getRoleByEmoji(guild, emoji);
                if (roleId == null) {
                    return; // Пропускаем если роль не найдена
                }

                // Проверяем, есть ли у бота права на управление ролями
                if (!guild.getSelfMember().hasPermission(net.dv8tion.jda.api.Permission.MANAGE_ROLES)) {
                    logger.error("Guild {}: У бота отсутствуют права MANAGE_ROLES для управления ролями", 
                        guild.getName());
                    return;
                }

                guild.removeRoleFromMember(
                    Objects.requireNonNull(event.getMember()).getUser(),
                    Objects.requireNonNull(guild.getRoleById(roleId))
                ).queue();
                
                logger.info("Guild {}: User {} unsubscribed from role {}",
                    guild.getName(),
                    event.getUser().getName(), 
                    guild.getRoleById(roleId).getName());
            } catch (IllegalArgumentException e) {
                logger.error("Guild {}: onMessageReactionRemove: ID emoji or ID role not found: {}",
                    guild.getName(), e.getMessage());
            }
        }
    }

    /**
     * Получает ID канала, связанного с эмодзи, для конкретной гильдии
     * @param guild Гильдия
     * @param emojiId ID эмодзи
     * @return ID канала или null, если не найден
     */
    private String getChannelByEmoji(Guild guild, String emojiId) {
        ServerRules root = jsonHandler.read(guild);
        return root.getRoles().stream()
            .filter(role -> role.getEmojiId().equals(emojiId))
            .map(Roles::getChannelId)
            .findFirst()
            .orElse(null);
    }

    /**
     * Получает ID роли, связанной с эмодзи, для конкретной гильдии
     * @param guild Гильдия
     * @param emoji ID эмодзи
     * @return ID роли или null, если не найден
     */
    private String getRoleByEmoji(Guild guild, String emoji) {
        ServerRules root = jsonHandler.read(guild);
        return root.getRoles().stream()
            .filter(e -> e.getEmojiId().equals(emoji))
            .findFirst()
            .map(Roles::getRoleId)
            .orElse(null);
    }
}

