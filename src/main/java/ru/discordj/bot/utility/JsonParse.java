package ru.discordj.bot.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.utility.pojo.ServerRules;
import ru.discordj.bot.utility.pojo.RulesMessage;
import ru.discordj.bot.utility.pojo.RadioStation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.List;
import java.util.ArrayList;

public class JsonParse implements IJsonHandler {
    private static final Logger logger = LoggerFactory.getLogger(JsonParse.class);
    private static final String CONFIG_DIR = "config";
    private static final ObjectMapper mapper = new ObjectMapper();

    private static JsonParse instance;

    // Кеш данных по гильдиям для уменьшения обращений к диску
    private final Map<String, ServerRules> guildConfigCache = new HashMap<>();
    
    // Замки для безопасного конкурентного доступа к файлам
    private final Map<String, ReentrantReadWriteLock> guildLocks = new HashMap<>();

    public JsonParse() {
        // Создаем директорию для конфигурации, если ее нет
        File configDir = new File(CONFIG_DIR);
        if (!configDir.exists()) {
            if (configDir.mkdir()) {
                logger.info("Created configuration directory: {}", CONFIG_DIR);
            } else {
                logger.error("Failed to create configuration directory: {}", CONFIG_DIR);
            }
        }
    }

    public static synchronized JsonParse getInstance() {
        if (instance == null) {
            instance = new JsonParse();
        }
        return instance;
    }

    @Override
    public RulesMessage readRules() {
        // Для глобальных правил используем правила из глобальной конфигурации
        ServerRules globalConfig = read();
        if (globalConfig.getRules() == null) {
            globalConfig.setRules(new RulesMessage());
            write(globalConfig);
        }
        return globalConfig.getRules();
    }

    @Override
    public void writeRules(RulesMessage rules) {
        // Для глобальных правил сохраняем в глобальную конфигурацию
        ServerRules globalConfig = read();
        globalConfig.setRules(rules);
        write(globalConfig);
        logger.info("Глобальные правила успешно сохранены");
    }

    /**
     * Получение корневого объекта конфигурации для конкретной гильдии
     * 
     * @param guild Объект гильдии
     * @return Корневой объект конфигурации
     */
    public ServerRules read(Guild guild) {
        if (guild == null) {
            return read(); // Возвращаем глобальную конфигурацию
        }
        
        String guildId = guild.getId();
        
        // Получаем или создаем замок для гильдии
        ReentrantReadWriteLock lock = guildLocks.computeIfAbsent(
            guildId, k -> new ReentrantReadWriteLock());
        
        // Блокируем на чтение
        lock.readLock().lock();
        try {
            // Проверяем кеш
            if (guildConfigCache.containsKey(guildId)) {
                return guildConfigCache.get(guildId);
            }
        } finally {
            lock.readLock().unlock();
        }
        
        // Если в кеше нет, блокируем на запись и загружаем
        lock.writeLock().lock();
        try {
            // Проверяем еще раз (возможно, другой поток уже загрузил)
            if (guildConfigCache.containsKey(guildId)) {
                return guildConfigCache.get(guildId);
            }
            
            // Загружаем конфигурацию гильдии
            ServerRules config = loadGuildConfig(guildId);
            guildConfigCache.put(guildId, config);
            return config;
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public ServerRules read() {
        // Возвращаем пустую конфигурацию, так как глобальный config.json больше не используется
        return new ServerRules();
    }

    /**
     * Сохранение конфигурации для конкретной гильдии
     * 
     * @param guild Объект гильдии
     * @param root Объект конфигурации для сохранения
     */
    public void write(Guild guild, ServerRules root) {
        if (guild == null) {
            write(root); // Сохраняем глобальную конфигурацию
            return;
        }
        
        String guildId = guild.getId();
        
        // Получаем или создаем замок для гильдии
        ReentrantReadWriteLock lock = guildLocks.computeIfAbsent(
            guildId, k -> new ReentrantReadWriteLock());
        
        lock.writeLock().lock();
        try {
            // Обновляем кеш
            guildConfigCache.put(guildId, root);
            
            // Преобразуем имя гильдии в безопасное имя файла
            String safeFileName = getSafeFileName(guild.getName());
            
            // Сохраняем в файл
            File configFile = new File(CONFIG_DIR, safeFileName + ".json");
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, root);
                logger.info("Guild configuration {} saved to file {}", guild.getName(), configFile.getName());
            } catch (IOException e) {
                logger.error("Error saving guild configuration {}: {}", guild.getName(), e.getMessage());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void write(ServerRules root) {
        // Глобальная конфигурация больше не сохраняется в файл
        logger.info("Global configuration updated in memory");
    }
    
    /**
     * Загружает конфигурацию для конкретной гильдии
     * 
     * @param guildId ID гильдии
     * @return Конфигурация гильдии
     */
    private ServerRules loadGuildConfig(String guildId) {
        Guild guild = null;
        try {
            guild = ru.discordj.bot.config.JdaConfig.getJda().getGuildById(guildId);
        } catch (Exception e) {
            logger.error("Failed to get guild with ID {}: {}", guildId, e.getMessage());
        }
        
        if (guild == null) {
            File configFile = new File(CONFIG_DIR, "guild_" + guildId + ".json");
            
            if (configFile.exists()) {
                try {
                    ServerRules config = mapper.readValue(configFile, ServerRules.class);
                    logger.info("Loaded configuration for guild with ID {}", guildId);
                    return config;
                } catch (IOException e) {
                    logger.error("Error reading configuration for guild with ID {}: {}", guildId, e.getMessage());
                }
            }
            
            logger.info("Creating new configuration for guild with ID {}", guildId);
            ServerRules newConfig = new ServerRules();
            
            try {
                mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, newConfig);
                logger.info("Created new configuration file for guild with ID {} with {} radio stations", 
                    guildId, newConfig.getRadioStations().size());
            } catch (IOException e) {
                logger.error("Error creating configuration for guild with ID {}: {}", guildId, e.getMessage());
            }
            
            return newConfig;
        }
        
        String safeFileName = getSafeFileName(guild.getName());
        File configFile = new File(CONFIG_DIR, safeFileName + ".json");
        
        File oldConfigFile = new File(CONFIG_DIR, "guild_" + guildId + ".json");
        if (oldConfigFile.exists() && !configFile.exists()) {
            if (oldConfigFile.renameTo(configFile)) {
                logger.info("Configuration file for guild {} renamed to new format", guild.getName());
            } else {
                logger.warn("Failed to rename configuration file for guild {}", guild.getName());
            }
        }
        
        if (configFile.exists()) {
            try {
                ServerRules config = mapper.readValue(configFile, ServerRules.class);
                logger.info("Loaded configuration for guild {} with {} radio stations", 
                    guild.getName(), config.getRadioStations().size());
                return config;
            } catch (IOException e) {
                logger.error("Error reading configuration for guild {}: {}", guild.getName(), e.getMessage());
            }
        }
        
        logger.info("Creating new configuration for guild {}", guild.getName());
        ServerRules newConfig = new ServerRules();
        
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, newConfig);
            logger.info("Created new configuration file for guild {} with {} radio stations", 
                guild.getName(), newConfig.getRadioStations().size());
        } catch (IOException e) {
            logger.error("Error creating configuration for guild {}: {}", guild.getName(), e.getMessage());
        }
        
        return newConfig;
    }
    
