package ru.discordj.bot.embed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.utility.IJsonHandler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MusicEmbed extends BaseEmbed {
    private static final int PROGRESS_BAR_LENGTH = 15;
    private static final String PROGRESS_START = "『";
    private static final String PROGRESS_END = "』";
    private static final String PROGRESS_LINE = "═";
    private static final String PROGRESS_CURRENT = "🔮";
    private static final String EMOJI_MUSIC = "🎵";
    private static final String EMOJI_QUEUE = "📝";
    private static final String EMOJI_DURATION = "⌛";
    private static final String EMOJI_AUTHOR = "👑";
    private static final String EMOJI_LINK = "🔗";
    private static final String EMOJI_PLAYING = "▶️";
    private static final String EMOJI_WARNING = "⚠";
    private static final String EMOJI_PAUSED = "⏸️";
    private static final String EMOJI_STOP = "⏹️";
    private static final String EMOJI_REPEAT = "🔄";
    private static final String EMOJI_SKIP = "⏭️";

    @Autowired
    public MusicEmbed(IJsonHandler jsonHandler) {
        super(jsonHandler);
    }

    public void updatePlayerMessage(TextChannel textChannel, String messageId) {
        if (messageId == null) return;
        
        textChannel.retrieveMessageById(messageId)
            .queue(message -> {
                MessageCreateBuilder newMessage = createPlayerMessage(textChannel);
                message.editMessageEmbeds(newMessage.getEmbeds())
                    .setComponents(newMessage.getComponents())
                    .queue(null, error -> {
                        // Если сообщение не найдено, создаем новое
                        textChannel.sendMessage(newMessage.build())
                            .queue(msg -> {
                                PlayerManager.getInstance()
                                    .getGuildMusicManager(textChannel.getGuild())
                                    .getTrackScheduler()
                                    .setPlayerMessage(textChannel, msg.getId());
                            });
                    });
            }, error -> {
                // Если сообщение не найдено, создаем новое
                MessageCreateBuilder newMessage = createPlayerMessage(textChannel);
                textChannel.sendMessage(newMessage.build())
                    .queue(msg -> {
                        PlayerManager.getInstance()
                            .getGuildMusicManager(textChannel.getGuild())
                            .getTrackScheduler()
                            .setPlayerMessage(textChannel, msg.getId());
                    });
            });
    }

    public MessageCreateBuilder createPlayerMessage(TextChannel textChannel) {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        EmbedBuilder embed = createDefaultBuilder();
        
        AudioTrack currentTrack = PlayerManager.getInstance()
            .getGuildMusicManager(textChannel.getGuild())
            .getAudioPlayer()
            .getPlayingTrack();

        if (currentTrack == null) {
            embed.setColor(Color.decode("#2f3136"))
                .setTitle(EMOJI_WARNING + " Нет активного трека")
                .setDescription("Используйте `/play` чтобы включить музыку");
        } else {
            String thumbnailUrl = "https://img.youtube.com/vi/" + currentTrack.getIdentifier() + "/default.jpg";
            
            embed.setColor(Color.decode("#5865F2"))
                .setTitle(EMOJI_PLAYING + " Сейчас играет")
                .setDescription(EMOJI_MUSIC + " **" + currentTrack.getInfo().title + "**\n" +
                              createProgressBar(currentTrack))
                .setThumbnail(thumbnailUrl)
                .addField(EMOJI_DURATION + " Время", 
                    "`" + formatTime(currentTrack.getPosition()) + " / " + formatTime(currentTrack.getDuration()) + "`", true)
                .addField(EMOJI_AUTHOR + " Исполнитель", 
                    "`" + currentTrack.getInfo().author + "`", true)
                .setFooter(EMOJI_LINK + " Источник • " + currentTrack.getInfo().uri);

            addQueueInfo(embed, textChannel, messageBuilder);
        }

        messageBuilder.setEmbeds(embed.build());
        addControlButtons(messageBuilder, textChannel);
        return messageBuilder;
    }

    private void addQueueInfo(EmbedBuilder embed, TextChannel textChannel, MessageCreateBuilder messageBuilder) {
        Queue<AudioTrack> queue = PlayerManager.getInstance()
            .getGuildMusicManager(textChannel.getGuild())
            .getTrackScheduler()
            .getQueue();

        if (!queue.isEmpty()) {
            StringBuilder queueText = new StringBuilder();
            int trackCount = 0;

            // Добавляем треки в список
            for (AudioTrack track : queue) {
                trackCount++;
                String trackTitle = track.getInfo().title;
                if (trackTitle.length() > 50) {
                    trackTitle = trackTitle.substring(0, 47) + "...";
                }
                
                queueText.append(trackCount)
                    .append(". ")
                    .append(trackTitle)
                    .append(" (")
                    .append(formatTime(track.getDuration()))
                    .append(")\n");
            }

            // Добавляем информацию о треках
            embed.addField(EMOJI_QUEUE + " В очереди:", queueText.toString(), false);
        }
    }

    private void addControlButtons(MessageCreateBuilder messageBuilder, TextChannel textChannel) {
        var guildManager = PlayerManager.getInstance().getGuildMusicManager(textChannel.getGuild());
        boolean isPlaying = guildManager.getAudioPlayer().getPlayingTrack() != null;
        boolean isPaused = guildManager.getAudioPlayer().isPaused();
        boolean isRepeat = guildManager.getTrackScheduler().isRepeat();
        
        List<Button> buttons = new ArrayList<>();
        buttons.add(Button.primary("play_pause", isPaused ? EMOJI_PLAYING : EMOJI_PAUSED));
        buttons.add(Button.danger("stop", EMOJI_STOP));
        buttons.add(Button.success("repeat", isRepeat ? EMOJI_REPEAT : "➡"));
        
        if (!guildManager.getTrackScheduler().getQueue().isEmpty()) {
            buttons.add(Button.primary("skip", EMOJI_SKIP));
        }
        
        if (isPlaying) {
            messageBuilder.setActionRow(buttons);
        }
    }

    private String createProgressBar(AudioTrack track) {
        long duration = track.getDuration();
        long position = track.getPosition();
        int progressFilled = (int) ((position * PROGRESS_BAR_LENGTH) / duration);
        
        StringBuilder progressBar = new StringBuilder(PROGRESS_START);
        for (int i = 0; i < PROGRESS_BAR_LENGTH; i++) {
            if (i == progressFilled) {
                progressBar.append(PROGRESS_CURRENT);
            } else {
                progressBar.append(PROGRESS_LINE);
            }
        }
        progressBar.append(PROGRESS_END);
        return "\n" + progressBar.toString();
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long hours = seconds / 3600;
        seconds %= 3600;
        long minutes = seconds / 60;
        seconds %= 60;
        
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%02d:%02d", minutes, seconds);
        }
    }
} 