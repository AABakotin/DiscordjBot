package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.JdaConfig;
import ru.discordj.bot.embed.EmbedCreation;

import java.util.Objects;

import static ru.discordj.bot.config.Constant.GUEST_CHANNEL;


public class AddRoleListener extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(AddRoleListener.class);

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(GUEST_CHANNEL);
        String emoji = event.getEmoji().getName();

        if (guestGuildChannel == event.getGuildChannel()) {
            try {
                event.getGuild()
                        .addRoleToMember(
                                Objects.requireNonNull(event.getMember()).getUser(),
                                event.getGuild().getRoleById(JdaConfig.getRoleToEmoji(emoji))
                        )
                        .queue();
            } catch (IllegalArgumentException e) {
                logger.error("onMessageReactionAdd: ID emoji or ID role in property.env is NULL");
            }


            String imageServer = event.getGuild().getIconUrl();
            String author = Objects.requireNonNull(event.getUser()).getName();

            event.getUser()
                    .openPrivateChannel()
                    .complete()
                    .sendMessageEmbeds(EmbedCreation.get().embedWelcomeGuild(imageServer, author)).queue();

            logger.info("User {} subscribe {}", event.getUser().getName(), event.getUser().getJDA().getRoleById(JdaConfig.getRoleToEmoji(emoji)));
        }
    }


    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {

        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(GUEST_CHANNEL);

        if (guestGuildChannel == event.getGuildChannel()) {
            String emoji = event.getEmoji().getName();

            try {
                event.getGuild()
                        .removeRoleFromMember(
                                Objects.requireNonNull(event.getMember()).getUser(),
                                event.getGuild().getRoleById(JdaConfig.getRoleToEmoji(emoji))
                        )
                        .queue();
            } catch (IllegalArgumentException e) {
                logger.error("onMessageReactionRemove: ID emoji or ID role in property.env is NULL");
            }

            String imageServer = event.getGuild().getIconUrl();
            String author = Objects.requireNonNull(event.getUser()).getName();

            event.getUser()
                    .openPrivateChannel()
                    .complete()
                    .sendMessageEmbeds(EmbedCreation.get().embedLeaveGuild(imageServer, author))
                    .queue();

            logger.info("User {} unsubscribe {}", event.getUser().getName(), event.getUser().getJDA().getRoleById(JdaConfig.getRoleToEmoji(emoji)));
        }
    }
}

