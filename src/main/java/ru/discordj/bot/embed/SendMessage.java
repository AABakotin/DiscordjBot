package ru.discordj.bot.embed;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.embed.createEmbed.EmbedForm;

import static java.util.concurrent.TimeUnit.SECONDS;
import static ru.discordj.bot.config.Constant.MESSAGE_LIFETIME;

public class SendMessage {
    private static final Logger logger = LoggerFactory.getLogger(SendMessage.class);

    private static final EmbedForm embed = EmbedForm.get();

    public static void playList(SlashCommandInteractionEvent event) {
        TextChannel textChannel = event.getChannel().asTextChannel();
            event.getMessageChannel().deleteMessageById(textChannel.getLatestMessageId()).queue(
                    ok -> event.reply(embed.playListEmbed(textChannel)).queue(),
                    error -> event.reply(embed.playListEmbed(textChannel)).queue());


    }

    public static void stopPlayer(SlashCommandInteractionEvent event) {
        event.getChannel()
                .deleteMessageById(event.getChannel().getLatestMessageId())
                .queue(e -> event.reply("Music playback is stop.")
                                .delay(MESSAGE_LIFETIME, SECONDS)
                                .queue(del -> event.getChannel()
                                        .deleteMessageById(event.getChannel().getLatestMessageId())
                                        .queue()),
                        error -> event.reply("Music playback is stop.").queue());
    }

    public static void sendRules(SlashCommandInteractionEvent event) {
        String imageServer = event.getGuild().getIconUrl();
        String author = event.getUser().getName();
        event.getUser()
                .openPrivateChannel()
                .complete()
                .sendMessageEmbeds(embed.embedWelcome(imageServer, author))
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
        event.replyEmbeds(embed.infoUser(event))
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
