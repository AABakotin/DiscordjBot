package ru.discordj.bot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import ru.discordj.bot.utility.IJsonHandler;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@ConfigurationProperties(prefix = "discord")
public class BotConfig {
    private static final Logger logger = LoggerFactory.getLogger(BotConfig.class);
    
    private String token;
    
    @Autowired
    private IJsonHandler jsonHandler;
    
    @PostConstruct
    public void init() {
        logger.info("Initializing BotConfig...");
        if (token == null || token.isEmpty()) {
            logger.info("Token not found in application.yml, trying config.json");
            String configToken = jsonHandler.getToken();
            if (configToken != null) {
                logger.info("Token successfully loaded from config.json");
                token = configToken;
            } else {
                logger.warn("Token not found in config.json");
            }
        } else {
            logger.info("Token successfully loaded from application.yml");
        }
        
        if (token == null || token.isEmpty()) {
            logger.error("Token not found in any configuration source");
        }
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
} 