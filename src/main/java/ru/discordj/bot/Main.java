package ru.discordj.bot;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import org.slf4j.bridge.SLF4JBridgeHandler;
import ru.discordj.bot.config.JdaConfig;
import ru.discordj.bot.utility.JsonParse;

/**
 * Главный класс приложения, инициализирующий всё необходимое для работы бота.
 */
public class Main {
    
    /**
     * Метод запуска приложения. Инициализирует и запускает бота Discord.
     * 
     * @param args аргументы командной строки
     * @throws Exception если произошла ошибка во время инициализации
     */
    public static void main(String[] args) {
        // Запуск автоапдейтера для домашних серверов (раз в сутки, версия из MANIFEST.MF)
        ru.discordj.bot.utility.Updater.startAutoUpdate();
        // Загружаем настройки логирования
        configureLogging();
        
        // Инициализируем JsonParse
        JsonParse.getInstance();
        
        printBanner();
        // Запускаем бота с использованием существующей конфигурации
        JdaConfig.start(args);
    }
    
    /**
     * Настраивает логирование для перенаправления всех логов через SLF4J
     */
    private static void configureLogging() {
        try {
            // Загружаем конфигурацию из файла properties
            InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("logging.properties");
            if (inputStream != null) {
                try {
                    LogManager.getLogManager().readConfiguration(inputStream);
                } finally {
                    inputStream.close();
                }
            } else {
                System.err.println("Файл настроек логирования не найден");
            }
            
            // Устанавливаем мост между JUL и SLF4J
            // Удаляем существующие хендлеры у корневого логгера
            SLF4JBridgeHandler.removeHandlersForRootLogger();
            
            // Устанавливаем мост между JUL и SLF4J
            SLF4JBridgeHandler.install();
            
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке настроек логирования: " + e.getMessage());
        }
    }

    private static void printBanner() {
        String version = ru.discordj.bot.utility.Updater.getCurrentVersionFromManifest();
        System.out.println("\n" +
        "    ____  _                          __    ______        __ \n" +
        "   / __ \\(_)_____________  _________/ /   / / __ )____  / /_\n" +
        "  / / / / / ___/ ___/ __ \\/ ___/ __  /_  / / __  / __ \\/ __/\n" +
        " / /_/ / (__  ) /__/ /_/ / /  / /_/ / /_/ / /_/ / /_/ / /_\n" +
        "/_____/_/____/\\___/\\____/_/   \\__,_/\\____/_____/\\____/\\__/\n" +
        "                                                                  \n" +
                "\n" +
                "\t\t" + (version != null ? "v" + version : "(версия не определена)") + "\n");
    }
}
