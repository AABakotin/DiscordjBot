package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.embed.SendMessage;
import ru.discordj.bot.events.ICommand;

import java.util.ArrayList;
import java.util.List;


public class Info implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Info.class);


    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Information about user.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> dataList = new ArrayList<>();
        dataList.add(new OptionData(
                OptionType.USER, "information", "Information about User", true));
        return dataList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        SendMessage.infoUser(event);
    }
}
