package ru.discordj.bot.informer.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.informer.parser.SteamInputStream;
import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class SourceQuery implements GameServerQuery {
    private static final Logger logger = LoggerFactory.getLogger(SourceQuery.class);
    private static final byte[] A2S_INFO = {
        (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, 0x54, 0x53, (byte) 0x6F, (byte) 0x75, (byte) 0x72,
        (byte) 0x63, (byte) 0x65, (byte) 0x20, (byte) 0x45, (byte) 0x6E, (byte) 0x67, (byte) 0x69, (byte) 0x6E,
        (byte) 0x65, (byte) 0x20, (byte) 0x51, (byte) 0x75, (byte) 0x65, (byte) 0x72, (byte) 0x79, (byte) 0x00,
        (byte) 0x0A, (byte) 0x08, (byte) 0x5E, (byte) 0xEA
};

    @Override
    public Map<String, String> query(String ip, int port) {
        Map<String, String> info = new HashMap<>();
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(5000);
            socket.send(new DatagramPacket(A2S_INFO, A2S_INFO.length, 
                InetAddress.getByName(ip), port));

            byte[] buffer = new byte[2048];
            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
            socket.receive(response);

            byte[] data = response.getData();
            if (data[4] == 0x49) {
                SteamInputStream sis = new SteamInputStream(new ByteArrayInputStream(data));
                
                sis.skipBytes(5);
                sis.readByte();
                
                info.put("name", sis.readString());
                info.put("map", sis.readString());
                info.put("folder", sis.readString());
                info.put("game", sis.readString());
                
                short appId = sis.readShort();
                info.put("appid", String.valueOf(appId));
                
                info.put("players", String.valueOf(sis.read()));
                info.put("maxplayers", String.valueOf(sis.read()));
                info.put("bots", String.valueOf(sis.read()));
                
                byte serverType = sis.readByte();
                byte environment = sis.readByte();
                byte visibility = sis.readByte();
                byte vac = sis.readByte();
                
                info.put("version", sis.readString());
                
                byte edf = sis.readByte();
                
                if ((edf & 0x80) != 0) {
                    info.put("port", String.valueOf(sis.readShort()));
                }
                if ((edf & 0x10) != 0) {
                    info.put("steamid", String.valueOf(sis.readLong()));
                }
                if ((edf & 0x40) != 0) {
                    info.put("sourcetv_port", String.valueOf(sis.readShort()));
                    info.put("sourcetv_name", sis.readString());
                }
                if ((edf & 0x20) != 0) {
                    info.put("keywords", sis.readString());
                }
                if ((edf & 0x01) != 0) {
                    info.put("gameid", String.valueOf(sis.readLong()));
                }

                int players = Integer.parseInt(info.get("players"));
                int maxPlayers = Integer.parseInt(info.get("maxplayers"));
                int bots = Integer.parseInt(info.get("bots"));
                
                info.put("players", (players + bots) + "/" + maxPlayers);
            }
        } catch (IOException e) {
            logger.error("Query error {}:{} - {}", ip, port, e.getMessage());
        }
        return info;
    }

    @Override
    public String getProtocolName() {
        return "source";
    }

    @Override
    public boolean supportsGame(String game) {
        return true;
    }
} 