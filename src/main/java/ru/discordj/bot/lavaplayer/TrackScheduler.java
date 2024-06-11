package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.discordj.bot.embed.EmbedCreation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {

    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean isRepeat = false;

    public TrackScheduler(AudioPlayer player) {
        this.queue = new LinkedBlockingQueue<>();
        this.player = player;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (isRepeat) {
                player.startTrack(track.makeClone(), false);
            } else {
                player.startTrack(this.queue.poll(), false);
            }
        }
    }

    public void skip(TextChannel textChannel) {
        if (this.queue.peek() != null) {
            this.player.startTrack(this.queue.peek().makeClone(), false);
            getQueue().poll();
            EmbedCreation.get().playListEmbed(textChannel);
        } else {
            EmbedCreation.get().playListEmbed(textChannel);
        }
    }

    public void queue(AudioTrack track) {
        if (!this.player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void removeTrack(String Identifier) {
        if (!Identifier.isEmpty()) {
            getQueue().removeIf(e -> e.getInfo().title.equals(Identifier));
        }
    }

    public AudioPlayer getPlayer() {
        return player;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    public boolean isRepeat() {
        return isRepeat;
    }

    public void setRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public List<AudioTrack> getPlayList() {
        return new ArrayList<>(queue);
    }

}