package ru.discordj.bot.events.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class Ping implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Ping.class);
    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Ping your source to Bot.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> dataList = new ArrayList<>();
        dataList.add(new OptionData(
                OptionType.STRING, "ping", "Ping to you", false));
        return dataList;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true)
                .flatMap(v ->
                        event.getHook()
                                .editOriginalFormat(
                                        "Pong: %d ms", System.currentTimeMillis() - time
                                )
                ).queue();
        logger.info("requested 'ping' by @" + event.getUser().getName() + " " + new Date());

    }
}
