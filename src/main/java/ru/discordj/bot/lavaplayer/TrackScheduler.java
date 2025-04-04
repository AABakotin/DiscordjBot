package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.discordj.bot.embed.EmbedFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean isRepeat = false;
    private String playerMessageId;
    private TextChannel textChannel;
    private TrackSelectionData trackSelectionData;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> updateTask;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void setPlayerMessage(TextChannel channel, String messageId) {
        this.textChannel = channel;
        this.playerMessageId = messageId;
        updatePlayerMessage();
    }

    private void updatePlayerMessage() {
        if (textChannel != null && playerMessageId != null) {
            EmbedFactory.getInstance().createMusicEmbed()
                .updatePlayerMessage(textChannel, playerMessageId);
        }
    }

    public void startUpdateTask() {
        stopUpdateTask(); // Останавливаем предыдущую задачу если она существует
        
        updateTask = scheduler.scheduleAtFixedRate(() -> {
            if (player.getPlayingTrack() != null && textChannel != null && playerMessageId != null) {
                updatePlayerMessage();
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    public void stopUpdateTask() {
        if (updateTask != null) {
            updateTask.cancel(false);
            updateTask = null;
        }
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        startUpdateTask();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (isRepeat) {
                this.player.startTrack(track.makeClone(), false);
            } else {
                nextTrack();
            }
        }
        
        if (player.getPlayingTrack() == null) {
            stopUpdateTask();
        }
        
        updatePlayerMessage();
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
        updatePlayerMessage();
    }

    public void skip() {
        if (isRepeat) {
            AudioTrack currentTrack = player.getPlayingTrack();
            if (currentTrack != null) {
                player.startTrack(currentTrack.makeClone(), false);
            }
        } else {
            nextTrack();
        }
        updatePlayerMessage();
    }

    public void togglePause() {
        player.setPaused(!player.isPaused());
        updatePlayerMessage();
    }

    public void stop() {
        queue.clear();
        player.stopTrack();
        stopUpdateTask();
        // Очищаем ID сообщения плеера и текстовый канал
        this.playerMessageId = null;
        this.textChannel = null;
        // НЕ вызываем updatePlayerMessage() так как сообщение будет удалено
    }

    public void toggleRepeat() {
        isRepeat = !isRepeat;
        updatePlayerMessage();
    }

    public void removeTrack(String title) {
        List<AudioTrack> currentQueue = new ArrayList<>(queue);
        queue.clear();
        currentQueue.stream()
            .filter(track -> !track.getInfo().title.equals(title))
            .forEach(queue::offer);
        updatePlayerMessage();
    }

    public void setTrackSelectionData(TrackSelectionData data) {
        this.trackSelectionData = data;
    }

    public TrackSelectionData getTrackSelectionData() {
        return trackSelectionData;
    }

    // Геттеры
    public AudioPlayer getPlayer() { return player; }
    public BlockingQueue<AudioTrack> getQueue() { return queue; }
    public boolean isRepeat() { return isRepeat; }
    public String getPlayerMessageId() { return playerMessageId; }
    public TextChannel getTextChannel() { return textChannel; }

    public List<AudioTrack> getPlayList() {
        return new ArrayList<>(queue);
    }

    private void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

}