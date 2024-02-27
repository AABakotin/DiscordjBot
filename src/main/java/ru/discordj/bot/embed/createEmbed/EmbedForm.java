package ru.discordj.bot.embed.createEmbed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import ru.discordj.bot.config.Constant;
import ru.discordj.bot.events.lavaplayer.PlayerManager;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static net.dv8tion.jda.api.interactions.components.buttons.Button.danger;
import static ru.discordj.bot.config.Constant.INVITATION_LINK;

public class EmbedForm {
    private final Date DATE = new Date();

    private static EmbedForm INSTANCE;

    public static EmbedForm get() {
        if (INSTANCE == null) {
            INSTANCE = new EmbedForm();
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


    public MessageCreateData playListEmbed(TextChannel textChannel) {
        MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
        EmbedBuilder builderPlayList = new EmbedBuilder();
        List<AudioTrack> playList = PlayerManager.get().getGuildMusicManager(textChannel.getGuild()).getTrackScheduler().getPlayList();
        AudioTrack playingTrack = PlayerManager.get().getGuildMusicManager(textChannel.getGuild()).player.getPlayingTrack();
        builderPlayList
                .setColor(Color.GREEN)
                .setTitle("Playing: " + " 🎵")
                .addField("*Name:*", "***" + playingTrack.getInfo().title + "***", false)
                .addField("*Duration:*", "***" + timer(playingTrack) + "***", true)
                .addField("*Repeat is:*", "***" + statusRepeat(textChannel) + "***", true)
                .addField("*URL:*", "***" + playingTrack.getInfo().uri + "***", false);

        if (!playList.isEmpty()) {
            List<Button> buttons = new ArrayList<>();
            builderPlayList.addBlankField(true)
                    .addField("________Playlist: ________", "", true);
            for (int i = 0, x = 1; i < playList.size() && x <= 5; i++, x++) {
                builderPlayList
                        .addField(
                                i + 1 + ".",
                                "***" + playList.get(i).getInfo().title + "\n" + timer(playList.get(i)) + "***",
                                false);
                buttons.add(danger(playList.get(i).getInfo().title, "🗑️ " + x));
            }
            messageCreateBuilder.setActionRow(buttons);
        }
        return messageCreateBuilder.setEmbeds(builderPlayList.build()).build();
    }

    public MessageEmbed infoUser(SlashCommandInteractionEvent event) {
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        User target = event.getOption("information", OptionMapping::getAsUser);
        Member member = event.getOption("information", OptionMapping::getAsMember);
        String avatar = target.getAvatarUrl();
        if (avatar == null) avatar = Constant.NON_AVATAR_URL;

        return new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle(target.getName() + "'s info:")
                .setDescription("Join on " + member.getTimeJoined().format(frm))
                .addField("Name", target.getName(), true)
                .addField("Online Status: ", member.getOnlineStatus().getKey(), true)
                .addField("Avatar: ", "The Avatar is below ", false)
                .setImage(avatar)
                .setFooter("requested by " + DATE, event.getGuild().getIconUrl()).build();

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
