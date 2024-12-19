package ru.discordj.bot.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.discordj.bot.monitor.ServerMonitor;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.Root;

public class MonitoringManager {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringManager.class);
    private static MonitoringManager instance;
    private ServerMonitor currentMonitor;
    private final JsonParse jsonHandler = JsonParse.getInstance();

    private MonitoringManager() {}

    public static synchronized MonitoringManager getInstance() {
        if (instance == null) {
            instance = new MonitoringManager();
        }
        return instance;
    }

    public synchronized void startMonitoring(Root root) {
        stopMonitoring(); // Останавливаем предыдущий монитор если есть
        
        currentMonitor = new ServerMonitor(root);
        currentMonitor.start();
        root.setMonitoringEnabled(true);
        jsonHandler.write(root);
        logger.info("Monitoring started");
    }

    public synchronized void stopMonitoring() {
        if (currentMonitor != null) {
            currentMonitor.stop();
            currentMonitor = null;
            Root root = jsonHandler.read();
            root.setMonitoringEnabled(false);
            jsonHandler.write(root);
            logger.info("Monitoring stopped");
        }
    }

    public synchronized boolean isMonitoringActive() {
        return currentMonitor != null && currentMonitor.isRunning();
    }

    public void init() {
        Root root = jsonHandler.read();
        if (root.isMonitoringEnabled() && root.getMonitoringChannelId() != null && !root.getServers().isEmpty()) {
            startMonitoring(root);
            logger.info("Monitoring auto-started");
        }
    }
} 