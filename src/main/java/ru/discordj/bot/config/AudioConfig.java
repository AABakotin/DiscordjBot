package ru.discordj.bot.config;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AudioConfig {
    
    @Bean
    public AudioPlayerManager audioPlayerManager() {
        return new DefaultAudioPlayerManager();
    }
    
    @Bean
    public AudioPlayer audioPlayer(AudioPlayerManager playerManager) {
        return playerManager.createPlayer();
    }
} 