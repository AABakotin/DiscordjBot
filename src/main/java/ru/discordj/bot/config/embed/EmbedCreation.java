package ru.discordj.bot.config.embed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import ru.discordj.bot.events.lavaplayer.PlayerManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ru.discordj.bot.config.Constant.INVITATION_LINK;

public class EmbedCreation {
    private static final Date DATE = new Date();


    public static MessageEmbed embedWelcome(String imageServer, String author) {
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

    public static MessageEmbed embedBay(String imageServer, String author) {
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.BLUE)
                .setTitle("█▓▒░⡷⠂𝚃𝚑𝚎 𝚂𝚝𝚎𝚊𝚕𝚝𝚑 𝙳𝚞𝚍𝚎⠐⢾░▒▓█")
                .addField("👋😊 До скорых встреч! ", author.toUpperCase(), true)
                .addField("😉 Ждем тебя снова!", INVITATION_LINK, false)
                .setFooter("📩 " + "requested by @" + author + " " + DATE, imageServer);
        return builder.build();
    }

    public static void playEmbed(TextChannel textChannel) {
        AudioTrack playingTrack = PlayerManager.get().getGuildMusicManager(textChannel.getGuild()).player.getPlayingTrack();
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("▶️" + " Playing: ")
                .addField("*Name:*", "***" + playingTrack.getInfo().title + "***", false)
                .addField("*Duration:*", "***" + timer(playingTrack) + "***", false)
                .addField("*URL:*", "***" + playingTrack.getInfo().uri + "***", false);
        textChannel.sendMessageEmbeds(builder.build()).queue();
    }

    public static void playListEmbed(TextChannel textChannel) {
        List<AudioTrack> playList = PlayerManager.get().getGuildMusicManager(textChannel.getGuild()).getTrackScheduler().getPlayList();
        Collection<Button> buttonList = new ArrayList<>();
        EmbedBuilder builder = new EmbedBuilder();
        if (!playList.isEmpty()) {
            for (int i = 0; i < playList.size(); i++) {
                builder
                        .setTitle("📑" + " Queue: ")
                        .setColor(Color.BLUE)
                        .addField(
                                i + 1 + ".",
                                "***" + playList.get(i).getInfo().title + "\n" + timer(playList.get(i)) + "***",
                                false);
                buttonList.add(Button.danger(playList.get(i).getIdentifier(), "" + ++i));
            }
            textChannel.sendMessageEmbeds(builder.build()).setActionRow(buttonList).queue();
        } else builder.setFooter("📑" + " Queue: is empty").build();

    }


    private static String timer(AudioTrack info) {
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
