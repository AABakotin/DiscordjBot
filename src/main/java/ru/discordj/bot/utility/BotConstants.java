package ru.discordj.bot.utility;

/**
 * Константы для бота Discord
 */
public final class BotConstants {
    // Информация о боте
    public static final String BOT_NAME = "DiscordJ Bot";
    public static final String BOT_VERSION = "1.0.0";
    public static final String BOT_AUTHOR = "Ваше имя";
    public static final String BOT_DESCRIPTION = "Многофункциональный бот для Discord";
    
    // Ссылки
    public static final String GITHUB_REPO = "https://github.com/your/repo";
    public static final String GITHUB_LINK = "[Репозиторий проекта](" + GITHUB_REPO + ")";
    public static final String INVITE_LINK = "https://discord.com/oauth2/authorize?..."; // Ваша ссылка для приглашения

    // Цвета
    public static final int EMBED_COLOR_HEX = 0x2F3136;

    private BotConstants() {
        // Запрещаем создание экземпляров
    }
} 