    /**
     * Преобразует имя гильдии в безопасное имя файла
     * 
     * @param guildName Имя гильдии
     * @return Безопасное имя для файла
     */
    private String getSafeFileName(String guildName) {
        // Заменяем недопустимые символы на подчеркивание
        return guildName.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
    }
    
    /**
     * Очищает кеш для указанной гильдии
     * 
     * @param guild Объект гильдии
     */
    public void clearCache(Guild guild) {
        if (guild == null) {
            return;
        }
        
        String guildId = guild.getId();
        ReentrantReadWriteLock lock = guildLocks.get(guildId);
        
        if (lock != null) {
            lock.writeLock().lock();
            try {
                guildConfigCache.remove(guildId);
                logger.debug("Cache cleared for guild {}", guildId);
            } finally {
                lock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Очищает весь кеш
     */
    public void clearAllCache() {
        // Блокируем все замки для записи
        for (ReentrantReadWriteLock lock : guildLocks.values()) {
            lock.writeLock().lock();
        }
        
        try {
            guildConfigCache.clear();
            logger.debug("All configuration cache cleared");
        } finally {
            // Разблокируем все замки
            for (ReentrantReadWriteLock lock : guildLocks.values()) {
                lock.writeLock().unlock();
            }
        }
    }

    @Override
    public RulesMessage readRules(Guild guild) {
        if (guild == null) {
            return readRules();
        }
        
        ServerRules config = read(guild);
        if (config.getRules() == null) {
            config.setRules(new RulesMessage());
            write(guild, config);
        }
        return config.getRules();
    }

    @Override
    public void writeRules(Guild guild, RulesMessage rules) {
        if (guild == null) {
            writeRules(rules);
            return;
        }
        
        ServerRules config = read(guild);
        config.setRules(rules);
        write(guild, config);
        logger.info("Правила для сервера {} успешно сохранены", guild.getName());
    }

    /**
     * Метод для обновления списка радиостанций из конфигурации по умолчанию.
     * Удаляет старые радиостанции и добавляет новые из конфигурации по умолчанию.
     * 
     * @param guild Объект гильдии Discord для обновления радиостанций
     * @return Обновленный список радиостанций
     */
    public List<RadioStation> reloadRadioStations(Guild guild) {
        if (guild == null) {
            return null;
        }
        
        // Создаем новую конфигурацию с радиостанциями по умолчанию
        ServerRules defaultConfig = new ServerRules();
        List<RadioStation> defaultStations = defaultConfig.getRadioStations();
        
        // Получаем текущую конфигурацию гильдии
        ServerRules guildConfig = read(guild);
        
        // Обновляем список радиостанций
        guildConfig.setRadioStations(new ArrayList<>(defaultStations));
        
        // Сохраняем обновленную конфигурацию
        write(guild, guildConfig);
        
        logger.info("Список радиостанций для сервера {} обновлен. Добавлено {} станций.", 
            guild.getName(), defaultStations.size());
        
        return guildConfig.getRadioStations();
    }
} 