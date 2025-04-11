package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.MonitoringManager;

public class ReadyListener implements EventListener {
    private static final Logger logger = LoggerFactory.getLogger(ReadyListener.class);

    @Override
    public void onEvent(GenericEvent event) {
        if (event instanceof ReadyEvent) {
            onReady((ReadyEvent) event);
        }
    }

    private void onReady(ReadyEvent event) {
        MonitoringManager.getInstance().initForAllGuilds(event.getJDA().getGuilds());
        logger.info("Bot is ready!");
    }
} 