package ru.discordj.bot.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public abstract class BaseEmbed {
    protected final IJsonHandler jsonHandler;
    protected final Color defaultColor = Color.BLUE;
    
    protected BaseEmbed() {
        this.jsonHandler = JsonParse.getInstance();
    }
    
    protected String formatDate() {
        return ZonedDateTime.now()
            .format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy"));
    }
    
    protected EmbedBuilder createDefaultBuilder() {
        return new EmbedBuilder().setColor(defaultColor);
    }
} 