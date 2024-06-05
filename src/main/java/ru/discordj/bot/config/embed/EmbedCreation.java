package ru.discordj.bot.config.embed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.lavaplayer.PlayerManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.interactions.components.buttons.Button.danger;
import static ru.discordj.bot.config.Constant.INVITATION_LINK;

public class EmbedCreation {
    private final Date DATE = new Date();

    private static EmbedCreation INSTANCE;
    private static final Logger logger = LoggerFactory.getLogger(EmbedCreation.class);

    public static EmbedCreation get() {
        if (INSTANCE == null) {
            INSTANCE = new EmbedCreation();
        }
        return INSTANCE;
    }

    public MessageEmbed embedWelcome(String imageServer, String author) {
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
                .setFooter("📩 " + "requested by @" + author + " " + DATE, imageServer);
        return builder.build();
    }

    public MessageEmbed embedBay(String imageServer, String author) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("█▓▒░⡷⠂𝚃𝚑𝚎 𝚂𝚝𝚎𝚊𝚕𝚝𝚑 𝙳𝚞𝚍𝚎⠐⢾░▒▓█")
                .addField("👋😊 До скорых встреч! ", author.toUpperCase(), true)
                .addField("😉 Ждем тебя снова!", INVITATION_LINK, false)
                .setFooter("📩 " + "requested by @" + author + " " + DATE, imageServer);
        return builder.build();
    }

    public void playListEmbed(TextChannel textChannel) {
        List<AudioTrack> playList = PlayerManager.get().getGuildMusicManager(textChannel.getGuild()).getTrackScheduler().getPlayList();
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        EmbedBuilder builderPlayList = new EmbedBuilder();
        AudioTrack playingTrack = PlayerManager.get().getGuildMusicManager(textChannel.getGuild()).player.getPlayingTrack();
        builderPlayList
                .setColor(Color.GREEN)
                .setTitle("Playing: " + " 🎵")
                .addField("*Name:*", "***" + playingTrack.getInfo().title + "***", false)
                .addField("*Duration:*", "***" + timer(playingTrack) + "***", true)
                .addField("*Repeat is:*", "***" + statusRepeat(textChannel) + "***", true)
                .addField("*URL:*", "***" + playingTrack.getInfo().uri + "***", false);
        messageCreateBuilder.setEmbeds(builderPlayList.build());
        if (!playList.isEmpty()) {
            List<Button> buttons = new ArrayList<>();

            builderPlayList.addBlankField(true)
                    .addField("Playlist:", "", true);
            for (int i = 0, x = 1; i < playList.size(); i++, x++) {
                builderPlayList
                        .addField(
                                i + 1 + ".",
                                "***" + playList.get(i).getInfo().title + "\n" + timer(playList.get(i)) + "***",
                                false);
                buttons.add(danger(playList.get(i).getInfo().title, "🗑️ " + x));
            }
            try{
                messageCreateBuilder.setActionRow(buttons);
            } catch (IllegalArgumentException ex){
                buttons.clear();
                builderPlayList.setFooter("Сорри, отваливаются кнопки! 🤫");
                logger.error(ex.getMessage());
            }
            messageCreateBuilder.setEmbeds(builderPlayList.build());
            textChannel.sendMessage(messageCreateBuilder.build()).queue();
        } else {
            textChannel.sendMessage(messageCreateBuilder.build()).queue();
        }
    }

    private String statusRepeat(TextChannel textChannel) {
        if (PlayerManager.get().getGuildMusicManager(textChannel.getGuild()).getTrackScheduler().isRepeat()) {
            return "🔁";
        }
        return "➡️";
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
