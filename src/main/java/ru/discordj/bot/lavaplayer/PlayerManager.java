package ru.discordj.bot.lavaplayer;


import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import dev.lavalink.youtube.YoutubeAudioSourceManager;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.discordj.bot.embed.EmbedFactory;
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {


    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager youtube = new dev.lavalink.youtube.YoutubeAudioSourceManager();
        audioPlayerManager.registerSourceManager(youtube);
        AudioSourceManagers.registerRemoteSources(this.audioPlayerManager);
        AudioSourceManagers.registerLocalSource(this.audioPlayerManager);
    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }


    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }


    public void play(TextChannel textChannel, String trackUrl) {
        final GuildMusicManager musicManager = getGuildMusicManager(textChannel.getGuild());
        
        // Создаем сообщение плеера, если его еще нет
        if (musicManager.getTrackScheduler().getPlayerMessageId() == null) {
            textChannel.sendMessage(EmbedFactory.getInstance().createMusicEmbed()
                .createPlayerMessage(textChannel).build())
                .queue(message -> 
                    musicManager.getTrackScheduler().setPlayerMessage(textChannel, message.getId())
                );
        }

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Берем только первый трек из плейлиста
                AudioTrack track = playlist.getTracks().get(0);
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Трек не найден: " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                textChannel.sendMessage("Ошибка загрузки: " + exception.getMessage()).queue();
            }
        });
    }

    public void searchAndPlay(TextChannel textChannel, String searchQuery) {
        final GuildMusicManager musicManager = getGuildMusicManager(textChannel.getGuild());

        audioPlayerManager.loadItemOrdered(musicManager, "ytsearch:" + searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack track = playlist.getTracks().get(0);
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Ничего не найдено по запросу: " + searchQuery).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                textChannel.sendMessage("Ошибка при загрузке: " + exception.getMessage()).queue();
            }
        });
    }
}