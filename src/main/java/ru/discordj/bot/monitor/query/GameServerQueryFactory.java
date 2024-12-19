package ru.discordj.bot.monitor.query;

public class GameServerQueryFactory {
    private static final SourceQuery sourceQuery = new SourceQuery();
    
    public static GameServerQuery getQueryHandler(String game) {
        return sourceQuery;
    }
} 