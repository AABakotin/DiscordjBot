package ru.discordj.bot.embed;

import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.discordj.bot.utility.IJsonHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.reflect.Constructor;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmbedFactory {
    private final IJsonHandler jsonHandler;
    
    // Используем Supplier для ленивой инициализации
    private final Map<Class<? extends BaseEmbed>, BaseEmbed> embedCache = new ConcurrentHashMap<>();

    /**
     * Создает или возвращает существующий экземпляр MusicEmbed
     */
    public MusicEmbed createMusicEmbed() {
        return getOrCreateEmbed(MusicEmbed.class);
    }

    /**
     * Создает или возвращает существующий экземпляр ConfigEmbed
     */
    public ConfigEmbed createConfigEmbed() {
        return getOrCreateEmbed(ConfigEmbed.class);
    }

    /**
     * Создает или возвращает существующий экземпляр WelcomeEmbed
     */
    public WelcomeEmbed createWelcomeEmbed() {
        return getOrCreateEmbed(WelcomeEmbed.class);
    }

    /**
     * Создает или возвращает существующий экземпляр ServerStatusEmbed
     */
    public ServerStatusEmbed createServerStatusEmbed() {
        return getOrCreateEmbed(ServerStatusEmbed.class);
    }

    /**
     * Обобщенный метод для создания или получения существующего экземпляра embed
     */
    @SuppressWarnings("unchecked")
    private <T extends BaseEmbed> T getOrCreateEmbed(Class<T> embedClass) {
        return (T) embedCache.computeIfAbsent(embedClass, this::createEmbed);
    }

    /**
     * Создает новый экземпляр embed класса
     */
    private BaseEmbed createEmbed(Class<? extends BaseEmbed> embedClass) {
        try {
            Constructor<?> constructor = embedClass.getConstructor(IJsonHandler.class);
            return (BaseEmbed) constructor.newInstance(jsonHandler);
        } catch (Exception e) {
            log.error("Failed to create embed instance for class: {}", embedClass.getSimpleName(), e);
            throw new RuntimeException("Failed to create embed instance", e);
        }
    }
} 