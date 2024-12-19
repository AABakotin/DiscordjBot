package ru.discordj.bot.embed;

public abstract class EmbedFactory {
    private static final EmbedFactory instance = new DefaultEmbedFactory();
    
    public static EmbedFactory getInstance() {
        return instance;
    }
    
    public abstract MusicEmbed createMusicEmbed();
    public abstract WelcomeEmbed createWelcomeEmbed();
    public abstract ConfigEmbed createConfigEmbed();
    public abstract ServerStatusEmbed createServerStatusEmbed();
}

class DefaultEmbedFactory extends EmbedFactory {
    private final MusicEmbed musicEmbed = new MusicEmbed();
    private final WelcomeEmbed welcomeEmbed = new WelcomeEmbed();
    private final ConfigEmbed configEmbed = new ConfigEmbed();
    private final ServerStatusEmbed serverStatusEmbed = new ServerStatusEmbed();
    
    @Override
    public MusicEmbed createMusicEmbed() {
        return musicEmbed;
    }
    
    @Override
    public WelcomeEmbed createWelcomeEmbed() {
        return welcomeEmbed;
    }
    
    @Override
    public ConfigEmbed createConfigEmbed() {
        return configEmbed;
    }
    
    @Override
    public ServerStatusEmbed createServerStatusEmbed() {
        return serverStatusEmbed;
    }
} 