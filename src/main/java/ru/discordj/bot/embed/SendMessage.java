package ru.discordj.bot.embed;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.embed.createEmbed.EmbedForm;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.discordj.bot.config.Constant.MESSAGE_LIFETIME;

public class SendMessage {

    private static final Logger logger = LoggerFactory.getLogger(SendMessage.class);




    public static void playList(SlashCommandInteractionEvent event) {
        IEmbed EMBED = new EmbedForm();
            event.getMessageChannel().deleteMessageById(event.getChannel().getLatestMessageId()).queue(
                    ok -> event.reply(EMBED.playListEmbed(event.getChannel().asTextChannel())).queue(),
                    error ->  event.reply(EMBED.playListEmbed(event.getChannel().asTextChannel())).queue()
            );
    }

    public static void stopPlayer(SlashCommandInteractionEvent event) {
        event.getMessageChannel()
                .deleteMessageById(event.getMessageChannel().getLatestMessageId())
                .queue(e -> event.reply("Music playback is stop.")
                                .delay(MESSAGE_LIFETIME, SECONDS)
                                .queue(del -> event.getMessageChannel()
                                        .deleteMessageById(event.getMessageChannel().getLatestMessageId())
                                        .queue()),
                        error -> event.reply("Music playback is stop.").queue());
    }

    public static void sendRules(SlashCommandInteractionEvent event) {
        IEmbed EMBED = new EmbedForm();
        String imageServer = event.getGuild().getIconUrl();
        String author = event.getUser().getName();
        event.getUser()
                .openPrivateChannel()
                .complete()
                .sendMessageEmbeds(EMBED.embedWelcome(imageServer, author))
                .queue(success -> event.reply("Server rules sent as a private message.").setEphemeral(true)
                                .queue(ok -> logger.info("requested 'rules' by @" + author)),
                        failure -> logger.error("Some error occurred in 'ping', try again!"));

    }

    public static void sendPingPong(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true)
                .flatMap(v ->
                        event.getHook()
                                .editOriginalFormat(
                                        "Pong: %d ms", System.currentTimeMillis() - time))
                .queue(
                        success -> logger.info("requested 'ping' by @" + event.getUser().getName()),
                        failure -> logger.error("Some error occurred in 'ping', try again!")
                );
    }

    public static void infoUser(SlashCommandInteractionEvent event) {
        IEmbed EMBED = new EmbedForm();
        event.replyEmbeds(EMBED.infoUser(event))
                .queue(
                        success -> logger.info("requested 'info' by @" + event.getUser().getName()),
                        failure -> logger.error("Some error occurred in 'info', try again!")
                );
    }

    public static void sendHello(SlashCommandInteractionEvent event) {
        String userName = event.getMember().getUser().getName();
        event.reply("Hello " + userName + " ,my friend " + event.getUser().getAvatarUrl())
                .setEphemeral(true)
                .queue(
                        success -> logger.info("requested 'hello' by @" + event.getUser().getName()),
                        failure -> logger.error("Some error occurred in 'hello', try again!")
                );
    }

}
