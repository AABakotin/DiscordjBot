package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.utility.JsonHandler;
import ru.discordj.bot.config.utility.JsonParse;
import ru.discordj.bot.embed.EmbedCreation;

import java.util.Objects;


public class AddRoleListener extends ListenerAdapter {

    private final JsonHandler jsonHandler = JsonParse.getInstance();
    private static final Logger logger = LoggerFactory.getLogger(AddRoleListener.class);


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        String emoji = event.getEmoji().getName();
        String channelByEmoji = getChannelByEmoji(emoji);
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(channelByEmoji);

        if (guestGuildChannel == event.getGuildChannel()) {
            try {
                event.getGuild()
                        .addRoleToMember(
                                Objects.requireNonNull(event.getMember()).getUser(),
                                Objects.requireNonNull(event.getGuild().getRoleById(getRoleByEmoji(emoji)))
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

            logger.info("User {} subscribe {}", event.getUser().getName(), event.getUser().getJDA().getRoleById(getRoleByEmoji(emoji)));
        }
    }


    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {

        String emoji = event.getEmoji().getName();
        String channelByEmoji = getChannelByEmoji(emoji);
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(channelByEmoji);

        if (guestGuildChannel == event.getGuildChannel()) {
            try {
                event.getGuild()
                        .removeRoleFromMember(
                                Objects.requireNonNull(event.getMember()).getUser(),
                                Objects.requireNonNull(event.getGuild().getRoleById(getRoleByEmoji(emoji)))
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

            logger.info("User {} unsubscribe {}", event.getUser().getName(), event.getUser().getJDA().getRoleById(getRoleByEmoji(emoji)));
        }
    }

    private String getChannelByEmoji(String emoji) {
       return  jsonHandler.read().getRoles().stream()
                .filter(e -> e.getEmoji().equals(emoji))
                .findFirst().get().getChannel();

    }

    private  String getRoleByEmoji(String emoji) {
        return jsonHandler.read().getRoles()
                .stream()
                .filter(e -> e.getEmoji().equals(emoji)).findFirst().get().getRole();
    }
}

