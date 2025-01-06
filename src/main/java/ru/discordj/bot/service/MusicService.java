package ru.discordj.bot.service;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import ru.discordj.bot.audio.GuildMusicManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Сервис для управления музыкальным плеером.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MusicService {
    private static final String LOG_TRACK_LOADED = "Загружен трек: {}";
    private static final String LOG_PLAYLIST_LOADED = "Загружен плейлист: {} треков";
    private static final String LOG_NO_MATCHES = "Трек не найден: {}";
    private static final String LOG_LOAD_FAILED = "Ошибка загрузки трека: {}";
    private static final String LOG_TRACK_INFO = "Сейчас играет: {} ({}/{})";
    private static final String NO_TRACK_PLAYING = "Сейчас ничего не играет";

    private final AudioPlayerManager playerManager;
    private final JDA jda;
    private final Map<String, GuildMusicManager> musicManagers = new ConcurrentHashMap<>();

    /**
     * Воспроизводит трек или плейлист
     */
    public void play(String guildId, String channelId, String trackUrl) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) {
            log.warn("Сервер не найден: {}", guildId);
            return;
        }

        GuildMusicManager musicManager = getGuildMusicManager(guildId);
        connectToVoiceChannel(guild, channelId);
        loadAndPlay(musicManager, trackUrl);
    }

    /**
     * Подключается к голосовому каналу
     */
    private void connectToVoiceChannel(Guild guild, String channelId) {
        VoiceChannel voiceChannel = guild.getVoiceChannelById(channelId);
        if (voiceChannel != null) {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        } else {
            log.warn("Голосовой канал не найден: {}", channelId);
        }
    }

    /**
     * Загружает и воспроизводит трек
     */
    private void loadAndPlay(GuildMusicManager musicManager, String trackUrl) {
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                log.info(LOG_TRACK_LOADED, track.getInfo().title);
                musicManager.scheduler.queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                log.info(LOG_PLAYLIST_LOADED, playlist.getTracks().size());
                playlist.getTracks().forEach(musicManager.scheduler::queue);
            }

            @Override
            public void noMatches() {
                log.warn(LOG_NO_MATCHES, trackUrl);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                log.error(LOG_LOAD_FAILED, e.getMessage());
            }
        });
    }

    /**
     * Управление воспроизведением
     */
    public void executeGuildAction(String guildId, Consumer<GuildMusicManager> action) {
        GuildMusicManager musicManager = getGuildMusicManager(guildId);
        action.accept(musicManager);
    }

    public void stop(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            executeGuildAction(guildId, manager -> {
                manager.scheduler.clearQueue();
                manager.player.stopTrack();
                guild.getAudioManager().closeAudioConnection();
            });
        }
    }

    public void pause(String guildId) {
        executeGuildAction(guildId, manager -> manager.player.setPaused(true));
    }

    public void resume(String guildId) {
        executeGuildAction(guildId, manager -> manager.player.setPaused(false));
    }

    public void skip(String guildId) {
        executeGuildAction(guildId, manager -> manager.scheduler.nextTrack());
    }

    public boolean isPaused(String guildId) {
        return getGuildMusicManager(guildId).scheduler.isPaused();
    }

    public void pauseTrack(String guildId) {
        executeGuildAction(guildId, manager -> manager.scheduler.togglePause());
    }

    public void resumeTrack(String guildId) {
        executeGuildAction(guildId, manager -> manager.scheduler.togglePause());
    }

    public void stopTrack(String guildId) {
        executeGuildAction(guildId, manager -> manager.scheduler.stop());
    }

    public void skipTrack(String guildId) {
        executeGuildAction(guildId, manager -> manager.scheduler.nextTrack());
    }

    public void toggleRepeat(String guildId) {
        executeGuildAction(guildId, manager -> manager.scheduler.toggleRepeat());
    }

    /**
     * Получает информацию о текущем треке
     */
    public String getCurrentTrackInfo(String guildId) {
        GuildMusicManager musicManager = getGuildMusicManager(guildId);
        AudioTrack track = musicManager.player.getPlayingTrack();
        
        if (track != null) {
            return String.format(LOG_TRACK_INFO,
                track.getInfo().title,
                formatTime(track.getPosition()),
                formatTime(track.getDuration())
            );
        }
        return NO_TRACK_PLAYING;
    }

    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    private GuildMusicManager getGuildMusicManager(String guildId) {
        return musicManagers.computeIfAbsent(guildId, 
            id -> new GuildMusicManager(playerManager));
    }
} 