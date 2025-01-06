package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.embed.EmbedFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberListener extends ListenerAdapter {
    @Autowired
    private EmbedFactory embedFactory;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        User user = event.getUser();
        // Отправляем приветственное сообщение в личку
        user.openPrivateChannel()
            .queue(channel -> channel.sendMessageEmbeds(
                embedFactory.createWelcomeEmbed().embedWelcomeGuild(
                    user.getEffectiveAvatarUrl(),
                    user.getName()
                )
            ).queue());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        User user = event.getUser();
        // Отправляем прощальное сообщение в личку
        user.openPrivateChannel()
            .queue(channel -> channel.sendMessageEmbeds(
                embedFactory.createWelcomeEmbed().embedLeaveGuild(
                    user.getEffectiveAvatarUrl(),
                    user.getName()
                )
            ).queue());
    }
} 