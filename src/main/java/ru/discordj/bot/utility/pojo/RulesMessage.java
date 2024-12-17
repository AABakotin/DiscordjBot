package ru.discordj.bot.utility.pojo;

public class RulesMessage {
    private String serverName;
    private String welcomeField;
    private String rulesField;
    private String footer;

    // Геттеры и сеттеры
    public String getserverName() { return serverName; }
    public void setserverName(String serverName) { this.serverName = serverName; }
    public String getWelcomeField() { return welcomeField; }
    public void setWelcomeField(String welcomeField) { this.welcomeField = welcomeField; }
    public String getRulesField() { return rulesField; }
    public void setRulesField(String rulesField) { this.rulesField = rulesField; }
    public String getFooter() { return footer; }
    public void setFooter(String footer) { this.footer = footer; }
} 