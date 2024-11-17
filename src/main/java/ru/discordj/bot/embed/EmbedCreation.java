package ru.discordj.bot.embed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.JdaConfig;
import ru.discordj.bot.lavaplayer.PlayerManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.interactions.components.buttons.Button.danger;
import static ru.discordj.bot.config.Constant.INVITATION_LINK;
import static ru.discordj.bot.config.Constant.TEST_CHANNEL;

public class EmbedCreation {

    private final Date date = new Date();
    private static EmbedCreation instance;
    private static final Logger logger = LoggerFactory.getLogger(EmbedCreation.class);
    private final JDA jda = JdaConfig.getJda().getSelfUser().getJDA();

    public static EmbedCreation get() {
        if (instance == null) {
            instance = new EmbedCreation();
        }
        return instance;
    }

    public MessageEmbed embedWelcomeGuild(String imageServer, String author) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("█▓▒░⡷⠂𝚃𝚑𝚎 𝚂𝚝𝚎𝚊𝚕𝚝𝚑 𝙳𝚞𝚍𝚎⠐⢾░▒▓█")
                .addField("Добро пожаловать!", author.toUpperCase(), false)
                .addField("✨ ***ВНИМАНИЕ*** ✨",
                        "️1️⃣ Все участники сервера имеют равные права независимо от их времени нахождения" +
                                " на сервере и занимаемой роли. 🤗. \n" +
                                "2️⃣ Строго запрещены:\n" +
                                "🔹 Флуд, злоупотребление матом, троллинг в сообщениях; 🫢 \n" +
                                "🔹 Использование шок-контента; 🫨 \n" +
                                "🔹 Оскорбление других пользователей; 🤨 \n" +
                                "🔹 Злоупотребление CAPS LOCK; 🫣 \n" +
                                "🔹 Запрещена спам-рассылка рекламы; 🧐 \n" +
                                "🔹 Запрещено включать музыку в микрофон; 😕 \n" +
                                "🔹 Запрещено издавать громкие звуки в микрофон. 🤫 \n" +
                                "3️⃣ Реферальная ссылка " + INVITATION_LINK + " 🤩. \n" +
                                "4️⃣ Надеемся, что тебе понравится с нами. 🫡",
                        false)
                .setFooter("📩 " + "requested by @" + author + " " + date, imageServer);
        return builder.build();
    }

    public MessageEmbed embedLeaveGuild(String imageServer, String author) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("█▓▒░⡷⠂𝚃𝚑𝚎 𝚂𝚝𝚎𝚊𝚕𝚝𝚑 𝙳𝚞𝚍𝚎⠐⢾░▒▓█")
                .addField("👋😊 До скорых встреч! ", author.toUpperCase(), true)
                .addField("😉 Ждем тебя снова!", INVITATION_LINK, false)
                .setFooter("📩 " + "requested by @" + author + " " + date, imageServer);
        return builder.build();
    }

    public void playListEmbed(TextChannel textChannel) {
        List<AudioTrack> playList = PlayerManager.getInstance().getGuildMusicManager(textChannel.getGuild()).getTrackScheduler().getPlayList();
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        EmbedBuilder builderPlayList = new EmbedBuilder();
        AudioTrack playingTrack = PlayerManager.getInstance().getGuildMusicManager(textChannel.getGuild()).player.getPlayingTrack();
        builderPlayList
                .setColor(Color.GREEN)
                .setThumbnail(playingTrack.getInfo().artworkUrl)
                .addField("*Name:*", "***" + playingTrack.getInfo().title + "***", false)
                .addField("*Duration:*", "***" + timer(playingTrack) + "***", true)
                .addField("*Repeat is:*", "***" + statusRepeat(textChannel) + "***", true)
                .addField("*URL:*", "***" + playingTrack.getInfo().uri + "***", false);
        messageCreateBuilder.setEmbeds(builderPlayList.build());
        if (playList.isEmpty()) {
            textChannel.sendMessage(messageCreateBuilder.build()).queue();
            return;
        }

        List<Button> buttons = new ArrayList<>();
        builderPlayList.addBlankField(true)
                .addField("Playlist:", "", true);
        for (int i = 0, x = 1; i < playList.size(); i++, x++) {
            builderPlayList
                    .setThumbnail(playingTrack.getInfo().artworkUrl)
                    .addField(
                            i + 1 + ".",
                            "***" + playList.get(i).getInfo().title + "\n" + timer(playList.get(i)) + "***",
                            false);
            buttons.add(danger(playList.get(i).getInfo().title, "🗑️ " + x));
        }
        try {
            messageCreateBuilder.setActionRow(buttons);
        } catch (IllegalArgumentException ex) {
            buttons.clear();
            builderPlayList.setFooter("Сорри, отваливаются кнопки! 🤫");
            logger.error(ex.getMessage());
        }
        messageCreateBuilder.setEmbeds(builderPlayList.build());
        textChannel.sendMessage(messageCreateBuilder.build()).queue();
    }

    public void embedServerStatus(Map<String, String> receive) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        EmbedBuilder builder = new EmbedBuilder();

        builder
                .setTitle("█▓▒░⡷⠂Monitoring Games Servers⠐⢾░▒▓█")
                .setColor(Color.BLUE)
                .setFooter("📩 " + "requested by @" + "author" + " " + date);
        receive.forEach((key, value) -> builder.addField(key, value, true));

        messageCreateBuilder.setEmbeds(builder.build());

        TextChannel textChannelById = jda.getTextChannelById(TEST_CHANNEL);

        if (textChannelById != null) {
            MessageCreateAction message = textChannelById.sendMessage(messageCreateBuilder.build());
            String latestMessageId = textChannelById.getLatestMessageId();
            textChannelById.deleteMessageById(latestMessageId)
                    .queue(ok -> message.queue(), not -> message.queue());
        }
    }

    private String statusRepeat(TextChannel textChannel) {
        return PlayerManager.getInstance()
                .getGuildMusicManager(textChannel.getGuild())
                .getTrackScheduler()
                .isRepeat() ? "🔁" : "➡️";
    }

    private String timer(AudioTrack info) {
        return String.format(
                "%02d : %02d : %02d",
                TimeUnit.MILLISECONDS.toHours(info.getDuration())
                        - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(info.getDuration())),
                TimeUnit.MILLISECONDS.toMinutes(info.getDuration())
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(info.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(info.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(info.getDuration()))
        );
    }
}
