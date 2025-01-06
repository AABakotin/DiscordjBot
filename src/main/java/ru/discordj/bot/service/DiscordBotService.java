package ru.discordj.bot.service;

import net.dv8tion.jda.api.JDA;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import ru.discordj.bot.listener.CommandListener;
import javax.annotation.PostConstruct;

@Service
public class DiscordBotService {
    
    private final JDA jda;
    private final CommandListener commandListener;
    
    @Autowired
    public DiscordBotService(JDA jda, CommandListener commandListener) {
        this.jda = jda;
        this.commandListener = commandListener;
    }
    
    @PostConstruct
    public void init() {
        jda.addEventListener(commandListener);
    }
} 