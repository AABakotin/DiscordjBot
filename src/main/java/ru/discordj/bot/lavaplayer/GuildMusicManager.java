package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;

public class GuildMusicManager {

    public final AudioPlayer player;
    private final TrackScheduler trackScheduler;
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild) {
        this.player = manager.createPlayer();
        this.trackScheduler = new TrackScheduler(this.player);
        this.player.addListener(this.trackScheduler);
        this.sendHandler = new AudioPlayerSendHandler(this.player);
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}