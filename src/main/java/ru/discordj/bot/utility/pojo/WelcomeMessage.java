package ru.discordj.bot.utility.pojo;

import java.util.List;

public class WelcomeMessage {
    private String title;
    private String description;
    private List<EmbedField> fields;
    private String footer;

    // Геттеры
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<EmbedField> getFields() { return fields; }
    public String getFooter() { return footer; }

    // Сеттеры
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setFields(List<EmbedField> fields) { this.fields = fields; }
    public void setFooter(String footer) { this.footer = footer; }

    public static class EmbedField {
        private String name;
        private String value;
        private boolean inline;

        public String getName() { return name; }
        public String getValue() { return value; }
        public boolean isInline() { return inline; }

        public void setName(String name) { this.name = name; }
        public void setValue(String value) { this.value = value; }
        public void setInline(boolean inline) { this.inline = inline; }
    }
} 