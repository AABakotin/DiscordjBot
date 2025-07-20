package ru.discordj.bot.service;

import net.dv8tion.jda.api.entities.Guild;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.ServerRules;
import ru.discordj.bot.utility.pojo.Roles;

public class RoleAssignmentService {
    private final JsonParse jsonHandler = JsonParse.getInstance();

    public String getChannelByEmoji(Guild guild, String emojiId) {
        ServerRules root = jsonHandler.read(guild);
        return root.getRoles().stream()
            .filter(role -> role.getEmojiId().equals(emojiId))
            .map(Roles::getChannelId)
            .findFirst()
            .orElse(null);
    }

    public String getRoleByEmoji(Guild guild, String emoji) {
        ServerRules root = jsonHandler.read(guild);
        return root.getRoles().stream()
            .filter(e -> e.getEmojiId().equals(emoji))
            .findFirst()
            .map(Roles::getRoleId)
            .orElse(null);
    }
} 