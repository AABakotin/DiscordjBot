package ru.discordj.bot.embed;

public abstract class EmbedFactory {
    private static final EmbedFactory instance = new DefaultEmbedFactory();
    
    public static EmbedFactory getInstance() {
        return instance;
    }
    
    public abstract MusicEmbed createMusicEmbed();
    public abstract WelcomeEmbed createWelcomeEmbed();
    public abstract ServerStatusEmbed createServerStatusEmbed();
    public abstract ConfigEmbed createConfigEmbed();
}

class DefaultEmbedFactory extends EmbedFactory {
    private final MusicEmbed musicEmbed = new MusicEmbed();
    private final WelcomeEmbed welcomeEmbed = new WelcomeEmbed();
    private final ServerStatusEmbed serverStatusEmbed = new ServerStatusEmbed();
    private ConfigEmbed configEmbed;
    
    @Override
    public MusicEmbed createMusicEmbed() {
        return musicEmbed;
    }
    
    @Override
    public WelcomeEmbed createWelcomeEmbed() {
        return welcomeEmbed;
    }
    
    @Override
    public ServerStatusEmbed createServerStatusEmbed() {
        return serverStatusEmbed;
    }
    
    @Override
    public ConfigEmbed createConfigEmbed() {
        if (configEmbed == null) {
            configEmbed = new ConfigEmbed();
        }
        return configEmbed;
    }
} 