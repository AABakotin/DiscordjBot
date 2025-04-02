package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

/**
 * Класс для настройки YouTube API с увеличенными таймаутами и обходом ограничений
 */
public class YoutubeConfig {
    private static final Logger log = LoggerFactory.getLogger(YoutubeConfig.class);
    
    /**
     * Настраивает YouTube SourceManager с увеличенными таймаутами
     * @param youtubeManager менеджер YouTube для настройки
     * @return настроенный менеджер
     */
    public static YoutubeAudioSourceManager configure(YoutubeAudioSourceManager youtubeManager) {
        try {
            // Пытаемся настроить таймауты через reflection API
            configureTimeouts(youtubeManager);
            
            // Можно добавить данные для аутентификации, чтобы обойти возрастные ограничения
            // youtubeManager.setEmailAndPassword("your-email@example.com", "your-password");
            
            log.info("YouTube настроен успешно с увеличенными таймаутами");
        } catch (Exception e) {
            log.warn("Не удалось настроить YouTube: {}", e.getMessage());
        }
        
        return youtubeManager;
    }
    
    /**
     * Настраивает HTTP клиент для YouTube с увеличенными таймаутами
     */
    private static void configureTimeouts(YoutubeAudioSourceManager manager) throws Exception {
        // Системные настройки таймаутов
        System.setProperty("http.connection.timeout", "30000");
        System.setProperty("http.socket.timeout", "30000");
        System.setProperty("jdk.httpclient.connectionTimeout", "30000");
        System.setProperty("jdk.httpclient.keepalive.timeout", "30000");
        
        // Настройка напрямую через reflection API
        try {
            // Находим приватное поле httpInterfaceManager
            Field httpInterfaceManagerField = manager.getClass().getDeclaredField("httpInterfaceManager");
            httpInterfaceManagerField.setAccessible(true);
            Object httpInterfaceManager = httpInterfaceManagerField.get(manager);
            
            // Находим поле configBuilder
            Field configBuilderField = httpInterfaceManager.getClass().getDeclaredField("configBuilder");
            configBuilderField.setAccessible(true);
            RequestConfig.Builder configBuilder = (RequestConfig.Builder) configBuilderField.get(httpInterfaceManager);
            
            // Настраиваем таймауты
            configBuilder
                .setConnectTimeout(30000)          // 30 секунд
                .setSocketTimeout(30000)           // 30 секунд
                .setConnectionRequestTimeout(30000); // 30 секунд
            
            // Находим поле httpClientBuilder
            Field httpClientBuilderField = httpInterfaceManager.getClass().getDeclaredField("httpClientBuilder");
            httpClientBuilderField.setAccessible(true);
            HttpClientBuilder httpClientBuilder = (HttpClientBuilder) httpClientBuilderField.get(httpInterfaceManager);
            
            // Создаем новую конфигурацию
            RequestConfig config = configBuilder.build();
            httpClientBuilder.setDefaultRequestConfig(config);
            
            log.info("HTTP client для YouTube настроен: connectTimeout={}, socketTimeout={}", 
                config.getConnectTimeout(), config.getSocketTimeout());
        } catch (Exception e) {
            throw new Exception("Не удалось настроить HTTP клиент: " + e.getMessage(), e);
        }
    }
} 