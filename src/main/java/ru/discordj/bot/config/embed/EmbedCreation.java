package ru.discordj.bot.config.embed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
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

    public static MessageEmbed embedMusic(AudioTrackInfo info) {
        timer(info);
        EmbedBuilder builder = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("▶️" + " Playing: ")
                .addField("*Name:*", "***" + info.title + "***", false)
                .addField("*Duration:*", "***" + timer(info) + "***", false)
                .addField("*URL:*", "***" + info.uri + "***", false);
        return builder.build();
    }

    public static MessageEmbed embedMusic(List<AudioTrack> info) {
        EmbedBuilder builder = new EmbedBuilder();
        if (!info.isEmpty()) {
            for (int i = 0; i < info.size(); i++) {
                builder
                        .setTitle("📑" + " Queue: ")
                        .setColor(Color.BLUE)
                        .addField(
                                i + 1 + ".",
                                "***" + info.get(i).getInfo().title + "\n" + timer(info.get(i).getInfo()) + "***",
                                false);
            }
        } else return builder.setFooter("📑" + " Queue: is empty").build();
        return builder.build();
    }


    private static String timer(AudioTrackInfo info) {
        return String.format(
                "%02d : %02d : %02d",
                TimeUnit.MILLISECONDS.toHours(info.length)
                        - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(info.length)),
                TimeUnit.MILLISECONDS.toMinutes(info.length)
                        - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(info.length)),
                TimeUnit.MILLISECONDS.toSeconds(info.length) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(info.length))
        );

    }
}
