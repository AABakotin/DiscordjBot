package ru.discordj.bot.events.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.discordj.bot.config.embed.EmbedCreation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerManager {

    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private final long MAX_SIZE = 5L;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

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
        this.audioPlayerManager.loadItemOrdered(guildMusicManager, trackURL, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track);
                textChannel.sendMessageEmbeds(EmbedCreation.embedMusic(track.getInfo())).queue();
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
                    textChannel.sendMessageEmbeds(EmbedCreation.embedMusic(tracks)).queue();
                }
            }

            @Override
            public void noMatches() {
//                textChannel.sendMessage("Not found. Repeat later.").queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
//                textChannel.sendMessage("The link is corrupted. Repeat later.").queue();
            }

        });
    }
}