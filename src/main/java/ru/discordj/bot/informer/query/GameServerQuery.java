package ru.discordj.bot.informer.query;

import java.util.Map;

public interface GameServerQuery {
    Map<String, String> query(String ip, int port);
    String getProtocolName();
    boolean supportsGame(String game);
} 