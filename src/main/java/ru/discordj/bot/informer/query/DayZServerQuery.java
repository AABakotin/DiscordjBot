package ru.discordj.bot.informer.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.*;
import ru.discordj.bot.informer.tools.Tools;

public class DayZServerQuery implements GameServerQuery {
    private static final Logger logger = LoggerFactory.getLogger(DayZServerQuery.class);
    private static final byte[] QUERY = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0x54, // 'T'
        (byte) 0x53, (byte) 0x6F, (byte) 0x75, (byte) 0x72,
        (byte) 0x63, (byte) 0x65, (byte) 0x20, (byte) 0x45,
        (byte) 0x6E, (byte) 0x67, (byte) 0x69, (byte) 0x6E,
        (byte) 0x65, (byte) 0x20, (byte) 0x51, (byte) 0x75,
        (byte) 0x65, (byte) 0x72, (byte) 0x79, (byte) 0x00
    };

    @Override
    public Map<String, String> query(String ip, int port) {
        Map<String, String> info = new HashMap<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            socket.send(new DatagramPacket(QUERY, QUERY.length, 
                InetAddress.getByName(ip), port));

            byte[] buffer = new byte[1400];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            byte[] data = response.getData();
            int offset = 6; // Пропускаем заголовок
            
            String name = Tools.getString(data, offset);
            offset += name.length() + 1;
            info.put("name", name);

            String map = Tools.getString(data, offset);
            offset += map.length() + 1;
            info.put("map", map);

            // Пропускаем folder и game
            offset += Tools.getString(data, offset).length() + 1;
            offset += Tools.getString(data, offset).length() + 1;

            // Пропускаем steamappid
            offset += 2;

            // Читаем игроков
            int players = data[offset++] & 0xFF;
            int maxPlayers = data[offset] & 0xFF;
            info.put("players", players + "/" + maxPlayers);

            logger.info("Raw server data: {}", Tools.bytesToHex(data));
            logger.info("Successfully parsed DayZ server info: {}", info);
        } catch (IOException e) {
            logger.error("Error querying DayZ server: {}", e.getMessage());
        }
        return info;
    }

    @Override
    public String getProtocolName() {
        return "dayz";
    }

    @Override
    public boolean supportsGame(String game) {
        return game.equalsIgnoreCase("dayz");
    }
} 