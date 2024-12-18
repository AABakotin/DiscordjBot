package ru.discordj.bot.informer.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class Arma3ServerQuery implements ServerQuery {
    private static final Logger logger = LoggerFactory.getLogger(Arma3ServerQuery.class);
    
    // Запрос для ARMA 3 (Query протокол)
    private static final byte[] ARMA3_QUERY = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,  // Header
        (byte) 0x54,                                          // 'T'
        (byte) 0x53, (byte) 0x6F, (byte) 0x75, (byte) 0x72,  // 'Sour'
        (byte) 0x63, (byte) 0x65, (byte) 0x20, (byte) 0x45,  // 'ce E'
        (byte) 0x6E, (byte) 0x67, (byte) 0x69, (byte) 0x6E,  // 'ngin'
        (byte) 0x65, (byte) 0x20, (byte) 0x51, (byte) 0x75,  // 'e Qu'
        (byte) 0x65, (byte) 0x72, (byte) 0x79, (byte) 0x00   // 'ery\0'
    };

    @Override
    public Map<String, String> getServerInfo(String ip, int port) {
        Map<String, String> info = new HashMap<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            InetAddress address = InetAddress.getByName(ip);

            // Отправляем запрос
            DatagramPacket requestPacket = new DatagramPacket(ARMA3_QUERY, ARMA3_QUERY.length, address, port);
            socket.send(requestPacket);

            // Получаем ответ
            byte[] buffer = new byte[2048];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);

            // Парсим ответ
            byte[] data = responsePacket.getData();
            int length = responsePacket.getLength();

            if (length > 5) {
                ByteArrayInputStream bis = new ByteArrayInputStream(data);
                DataInputStream dis = new DataInputStream(bis);

                // Пропускаем заголовок
                dis.skipBytes(5);
                dis.readByte(); // Пропускаем протокол

                // Читаем имя сервера
                String name = readString(dis);
                info.put("name", name.trim());

                // Читаем карту
                String map = readString(dis);
                info.put("map", map.trim());

                // Пропускаем folder и game
                readString(dis); // folder
                readString(dis); // game

                // Пропускаем steamappid
                dis.skipBytes(2);

                // Читаем игроков
                int players = dis.readByte() & 0xFF;
                int maxPlayers = dis.readByte() & 0xFF;
                info.put("players", players + "/" + maxPlayers);

                logger.info("Successfully parsed ARMA 3 server info: {}", info);
            }
        } catch (Exception e) {
            logger.error("Error querying ARMA 3 server {}:{}: {}", ip, port, e.getMessage());
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
        return sb.toString();
    }

    @Override
    public boolean supportsPort(int port) {
        return port == 2303;
    }
} 