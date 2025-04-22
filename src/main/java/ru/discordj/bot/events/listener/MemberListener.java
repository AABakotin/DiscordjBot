package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.embed.EmbedFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MemberListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(MemberListener.class);

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();
        logger.debug("User join event: {} joined server {}", user.getName(), event.getGuild().getName());
        
        // Отправляем приветственное сообщение в личку
        user.openPrivateChannel()
            .queue(channel -> channel.sendMessageEmbeds(
                EmbedFactory.createWelcomeEmbed().embedWelcomeGuild(
                    user.getEffectiveAvatarUrl(),
                    user.getName()
                )
            ).queue());
    }
} 