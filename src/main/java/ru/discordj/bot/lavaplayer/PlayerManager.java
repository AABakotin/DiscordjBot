package ru.discordj.bot.lavaplayer;


import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidWithThumbnail;
import dev.lavalink.youtube.clients.MusicWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import dev.lavalink.youtube.clients.skeleton.Client;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.embed.EmbedCreation;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;
    private static final Logger logger = LoggerFactory.getLogger(PlayerManager.class);
    private final long MAX_SIZE = 1L;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(
                true,
                new Client[] { new MusicWithThumbnail(), new WebWithThumbnail(), new AndroidWithThumbnail() }
        );
        audioPlayerManager.registerSourceManager(youtube);
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static PlayerManager get() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerManager();
        }
        return INSTANCE;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }


    public void play(TextChannel textChannel, String trackURL) {
        final GuildMusicManager guildMusicManager = getGuildMusicManager(textChannel.getGuild());
        this.audioPlayerManager.loadItemOrdered(
                guildMusicManager,
                trackURL,
                new AudioLoadResultHandler()
                {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track.makeClone());
                EmbedCreation.get().playListEmbed(textChannel);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks()
                        .stream()
                        .limit(MAX_SIZE)
                        .collect(Collectors.toList());

                if (!tracks.isEmpty()) {
                    for (AudioTrack track : tracks) {
                        guildMusicManager.getTrackScheduler().queue(track);
                    }
                    EmbedCreation.get().playListEmbed(textChannel);
                }
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Не нашел :(").queue();
                logger.warn("noMatches.");
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                logger.warn("Something broke when playing the track." + exception.getMessage());
            }
        });
    }
}