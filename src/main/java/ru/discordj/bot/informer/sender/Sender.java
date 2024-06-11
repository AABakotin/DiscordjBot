package ru.discordj.bot.informer.sender;

import ru.discordj.bot.informer.parser.Parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;


import static ru.discordj.bot.config.Constant.INFO_RESPONSE;

public class Sender implements ISender {
    private final InetSocketAddress host;
    private final DatagramSocket socket;

    private Sender(Configuration builder) {
        this.host = new InetSocketAddress(
                builder.ipAddress,
                builder.gamePort);
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            socket.setTrafficClass(0x04);
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> receive() {
        byte[] buf = new byte[4096];
        DatagramPacket rec = new DatagramPacket(buf, buf.length);
        try {
            socket.receive(rec);
        } catch (IOException e) {
            System.out.println(host.getAddress().getHostAddress() + ":" + host.getPort() + " did not respond.");
            return null;
        }

        if (rec.getLength() > 0 && rec.getData()[4] != INFO_RESPONSE) {
            System.out.println("ERROR: wrong packet received, expected 0x" + INFO_RESPONSE);
            return null;
        }
        return Parser.getInformation(rec);
    }

    @Override
    public void send(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, host);
        try {
            socket.send(packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Configuration {

        private String ipAddress;
        private int gamePort;

        public Configuration setAddress(String gameAddress, int queuePort) {
            ipAddress = gameAddress;
            gamePort = queuePort;
            return this;
        }
        public Sender build() {
            return new Sender(this);
        }
    }
}