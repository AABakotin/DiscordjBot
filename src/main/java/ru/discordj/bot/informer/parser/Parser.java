package ru.discordj.bot.informer.parser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ru.discordj.bot.informer.tools.Tools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parser {

    private static final Logger logger = LoggerFactory.getLogger(Parser.class);
    private final List<ServerQuery> queryHandlers;

    public Parser() {
        queryHandlers = Arrays.asList(
            new DayZServerQuery(),
            new Arma3ServerQuery()
            // Добавлять новые обработчики здесь
        );
    }

    public static Map<String, String> getInformation(DatagramPacket packet) {
        Map<String, String> stringMap = new LinkedHashMap<>();
        if (packet != null) {
            SteamInputStream sis = new SteamInputStream(
                    new ByteArrayInputStream(packet.getData()));

            try {
                sis.skipBytes(5);
                byte protocol = sis.readByte();

                String name = sis.readString();
                stringMap.put("ServerName", name);

                String map = sis.readString();
                stringMap.put("Map", map);

                String folder = sis.readString();
                String game = sis.readString();
                String appId = sis.readSteamShort();
                String players = String.valueOf(sis.read());

                stringMap.put("Players", players);
                String maxPlayers = String.valueOf(sis.read());

                stringMap.put("MaxPlayers", maxPlayers);
                String bots = String.valueOf(sis.read());

                String serverType = String.valueOf((char) sis.read());
                String environment = String.valueOf((char) sis.read());
                String visibility = String.valueOf(sis.readByte());
                String vac = String.valueOf(sis.readByte());

                String version = sis.readString();
                stringMap.put("Version", version);

                byte EDF = sis.readByte();
                if ((EDF & 0x80) > 0) {
                    String gamePort = sis.readSteamShort();
                    stringMap.put("gamePort", gamePort);
                }
                if ((EDF & 0x10) > 0) {
                    String steamId = String.valueOf(sis.readLong());
                }
                if ((EDF & 0x40) > 0) {
                    String sourceTVPort = sis.readSteamShort();
                    String sourceTVName = sis.readString();
                }
                if ((EDF & 0x20) > 0) {
                    String descTags = sis.readString();
                }
                if ((EDF & 0x01) > 0) {
                    String gameId = String.valueOf(sis.readLong());
                }

                return stringMap;

            } catch (IOException e) {
                e.fillInStackTrace();
            }
        }
        return stringMap;
    }

    public Map<String, String> getServerInfo(String ip, int port) {
        for (ServerQuery handler : queryHandlers) {
            if (handler.supportsPort(port)) {
                Map<String, String> info = handler.getServerInfo(ip, port);
                if (!info.isEmpty()) {
                    return info;
                }
            }
        }
        logger.warn("No suitable query handler found for port {}", port);
        return new HashMap<>();
    }

    private String readString(DataInputStream dis) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = dis.readByte()) != 0) {
            if (b >= 32 && b < 127) { // Только читаемые ASCII символы
                sb.append((char) b);
            }
        }
        return sb.toString();
    }
}