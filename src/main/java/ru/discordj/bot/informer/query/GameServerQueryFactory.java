package ru.discordj.bot.informer.query;

import java.util.HashMap;
import java.util.Map;

public class GameServerQueryFactory {
    private static final Map<String, GameServerQuery> queryHandlers = new HashMap<>();
    
    static {
        queryHandlers.put("source", new SourceQuery());
        queryHandlers.put("arma3", new Arma3ServerQuery());
        queryHandlers.put("dayz", new DayZServerQuery());
    }
    
    public static GameServerQuery getQueryHandler(String game) {
        GameServerQuery handler = queryHandlers.get(game.toLowerCase());
        if (handler == null) {
            // Определяем тип по порту
            if (game.equals("2302") || game.equals("2303") || game.equals("2403")) {
                return queryHandlers.get("arma3");
            }
            if (game.equals("2456") || game.equals("2457")) {
                return queryHandlers.get("dayz");
            }
            return queryHandlers.get("source"); // По умолчанию
        }
        return handler;
    }
} 