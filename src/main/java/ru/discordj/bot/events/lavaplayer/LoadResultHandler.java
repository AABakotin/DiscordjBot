package ru.discordj.bot.events.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.embed.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

import static ru.discordj.bot.config.Constant.MAX_SIZE_FUNDED_SONGS;

public class LoadResultHandler implements AudioLoadResultHandler {

    private final GuildMusicManager guildMusicManager;
    private static final Logger logger = LoggerFactory.getLogger(LoadResultHandler.class);
    private final SlashCommandInteractionEvent event;


    public LoadResultHandler(GuildMusicManager guildMusicManager, SlashCommandInteractionEvent textChannel) {
        this.guildMusicManager = guildMusicManager;
        this.event = textChannel;
    }


    @Override
    public void trackLoaded(AudioTrack track) {
        guildMusicManager.getTrackScheduler().queue(track.makeClone());
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        final List<AudioTrack> tracks = playlist.getTracks()
                .stream()
                .limit(MAX_SIZE_FUNDED_SONGS)
                .collect(Collectors.toList());

        if (!tracks.isEmpty()) {
            for (AudioTrack track : tracks) {
                guildMusicManager.getTrackScheduler().queue(track);
            }
            SendMessage.playList(event);
        }
    }

    @Override
    public void noMatches() {
        logger.warn("noMatches.");
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        logger.error("Something broke when playing the track.");
    }


}
