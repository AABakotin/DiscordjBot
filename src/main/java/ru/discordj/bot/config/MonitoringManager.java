package ru.discordj.bot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.discordj.bot.monitor.ServerMonitor;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Root;

@Component
public class MonitoringManager {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringManager.class);
    
    private final IJsonHandler jsonHandler;
    private final ServerMonitor serverMonitor;

    @Autowired
    public MonitoringManager(IJsonHandler jsonHandler, ServerMonitor serverMonitor) {
        this.jsonHandler = jsonHandler;
        this.serverMonitor = serverMonitor;
    }

    public void startMonitoring(Root root) {
        stopMonitoring();
        serverMonitor.updateConfig(root);
        serverMonitor.start();
        root.setMonitoringEnabled(true);
        jsonHandler.write(root);
        logger.info("Monitoring started");
    }

    public void stopMonitoring() {
        serverMonitor.stop();
        Root root = jsonHandler.read();
        root.setMonitoringEnabled(false);
        jsonHandler.write(root);
        logger.info("Monitoring stopped");
    }

    public boolean isMonitoringActive() {
        return serverMonitor.isRunning();
    }

    public void init() {
        Root root = jsonHandler.read();
        if (root.isMonitoringEnabled() && root.getMonitoringChannelId() != null && !root.getServers().isEmpty()) {
            startMonitoring(root);
            logger.info("Monitoring auto-started");
        }
    }
} 