package ru.discordj.bot.informer;

import ru.discordj.bot.informer.sender.ISender;
import ru.discordj.bot.informer.sender.Sender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static ru.discordj.bot.config.Constant.A2S_INFO;

public class Informer {

    private static final String address = "195.18.27.92";
    private static final int port = 2343;



    public static void main(String[] args) {

        List<Integer> portlist = new ArrayList<>();
        portlist.add(port);
        for (Integer i : portlist) {
            ISender sender = new Sender.Configuration()
                    .setAddress(address, i)
                    .build();

            sender.send(A2S_INFO);
            Map<String, String> receive = sender.receive();
            receive.forEach((k, v) -> System.out.println(k + ": " + v));

        }
    }
}
