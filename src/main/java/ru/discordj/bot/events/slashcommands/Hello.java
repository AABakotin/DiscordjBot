package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.embed.SendMessage;
import ru.discordj.bot.events.ICommand;

import java.util.List;

public class Hello implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Hello.class);


    public String getName() {
        return "hello";
    }

    @Override
    public String getDescription() {
        return "Hello My Friend ;)";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        SendMessage.sendHello(event);
    }
}