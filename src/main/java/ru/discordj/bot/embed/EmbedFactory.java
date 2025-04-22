package ru.discordj.bot.embed;

public class EmbedFactory {
    public static MusicEmbed createMusicEmbed() {
        return new MusicEmbed();
    }
    
    public static WelcomeEmbed createWelcomeEmbed() {
        return new WelcomeEmbed();
    }
    
    public static ServerStatusEmbed createServerStatusEmbed() {
        return new ServerStatusEmbed();
    }
} 