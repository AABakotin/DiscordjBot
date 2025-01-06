package ru.discordj.bot.embed;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.utility.IJsonHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MusicEmbed extends BaseEmbed {
    private static final int PROGRESS_BAR_LENGTH = 15;
    private static final String PROGRESS_START = "『";
    private static final String PROGRESS_END = "』";
    private static final String PROGRESS_LINE = "═";
    private static final String PROGRESS_CURRENT = "🔮";
    
    // Эмодзи для плеера
    private static final Map<String, String> EMOJI = Map.ofEntries(
        Map.entry("MUSIC", "🎵"),
        Map.entry("QUEUE", "📝"),
        Map.entry("DURATION", "⌛"),
        Map.entry("AUTHOR", "👑"),
        Map.entry("LINK", "🔗"),
        Map.entry("PLAYING", "▶️"),
        Map.entry("WARNING", "⚠"),
        Map.entry("PAUSED", "⏸️"),
        Map.entry("STOP", "⏹️"),
        Map.entry("REPEAT", "🔄"),
        Map.entry("SKIP", "⏭️")
    );

    @Autowired
    public MusicEmbed(IJsonHandler jsonHandler) {
        super(jsonHandler);
    }

    /**
     * Обновляет сообщение плеера
     */
    public void updatePlayerMessage(TextChannel textChannel, String messageId) {
        if (messageId == null) return;
        
        textChannel.retrieveMessageById(messageId)
            .queue(
                message -> updateExistingMessage(message, textChannel),
                error -> createNewMessage(textChannel)
            );
    }

    /**
     * Создает сообщение плеера
     */
    public MessageCreateBuilder createPlayerMessage(TextChannel textChannel) {
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        EmbedBuilder embed = createDefaultBuilder();
        
        AudioTrack currentTrack = getCurrentTrack(textChannel);
        updateEmbedWithTrackInfo(embed, currentTrack, textChannel);
        addQueueInfo(embed, textChannel);
        
        messageBuilder.setEmbeds(embed.build());
        addControlButtons(messageBuilder, textChannel);
        
        return messageBuilder;
    }

    private void updateExistingMessage(Message message, TextChannel textChannel) {
        MessageCreateBuilder newMessage = createPlayerMessage(textChannel);
        message.editMessageEmbeds(newMessage.getEmbeds())
            .setComponents(newMessage.getComponents())
            .queue(null, error -> createNewMessage(textChannel));
    }

    private void createNewMessage(TextChannel textChannel) {
        MessageCreateBuilder newMessage = createPlayerMessage(textChannel);
        textChannel.sendMessage(newMessage.build())
            .queue(msg -> updateTrackScheduler(textChannel, msg.getId()));
    }

    private AudioTrack getCurrentTrack(TextChannel textChannel) {
        return PlayerManager.getInstance()
            .getGuildMusicManager(textChannel.getGuild())
            .getAudioPlayer()
            .getPlayingTrack();
    }

    private void updateEmbedWithTrackInfo(EmbedBuilder embed, AudioTrack track, TextChannel textChannel) {
        if (track != null) {
            embed.setTitle(EMOJI.get("MUSIC") + " Сейчас играет:")
                .addField(EMOJI.get("MUSIC") + " Название:", track.getInfo().title, false)
                .addField(EMOJI.get("AUTHOR") + " Автор:", track.getInfo().author, true)
                .addField(EMOJI.get("DURATION") + " Длительность:", 
                    formatTime(track.getPosition()) + "/" + formatTime(track.getDuration()), true)
                .addField(EMOJI.get("LINK") + " Ссылка:", track.getInfo().uri, false)
                .addField("Прогресс:", createProgressBar(track), false);
        } else {
            embed.setTitle(EMOJI.get("WARNING") + " Нет активного трека");
        }
    }

    private void addQueueInfo(EmbedBuilder embed, TextChannel textChannel) {
        Queue<AudioTrack> queue = PlayerManager.getInstance()
            .getGuildMusicManager(textChannel.getGuild())
            .getTrackScheduler()
            .getQueue();

        if (!queue.isEmpty()) {
            embed.addField(EMOJI.get("QUEUE") + " В очереди:", formatQueueInfo(queue), false);
        }
    }

    private String formatQueueInfo(Queue<AudioTrack> queue) {
        StringBuilder queueText = new StringBuilder();
        int trackCount = 0;

        for (AudioTrack track : queue) {
            trackCount++;
            String trackTitle = truncateString(track.getInfo().title, 50);
            queueText.append(String.format("%d. %s (%s)\n", 
                trackCount, trackTitle, formatTime(track.getDuration())));
        }

        return queueText.toString();
    }

    private void addControlButtons(MessageCreateBuilder messageBuilder, TextChannel textChannel) {
        var guildManager = PlayerManager.getInstance().getGuildMusicManager(textChannel.getGuild());
        boolean isPlaying = guildManager.getAudioPlayer().getPlayingTrack() != null;
        boolean isPaused = guildManager.getAudioPlayer().isPaused();
        boolean isRepeat = guildManager.getTrackScheduler().isRepeat();
        
        if (isPlaying) {
            List<Button> buttons = new ArrayList<>();
            buttons.add(Button.primary("play_pause", isPaused ? EMOJI.get("PLAYING") : EMOJI.get("PAUSED")));
            buttons.add(Button.danger("stop", EMOJI.get("STOP")));
            buttons.add(Button.success("repeat", isRepeat ? EMOJI.get("REPEAT") : "➡"));
            
            if (!guildManager.getTrackScheduler().getQueue().isEmpty()) {
                buttons.add(Button.primary("skip", EMOJI.get("SKIP")));
            }
            
            messageBuilder.setActionRow(buttons);
        }
    }

    private String truncateString(String str, int length) {
        return str.length() > length ? str.substring(0, length - 3) + "..." : str;
    }

    private void updateTrackScheduler(TextChannel textChannel, String messageId) {
        PlayerManager.getInstance()
            .getGuildMusicManager(textChannel.getGuild())
            .getTrackScheduler()
            .setPlayerMessage(textChannel, messageId);
    }

    private String createProgressBar(AudioTrack track) {
        long duration = track.getDuration();
        long position = track.getPosition();
        int progressFilled = (int) ((position * PROGRESS_BAR_LENGTH) / duration);
        
        StringBuilder progressBar = new StringBuilder(PROGRESS_START);
        for (int i = 0; i < PROGRESS_BAR_LENGTH; i++) {
            progressBar.append(i == progressFilled ? PROGRESS_CURRENT : PROGRESS_LINE);
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
        
        return hours > 0 
            ? String.format("%d:%02d:%02d", hours, minutes, seconds)
            : String.format("%02d:%02d", minutes, seconds);
    }
} 