package ru.discordj.bot.informer.query;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.*;

public class SourceQuery implements GameServerQuery {
    private static final byte[] A2S_INFO = {
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
            socket.send(new DatagramPacket(A2S_INFO, A2S_INFO.length, 
                InetAddress.getByName(ip), port));

            byte[] buffer = new byte[1400];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            // Парсим ответ
            DataInputStream dis = new DataInputStream(
                new ByteArrayInputStream(response.getData()));
            dis.skipBytes(5);
            
            info.put("name", readString(dis));
            info.put("map", readString(dis));
            info.put("players", String.valueOf(dis.read()));
            info.put("maxplayers", String.valueOf(dis.read()));
        } catch (IOException e) {
            // Игнорируем ошибки
        }
        return info;
    }

    private String readString(DataInputStream dis) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte b;
        while ((b = dis.readByte()) != 0) {
            sb.append((char) b);
        }
        return sb.toString();
    }

    @Override
    public String getProtocolName() {
        return "source";
    }

    @Override
    public boolean supportsGame(String game) {
        return game.equalsIgnoreCase("source") || 
               game.equalsIgnoreCase("csgo") ||
               game.equalsIgnoreCase("tf2");
    }
} 