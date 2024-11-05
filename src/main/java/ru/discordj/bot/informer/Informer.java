package ru.discordj.bot.informer;

import ru.discordj.bot.informer.sender.ISender;
import ru.discordj.bot.informer.sender.Sender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.discordj.bot.config.Constant.A2S_INFO;

public class Informer {

    private String address;
    private int port;
    private static Map<String, String> receive = new HashMap<>();

    public Informer(String address, int port) {
        this.address = address;
        this.port = port;


    }

    public static Map<String, String> getReceive() {
        return receive;
    }

    public void listenerServers() {
        List<Integer> portlist = new ArrayList<>();
        portlist.add(port);
        for (Integer gamePort : portlist) {
            ISender sender = new Sender.Configuration()
                    .setAddress(address, gamePort)
                    .build();
            sender.send(A2S_INFO);
            receive = sender.receive();

//            receive.forEach((k, v) -> System.out.println(k + ": " + v));

        }

    }
}
