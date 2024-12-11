package ru.discordj.bot.config.utility.pojo;

public class Roles {

    private String channel;
    private String role;
    private String emoji;

    public Roles() {
    }

    public Roles(String channel, String role, String emoji) {
        this.channel = channel;
        this.role = role;
        this.emoji = emoji;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    @Override
    public String toString() {
        return "Roles{" +
                "channel='" + channel + '\'' +
                ", role='" + role + '\'' +
                ", emoji='" + emoji + '\'' +
                '}';
    }
}
