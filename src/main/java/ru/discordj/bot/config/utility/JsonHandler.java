package ru.discordj.bot.config.utility;

import ru.discordj.bot.config.utility.pojo.Root;

public interface JsonHandler {

    Root read();

    void write(Root root);
}
