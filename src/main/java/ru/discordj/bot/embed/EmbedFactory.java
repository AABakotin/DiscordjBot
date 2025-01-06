package ru.discordj.bot.embed;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

@Component
public class EmbedFactory {
    @Autowired
    private MusicEmbed musicEmbed;
    
    @Autowired
    private WelcomeEmbed welcomeEmbed;
    
    @Autowired
    private ConfigEmbed configEmbed;
    
    @Autowired
    private ServerStatusEmbed serverStatusEmbed;

    public MusicEmbed createMusicEmbed() {
        return musicEmbed;
    }

    public WelcomeEmbed createWelcomeEmbed() {
        return welcomeEmbed;
    }

    public ConfigEmbed createConfigEmbed() {
        return configEmbed;
    }

    public ServerStatusEmbed createServerStatusEmbed() {
        return serverStatusEmbed;
    }
} 