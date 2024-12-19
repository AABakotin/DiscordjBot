package ru.discordj.bot.monitor.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.discordj.bot.monitor.query.GameServerQueryFactory;

import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;

public class Parser {
    private static final Logger logger = LoggerFactory.getLogger(Parser.class);

    public static Map<String, String> getInformation(DatagramPacket packet) {
        Map<String, String> info = new HashMap<>();
        if (packet != null) {
            try {
                SteamInputStream sis = new SteamInputStream(
                    new ByteArrayInputStream(packet.getData()));
                
                sis.skipBytes(5);
                sis.readByte(); // protocol
                
                info.put("name", sis.readString());
                info.put("map", sis.readString());
                info.put("players", String.valueOf(sis.read()));
                info.put("maxplayers", String.valueOf(sis.read()));
            } catch (IOException e) {
                logger.error("Error parsing packet: {}", e.getMessage());
            }
        }
        return info;
    }

    public Map<String, String> getServerInfo(String ip, int port) {
        try {
            return GameServerQueryFactory.getQueryHandler("source").query(ip, port);
        } catch (Exception e) {
            logger.error("Error querying server {}:{}: {}", ip, port, e.getMessage());
            return new HashMap<>();
        }
    }
}