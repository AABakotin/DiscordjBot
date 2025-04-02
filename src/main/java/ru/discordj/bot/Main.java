package ru.discordj.bot;

import ru.discordj.bot.config.JdaConfig;
import ru.discordj.bot.utility.HttpConfigUtil;

public class Main {
    public static void main(String[] args) {
        // Отключаем логи Apache HTTP Client
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
        
        // Настраиваем HTTP таймауты
        HttpConfigUtil.setupHttpTimeouts();
        HttpConfigUtil.setupYoutubeTimeouts();
        
        JdaConfig.start(args);
    }
}
