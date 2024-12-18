package ru.discordj.bot.informer.parser;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SteamInputStream extends DataInputStream {

    private DatagramSocket socket;
    private InetAddress address;
    private int port;

    public SteamInputStream(ByteArrayInputStream inputStream) {
        super(inputStream);
    }

    public SteamInputStream(String ip, int port) throws IOException {
        super(new ByteArrayInputStream(new byte[0]));
        this.socket = new DatagramSocket();
        this.socket.setSoTimeout(2000);
        this.address = InetAddress.getByName(ip);
        this.port = port;
    }

    public void send(byte[] data) throws IOException {
        DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
        socket.send(packet);
    }

    public byte[] receive() throws IOException {
        byte[] buffer = new byte[1400];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet.getData();
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