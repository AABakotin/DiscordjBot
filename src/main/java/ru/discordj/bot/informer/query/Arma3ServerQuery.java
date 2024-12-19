package ru.discordj.bot.informer.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.io.*;
import java.net.*;
import ru.discordj.bot.informer.tools.Tools;

public class Arma3ServerQuery implements GameServerQuery {
    private static final Logger logger = LoggerFactory.getLogger(Arma3ServerQuery.class);
    private static final int TIMEOUT = 10000; // 10 секунд
    private static final int MAX_RETRIES = 3; // Максимум 3 попытки
    private static final byte[] QUERY = {
        (byte) 0xFE, (byte) 0xFD,  // GameSpy header
        0x00,                       // Query type
        0x04, 0x05, 0x06, 0x07     // Challenge number
    };

    @Override
    public Map<String, String> query(String ip, int port) {
        Map<String, String> info = new HashMap<>();
        
        // Пробуем несколько раз
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try (DatagramSocket socket = new DatagramSocket()) {
                socket.setSoTimeout(TIMEOUT);
                socket.send(new DatagramPacket(QUERY, QUERY.length, 
                    InetAddress.getByName(ip), port));

                byte[] buffer = new byte[2048];
                DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                socket.receive(response);

                byte[] data = response.getData();
                logger.info("Raw server data: {}", Tools.bytesToHex(data));

                // Парсим ответ GameSpy
                String responseData = new String(data, 0, response.getLength(), "UTF-8");
                String[] pairs = responseData.split("\\\\");

                boolean hasValidData = false;
                for (int i = 1; i < pairs.length - 1; i += 2) {
                    String key = pairs[i];
                    String value = pairs[i + 1];
                    
                    if (value != null && !value.trim().isEmpty()) {
                        switch (key) {
                            case "hostname":
                                info.put("name", Tools.cleanString(value));
                                hasValidData = true;
                                break;
                            case "mapname":
                                info.put("map", Tools.cleanString(value));
                                break;
                            case "numplayers":
                                info.put("players", value + "/" + pairs[i + 3]);
                                break;
                        }
                    }
                }

                if (hasValidData) {
                    logger.info("Successfully parsed ARMA 3 server info (attempt {}): {}", attempt, info);
                    return info;
                }
                
                logger.warn("Server response contains no valid data (attempt {})", attempt);
                info.clear();
                
            } catch (IOException e) {
                logger.warn("Error querying ARMA 3 server {}:{} (attempt {}): {}", 
                    ip, port, attempt, e.getMessage());
                if (attempt == MAX_RETRIES) {
                    logger.error("All {} attempts failed for server {}:{}", MAX_RETRIES, ip, port);
                }
            }
            
            // Небольшая пауза между попытками
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return info;
    }

    @Override
    public String getProtocolName() {
        return "arma3";
    }

    @Override
    public boolean supportsGame(String game) {
        return game.equalsIgnoreCase("arma3");
    }
} 