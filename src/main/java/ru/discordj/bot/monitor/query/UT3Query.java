package ru.discordj.bot.monitor.query;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.*;

public class UT3Query implements GameServerQuery {
    private static final byte[] QUERY = {
        (byte) 0x78, (byte) 0x00, (byte) 0x00, (byte) 0x00,  // UT3 header
        0x00                                                  // Query type
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
        return "ut3";
    }

    @Override
    public boolean supportsGame(String game) {
        return game.equalsIgnoreCase("ut3") || 
               game.equalsIgnoreCase("unreal");
    }
} 