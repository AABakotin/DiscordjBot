package ru.discordj.bot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.IJsonHandler;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class RootConfig {
    
    @Autowired
    private IJsonHandler jsonHandler;
    
    @Bean
    public Root root() {
        return jsonHandler.read();
    }
} 