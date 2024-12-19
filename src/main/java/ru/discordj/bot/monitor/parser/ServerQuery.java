package ru.discordj.bot.monitor.parser;

import java.util.Map;

public interface ServerQuery {
    Map<String, String> getServerInfo(String ip, int port);
    boolean supportsPort(int port);
} 