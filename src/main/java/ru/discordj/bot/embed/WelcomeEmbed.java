package ru.discordj.bot.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import java.awt.Color;
import java.util.Date;

public class WelcomeEmbed extends BaseEmbed {
        public MessageEmbed embedWelcomeGuild(String imageServer, String author) {  String date;
            date = formatDate();
            EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setTitle("█▓▒░⡷⠂𝚃𝚑𝚎 𝚂𝚝𝚎𝚊𝚕𝚝𝚑 𝙳𝚞𝚍𝚎⠐⢾░▒▓█")
                        .addField("Добро пожаловать!", author.toUpperCase(), false)
                        .addField("✨ ***ВНИМАНИЕ*** ✨",
                                "️1️⃣ Все участники сервера имеют равные права независимо от их времени нахождения" +
                                        " на сервере и занимаемой роли. 🤗. \n" +
                                        "2️⃣ Строго запрещены:\n" +
                                        "🔹 Флуд, злоупотребление матом, т��ллинг в сообщениях; 🫢 \n" +
                                        "🔹 Использование шок-контента; 🫨 \n" +
                                        "🔹 Оскорбление других пользователей; 🤨 \n" +
                                        "🔹 Злоупотребление CAPS LOCK; 🫣 \n" +
                                        "🔹 Запрещена спам-рассылка рекламы; 🧐 \n" +
                                        "🔹 Запрещено включать музыку в микрофон; 😕 \n" +
                                        "🔹 Запрещено издавать громкие звуки в микрофон. 🤫 \n" +
                                        "3️⃣ Реферальная ссылка " + jsonHandler.read().getInviteLink() + " 🤩. \n" +
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
                    .addField("😉 Ждем тебя снова!", jsonHandler.read().getInviteLink(), false)
                    .setFooter("📩 " + "requested by @" + author + " " + new Date(), imageServer);
        return builder.build();
} 
}