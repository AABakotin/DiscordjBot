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

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MusicService {
    
    private final AudioPlayerManager playerManager;
    private final JDA jda;
    private final Map<String, GuildMusicManager> musicManagers;
    
    public MusicService(AudioPlayerManager playerManager, JDA jda) {
        this.playerManager = playerManager;
        this.jda = jda;
        this.musicManagers = new HashMap<>();
    }
    
    public void play(String guildId, String channelId, String trackUrl) {
        Guild guild = jda.getGuildById(guildId);
        if (guild == null) return;
        
        GuildMusicManager musicManager = getGuildMusicManager(guild);
        VoiceChannel voiceChannel = guild.getVoiceChannelById(channelId);
        
        if (voiceChannel != null) {
            guild.getAudioManager().openAudioConnection(voiceChannel);
        }
        
        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.scheduler.queue(track);
            }
            
            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    musicManager.scheduler.queue(track);
                }
            }
            
            @Override
            public void noMatches() {
                // Трек не найден
            }
            
            @Override
            public void loadFailed(FriendlyException e) {
                // Ошибка загрузки
            }
        });
    }
    
    public void stop(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            musicManager.scheduler.clearQueue();
            musicManager.player.stopTrack();
            guild.getAudioManager().closeAudioConnection();
        }
    }
    
    public void pause(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            musicManager.player.setPaused(true);
        }
    }
    
    public void resume(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            musicManager.player.setPaused(false);
        }
    }
    
    public void skip(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            musicManager.scheduler.nextTrack();
        }
    }
    
    public String getCurrentTrackInfo(String guildId) {
        Guild guild = jda.getGuildById(guildId);
        if (guild != null) {
            GuildMusicManager musicManager = getGuildMusicManager(guild);
            AudioTrack track = musicManager.player.getPlayingTrack();
            if (track != null) {
                return String.format("Сейчас играет: %s (%s)",
                    track.getInfo().title,
                    formatTime(track.getPosition()) + "/" + formatTime(track.getDuration())
                );
            }
        }
        return "Сейчас ничего не играет";
    }
    
    private String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        String guildId = guild.getId();
        GuildMusicManager musicManager = musicManagers.get(guildId);
        
        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }
        
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());
        return musicManager;
    }
} 