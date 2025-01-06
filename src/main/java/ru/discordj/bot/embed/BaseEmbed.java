package ru.discordj.bot.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import ru.discordj.bot.utility.IJsonHandler;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseEmbed {
    protected final IJsonHandler jsonHandler;
    protected final Color defaultColor = Color.BLUE;
    
    @Autowired
    protected BaseEmbed(IJsonHandler jsonHandler) {
        this.jsonHandler = jsonHandler;
    }
    
    protected String formatDate() {
        return ZonedDateTime.now()
            .format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy"));
    }
    
    protected EmbedBuilder createDefaultBuilder() {
        return new EmbedBuilder().setColor(defaultColor);
    }
} 