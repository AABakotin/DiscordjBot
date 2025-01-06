package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.util.List;
import java.util.Optional;

/**
 * Слушатель для автоматического управления ролями на основе реакций.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AddRoleListener extends ListenerAdapter {
    private static final String ERROR_ROLE_NOT_FOUND = "❌ Роль не найдена: {}";
    private static final String LOG_ROLE_ADDED = "Роль {} добавлена пользователю {} в канале {}";
    private static final String LOG_ROLE_REMOVED = "Роль {} удалена у пользователя {} в канале {}";

    private final IJsonHandler jsonHandler;

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;
        
        handleReactionChange(event.getChannel().asTextChannel(), 
                           event.getMessageId(), 
                           event.getEmoji().asUnicode().getAsCodepoints(),
                           true,
                           event);
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        if (event.getUser().isBot()) return;
        
        handleReactionChange(event.getChannel().asTextChannel(), 
                           event.getMessageId(), 
                           event.getEmoji().asUnicode().getAsCodepoints(),
                           false,
                           event);
    }

    /**
     * Обрабатывает изменение реакции (добавление/удаление)
     */
    private void handleReactionChange(TextChannel channel, 
                                    String messageId, 
                                    String emojiId, 
                                    boolean isAdd,
                                    Object event) {
        List<Roles> roles = jsonHandler.read().getRoles();
        
        findMatchingRole(roles, channel.getId(), messageId, emojiId)
            .ifPresent(role -> {
                Role guildRole = findGuildRole(channel, role.getRoleId());
                if (guildRole != null) {
                    updateMemberRole(event, guildRole, isAdd, channel);
                } else {
                    log.error(ERROR_ROLE_NOT_FOUND, role.getRoleId());
                }
            });
    }

    /**
     * Находит соответствующую роль по параметрам
     */
    private Optional<Roles> findMatchingRole(List<Roles> roles, 
                                           String channelId, 
                                           String messageId, 
                                           String emojiId) {
        return roles.stream()
            .filter(role -> role.getChannelId().equals(channelId) &&
                          role.getEmojiId().equals(emojiId))
            .findFirst();
    }

    /**
     * Находит роль на сервере по её ID
     */
    private Role findGuildRole(TextChannel channel, String roleId) {
        return channel.getGuild().getRoleById(roleId);
    }

    /**
     * Обновляет роль участника (добавляет/удаляет)
     */
    private void updateMemberRole(Object event, Role role, boolean isAdd, TextChannel channel) {
        try {
            if (event instanceof MessageReactionAddEvent) {
                MessageReactionAddEvent addEvent = (MessageReactionAddEvent) event;
                channel.getGuild().addRoleToMember(addEvent.getUser(), role).queue();
                log.info(LOG_ROLE_ADDED, role.getName(), addEvent.getUser().getName(), channel.getName());
            } else if (event instanceof MessageReactionRemoveEvent) {
                MessageReactionRemoveEvent removeEvent = (MessageReactionRemoveEvent) event;
                channel.getGuild().removeRoleFromMember(removeEvent.getUser(), role).queue();
                log.info(LOG_ROLE_REMOVED, role.getName(), removeEvent.getUser().getName(), channel.getName());
            }
        } catch (Exception e) {
            log.error("Ошибка при обновлении роли: {}", e.getMessage());
        }
    }
}

