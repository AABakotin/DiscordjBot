package ru.discordj.bot.utility;

import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.RulesMessage;

public interface IJsonHandler {
    Root read();
    void write(Root root);
    RulesMessage readRules();
    void writeRules(RulesMessage rules);
} 