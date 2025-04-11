package ru.discordj.bot.config;

import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.discordj.bot.monitor.ServerMonitor;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.ServerRules;

import java.util.HashMap;
import java.util.Map;

public class MonitoringManager {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringManager.class);
    private static MonitoringManager instance;
    
    // Карта мониторов для каждой гильдии
    private final Map<String, ServerMonitor> guildMonitors = new HashMap<>();
    
    private final JsonParse jsonHandler = JsonParse.getInstance();

    private MonitoringManager() {}

    public static synchronized MonitoringManager getInstance() {
        if (instance == null) {
            instance = new MonitoringManager();
        }
        return instance;
    }

    /**
     * Запускает мониторинг для указанной гильдии
     * 
     * @param guild Гильдия, для которой запускается мониторинг
     */
    public synchronized void startMonitoring(Guild guild) {
        if (guild == null) {
            logger.warn("Попытка запустить мониторинг для null гильдии");
            return;
        }
        
        String guildId = guild.getId();
        stopMonitoring(guild); // Останавливаем предыдущий монитор если есть
        
        // Загружаем конфигурацию гильдии
        ServerRules root = jsonHandler.read(guild);
        
        // Создаем и запускаем монитор
        ServerMonitor monitor = new ServerMonitor(root);
        monitor.start();
        guildMonitors.put(guildId, monitor);
        
        // Обновляем конфигурацию
        root.setMonitoringEnabled(true);
        jsonHandler.write(guild, root);
        
        logger.info("Мониторинг запущен для гильдии {}", guild.getName());
    }

    /**
     * Останавливает мониторинг для указанной гильдии
     * 
     * @param guild Гильдия, для которой останавливается мониторинг
     */
    public synchronized void stopMonitoring(Guild guild) {
        if (guild == null) {
            logger.warn("Попытка остановить мониторинг для null гильдии");
            return;
        }
        
        String guildId = guild.getId();
        ServerMonitor monitor = guildMonitors.get(guildId);
        
        if (monitor != null) {
            monitor.stop();
            guildMonitors.remove(guildId);
            
            // Обновляем конфигурацию
            ServerRules root = jsonHandler.read(guild);
            root.setMonitoringEnabled(false);
            jsonHandler.write(guild, root);
            
            logger.info("Мониторинг остановлен для гильдии {}", guild.getName());
        }
    }

    /**
     * Проверяет, активен ли мониторинг для указанной гильдии
     * 
     * @param guild Гильдия для проверки
     * @return true, если мониторинг активен
     */
    public synchronized boolean isMonitoringActive(Guild guild) {
        if (guild == null) {
            return false;
        }
        
        ServerMonitor monitor = guildMonitors.get(guild.getId());
        return monitor != null && monitor.isRunning();
    }

    /**
     * Инициализирует мониторинг для всех гильдий
     * 
     * @param guilds Список всех гильдий бота
     */
    public void initForAllGuilds(Iterable<Guild> guilds) {
        if (guilds == null) {
            return;
        }
        
        for (Guild guild : guilds) {
            ServerRules root = jsonHandler.read(guild);
            if (root.isMonitoringEnabled() && 
                root.getMonitoringChannelId() != null && 
                !root.getServers().isEmpty()) {
                
                // Создаем и запускаем монитор
                ServerMonitor monitor = new ServerMonitor(root);
                monitor.start();
                guildMonitors.put(guild.getId(), monitor);
                
                logger.info("Мониторинг автоматически запущен для гильдии {}", guild.getName());
            }
        }
    }
    
    /**
     * Останавливает мониторинг для всех гильдий
     */
    public synchronized void stopAllMonitoring() {
        for (Map.Entry<String, ServerMonitor> entry : guildMonitors.entrySet()) {
            entry.getValue().stop();
            logger.info("Мониторинг остановлен для гильдии с ID {}", entry.getKey());
        }
        guildMonitors.clear();
    }
} 