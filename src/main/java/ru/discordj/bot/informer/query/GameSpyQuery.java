package ru.discordj.bot.informer.query;

import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.*;

public class GameSpyQuery implements GameServerQuery {
    private static final byte[] QUERY = {
        (byte) 0xFE, (byte) 0xFD,  // GameSpy header
        0x00,                       // Query type
        0x33, 0x33, 0x33, 0x33     // Challenge number
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

            // Парсим ответ GameSpy
            String[] data = new String(response.getData(), 0, response.getLength())
                .split("\\\\");
            
            for (int i = 1; i < data.length; i += 2) {
                if (i + 1 < data.length) {
                    info.put(data[i], data[i + 1]);
                }
            }
        } catch (IOException e) {
            // Игнорируем ошибки
        }
        return info;
    }

    @Override
    public String getProtocolName() {
        return "gamespy";
    }

    @Override
    public boolean supportsGame(String game) {
        return game.equalsIgnoreCase("arma3");
    }
} 