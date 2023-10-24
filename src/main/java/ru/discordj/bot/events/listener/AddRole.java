package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.embed.EmbedCreation;
import ru.discordj.bot.config.JDA;

import static ru.discordj.bot.config.Constant.GUEST_CHANNEL;


public class AddRole extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AddRole.class);


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(GUEST_CHANNEL);

        if (guestGuildChannel == event.getGuildChannel()) {
            String emoji = event.getEmoji().getName();
            event.getGuild()
                    .addRoleToMember(
                            event.getMember().getUser(),
                            event.getGuild().getRoleById(JDA.getRoleToEmoji(emoji))
                    )
                    .queue();

            String imageServer = event.getGuild().getIconUrl();
            String author = event.getUser().getName();

            event.getUser()
                    .openPrivateChannel()
                    .complete()
                    .sendMessageEmbeds(EmbedCreation.get().embedWelcome(imageServer, author)).queue();

            logger.info("User " + event.getUser().getName() + " subscribe " + emoji);
        }
    }


    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(GUEST_CHANNEL);

        if (guestGuildChannel == event.getGuildChannel()) {
            String emoji = event.getEmoji().getName();

            event.getGuild()
                    .removeRoleFromMember(
                            event.getMember().getUser(),
                            event.getGuild().getRoleById(JDA.getRoleToEmoji(emoji))
                    )
                    .queue();

            String imageServer = event.getGuild().getIconUrl();
            String author = event.getUser().getName();

            event.getUser()
                    .openPrivateChannel()
                    .complete()
                    .sendMessageEmbeds(EmbedCreation.get().embedBay(imageServer, author))
                    .queue();

            logger.info("User " + event.getUser().getName() + " unsubscribe " + emoji);
        }
    }
}

