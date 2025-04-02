package ru.discordj.bot.utility;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилита для настройки HTTP клиентов с увеличенными таймаутами.
 * Особенно полезна для работы с YouTube API, которое может требовать больше времени для ответа.
 */
public class HttpConfigUtil {
    
    private static final Logger log = LoggerFactory.getLogger(HttpConfigUtil.class);
    
    /**
     * Настраивает системные свойства для HTTP клиентов
     */
    public static void setupHttpTimeouts() {
        System.setProperty("http.connection.timeout", "30000");  // 30 секунд
        System.setProperty("http.socket.timeout", "30000");      // 30 секунд
        System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
        System.setProperty("sun.net.client.defaultReadTimeout", "30000");
        log.info("HTTP таймауты настроены: 30 секунд");
    }
    
    /**
     * Пытается настроить YouTube таймауты через рефлексию
     */
    public static void setupYoutubeTimeouts() {
        try {
            // Пробуем загрузить класс dev.lavalink.youtube.YoutubeHttpManager
            Class<?> httpManagerClass = Class.forName("dev.lavalink.youtube.YoutubeHttpManager");
            
            // Пытаемся получить метод configureBuilder
            // Сначала получаем все поля класса
            for (Field field : httpManagerClass.getDeclaredFields()) {
                if (field.getType().equals(HttpClientBuilder.class)) {
                    // Если нашли поле с типом HttpClientBuilder, делаем его доступным
                    field.setAccessible(true);
                    
                    // Создаем новую конфигурацию с увеличенными таймаутами
                    RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(30000)          // 30 секунд вместо 3
                        .setConnectionRequestTimeout(30000) // 30 секунд
                        .setSocketTimeout(30000)           // 30 секунд
                        .build();
                    
                    // Пытаемся найти экземпляр HttpClientBuilder
                    HttpClientBuilder builder = (HttpClientBuilder) field.get(null);
                    if (builder != null) {
                        builder.setDefaultRequestConfig(config);
                        log.info("YouTube HTTP клиент настроен с увеличенными таймаутами");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Не удалось настроить YouTube HTTP клиент: {}", e.getMessage());
        }
    }
} 