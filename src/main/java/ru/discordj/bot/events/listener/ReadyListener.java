package ru.discordj.bot.events.listener;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.MonitoringManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadyListener extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ReadyListener.class);
    
    private final MonitoringManager monitoringManager;

    @Autowired
    public ReadyListener(MonitoringManager monitoringManager) {
        this.monitoringManager = monitoringManager;
    }

    @Override
    public void onReady(ReadyEvent event) {
        monitoringManager.init();
        logger.info("Bot is ready!");
    }
} 