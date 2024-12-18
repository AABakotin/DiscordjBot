package ru.discordj.bot.informer.parser;

import java.util.Map;

public interface ServerQuery {
    Map<String, String> getServerInfo(String ip, int port);
    boolean supportsPort(int port);
} 