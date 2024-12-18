package ru.discordj.bot.utility.pojo;

public class RulesMessage {
    private String title;
    private String welcomeField;
    private String rulesField;
    private String footer;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getWelcomeField() { return welcomeField; }
    public void setWelcomeField(String welcomeField) { this.welcomeField = welcomeField; }
    public String getRulesField() { return rulesField; }
    public void setRulesField(String rulesField) { this.rulesField = rulesField; }
    public String getFooter() { return footer; }
    public void setFooter(String footer) { this.footer = footer; }
} 