package ru.discordj.bot.informer.sender;

import ru.discordj.bot.informer.parser.Parser;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;

/**
 * Класс для отправки и получения данных от игровых серверов по протоколу UDP.
 * Реализует интерфейс ISender и использует паттерн Builder для конфигурации.
 * Обеспечивает базовый функционал для взаимодействия с серверами через UDP сокеты.
 */
public class Sender implements ISender {
    private final InetSocketAddress host;
    private final DatagramSocket socket;

    /**
     * Создает новый экземпляр отправителя с указанной конфигурацией.
     * Инициализирует UDP сокет с таймаутом и настройками трафика.
     *
     * @param builder конфигурация с адресом и портом сервера
     * @throws RuntimeException если не удалось создать сокет
     */
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
            return null;
        }

        if (rec.getLength() > 0 && rec.getData()[4] != 0x49) {
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

    /**
     * Внутренний класс для конфигурации Sender.
     * Реализует паттерн Builder для удобного создания экземпляров Sender.
     */
    public static class Configuration {
        private String ipAddress;
        private int gamePort;

        /**
         * Устанавливает адрес и порт сервера.
         *
         * @param gameAddress IP-адрес сервера
         * @param queuePort порт сервера
         * @return текущий экземпляр Configuration для цепочки вызовов
         */
        public Configuration setAddress(String gameAddress, int queuePort) {
            ipAddress = gameAddress;
            gamePort = queuePort;
            return this;
        }

        /**
         * Создает новый экземпляр Sender с текущей конфигурацией.
         *
         * @return сконфигурированный экземпляр Sender
         */
        public Sender build() {
            return new Sender(this);
        }
    }
}