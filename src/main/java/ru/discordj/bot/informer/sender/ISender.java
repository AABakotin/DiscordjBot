package ru.discordj.bot.informer.sender;

import java.util.Map;

public interface ISender {

    void send(byte[] data);

    Map<String, String> receive();


}