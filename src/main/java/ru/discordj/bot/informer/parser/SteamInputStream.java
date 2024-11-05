package ru.discordj.bot.informer.parser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class SteamInputStream extends DataInputStream {

    public SteamInputStream(ByteArrayInputStream inputStream) {
        super(inputStream);
    }

    public String readString() throws IOException {
        StringBuilder res = new StringBuilder();
        byte b = readByte();
        while (b != 0x00) {
            res.append((char) b);
            b = readByte();
        }
        return res.toString();
    }

    public String readSteamLong() throws IOException {
        return String.valueOf(Integer.reverseBytes(readInt()));
    }

    public String readSteamFloat() throws IOException {
        return String.valueOf(Float.intBitsToFloat(Integer.reverseBytes(readInt())));
    }

    public String readSteamShort() throws IOException {
        return String.valueOf(Short.reverseBytes(readShort()));
    }

}