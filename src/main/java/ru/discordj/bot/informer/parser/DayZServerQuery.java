package ru.discordj.bot.informer.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class DayZServerQuery implements ServerQuery {
    private static final Logger logger = LoggerFactory.getLogger(DayZServerQuery.class);
    private static final byte[] SOURCE_QUERY = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
        (byte) 0x54, // 'T'
        (byte) 0x53, (byte) 0x6F, (byte) 0x75, (byte) 0x72,
        (byte) 0x63, (byte) 0x65, (byte) 0x20, (byte) 0x45,
        (byte) 0x6E, (byte) 0x67, (byte) 0x69, (byte) 0x6E,
        (byte) 0x65, (byte) 0x20, (byte) 0x51, (byte) 0x75,
        (byte) 0x65, (byte) 0x72, (byte) 0x79, (byte) 0x00
    };

    @Override
    public Map<String, String> getServerInfo(String ip, int port) {
        Map<String, String> info = new HashMap<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(3000);
            InetAddress address = InetAddress.getByName(ip);

            DatagramPacket requestPacket = new DatagramPacket(SOURCE_QUERY, SOURCE_QUERY.length, address, port);
            socket.send(requestPacket);

            byte[] buffer = new byte[1400];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            byte[] data = responsePacket.getData();
            if (data[4] == 0x49) { // 'I' - правильный ответ
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bis);
                
                dis.skipBytes(5); // Пропускаем заголовок и тип
                dis.readByte(); // Пропускаем протокол
                
                // Читаем имя сервера
                info.put("name", readString(dis));
                
                // Читаем карту
                info.put("map", readString(dis));
                
                // Пропускаем folder и game
                readString(dis); // folder
                readString(dis); // game
                
                // Пропускаем steamappid
                dis.skipBytes(2);
                
                // Читаем игроков
                int players = dis.readByte() & 0xFF;
                int maxPlayers = dis.readByte() & 0xFF;
                info.put("players", players + "/" + maxPlayers);
            }
        } catch (Exception e) {
            logger.error("Error querying DayZ server {}:{}: {}", ip, port, e.getMessage());
        }
        return info;
    }

    private String readString(DataInputStream dis) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = dis.readByte()) != 0) {
            if (b >= 32 && b < 127) {
                sb.append((char) b);
            }
        }
        return sb.toString().trim();
    }

    @Override
    public boolean supportsPort(int port) {
        return port == 2411 || port == 2505; // Стандартные порты DayZ
    }
} 