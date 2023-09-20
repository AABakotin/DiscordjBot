package ru.discordj.bot.config;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private final String nonAvatarUrl;
    private final Long guestChannel;
    private final String emojiAccess;
    private final String emojiJava;
    private final String roleAccess;
    private final String roleJavaDevelopment;
    private final String token;

    public Config() {
        try {
            Properties properties = new Properties();
            File file = new File("configuration.properties");
            properties.load(new FileReader(file));
            this.nonAvatarUrl = properties.getProperty("nonAvatarUrl");
            this.guestChannel = Long.valueOf(properties.getProperty("guestChannel"));
            this.emojiAccess = properties.getProperty("emoji.access");
            this.emojiJava = properties.getProperty("emoji.java");
            this.roleAccess = properties.getProperty("role.access");
            this.roleJavaDevelopment = properties.getProperty("role.javaDevelopment");
            this.token = properties.getProperty("token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getNonAvatarUrl() {
        return nonAvatarUrl;
    }

    public Long getGuestChannel() {
        return guestChannel;
    }

    public String getEmojiAccess() {
        return emojiAccess;
    }

    public String getEmojiJava() {
        return emojiJava;
    }

    public String getRoleAccess() {
        return roleAccess;
    }

    public String getRoleJavaDevelopment() {
        return roleJavaDevelopment;
    }

    public String getToken() {
        return token;
    }
}


