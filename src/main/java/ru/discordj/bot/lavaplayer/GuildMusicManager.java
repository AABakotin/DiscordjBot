package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.Guild;
import ru.discordj.bot.embed.EmbedFactory;

public class GuildMusicManager {

    private final AudioPlayer player;
    private final TrackScheduler scheduler;
    private final AudioPlayerSendHandler sendHandler;

    public GuildMusicManager(AudioPlayerManager manager, Guild guild, EmbedFactory embedFactory) {
        this.player = manager.createPlayer();
        this.scheduler = new TrackScheduler(player, embedFactory);
        this.player.addListener(scheduler);
        this.sendHandler = new AudioPlayerSendHandler(player);
    }

    public AudioPlayerSendHandler getSendHandler() {
        return sendHandler;
    }

    public TrackScheduler getTrackScheduler() {
        return scheduler;
    }

    public AudioPlayer getAudioPlayer() {
        return player;
    }
}