package ru.discordj.bot.informer.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parser {


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
}