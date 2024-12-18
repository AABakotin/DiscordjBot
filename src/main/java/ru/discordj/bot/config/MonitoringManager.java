package ru.discordj.bot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.listener.configurator.command.MonitoringCommand;

public class MonitoringManager {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringManager.class);
    private static MonitoringManager instance;
    private final MonitoringCommand monitoringCommand;

    private MonitoringManager() {
        this.monitoringCommand = new MonitoringCommand();
    }

    public static MonitoringManager getInstance() {
        if (instance == null) {
            instance = new MonitoringManager();
        }
        return instance;
    }

    public void init() {
        monitoringCommand.initMonitoring();
        logger.info("Monitoring system initialized");
    }
} 