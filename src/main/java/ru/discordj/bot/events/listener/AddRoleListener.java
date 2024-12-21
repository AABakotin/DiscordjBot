package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.Roles;

import java.util.Objects;


public class AddRoleListener extends ListenerAdapter {

    private final IJsonHandler jsonHandler = JsonParse.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AddRoleListener.class);


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        String emoji = event.getEmoji().getName();
        String channelByEmoji = getChannelByEmoji(emoji);
        
        if (channelByEmoji == null) {
            return; // Пропускаем если канал не найден
        }
        
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(channelByEmoji);
        if (guestGuildChannel == null) {
            return; // Пропускаем если канал не существует
        }

        if (guestGuildChannel == event.getGuildChannel()) {
            try {
                String roleId = getRoleByEmoji(emoji);
                if (roleId == null) {
                    return; // Пропускаем если роль не найдена
                }

                event.getGuild()
                    .addRoleToMember(
                        Objects.requireNonNull(event.getMember()).getUser(),
                        Objects.requireNonNull(event.getGuild().getRoleById(roleId))
                    )
                    .queue();
                logger.info("User {} subscribe {}", event.getUser().getName(), 
                    event.getUser().getJDA().getRoleById(roleId));
            } catch (IllegalArgumentException e) {
                logger.error("onMessageReactionAdd: ID emoji or ID role in property.env is NULL");
            }
        }
    }


    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        String emoji = event.getEmoji().getName();
        String channelByEmoji = getChannelByEmoji(emoji);
        
        if (channelByEmoji == null) {
            return; // Пропускаем если канал не найден
        }
        
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(channelByEmoji);
        if (guestGuildChannel == null) {
            return; // Пропускаем если канал не существует
        }

        if (guestGuildChannel == event.getGuildChannel()) {
            try {
                String roleId = getRoleByEmoji(emoji);
                if (roleId == null) {
                    return; // Пропускаем если роль не найдена
                }

                event.getGuild()
                    .removeRoleFromMember(
                        Objects.requireNonNull(event.getMember()).getUser(),
                        Objects.requireNonNull(event.getGuild().getRoleById(roleId))
                    )
                    .queue();
                logger.info("User {} unsubscribe {}", event.getUser().getName(), 
                    event.getUser().getJDA().getRoleById(roleId));
            } catch (IllegalArgumentException e) {
                logger.error("onMessageReactionRemove: ID emoji or ID role in property.env is NULL");
            }
        }
    }

    private String getChannelByEmoji(String emojiId) {
        Root root = jsonHandler.read();
        return root.getRoles().stream()
            .filter(role -> role.getEmojiId().equals(emojiId))
            .map(Roles::getChannelId)
            .findFirst()
            .orElse(null);
    }

    private  String getRoleByEmoji(String emoji) {
        return jsonHandler.read().getRoles()
                .stream()
                .filter(e -> e.getEmojiId().equals(emoji)).findFirst().get().getRoleId();
    }
}

