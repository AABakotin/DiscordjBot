package ru.discordj.bot.events.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;

import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.DAYS;


public class Ban implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Ban.class);

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Will ban a member.";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> dataList = new ArrayList<>();
        dataList.add(new OptionData(OptionType.USER, "banned", "The user to ban", true));
        dataList.add(new OptionData(OptionType.INTEGER, "days", "The user to ban", true));
        return dataList;

    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getMember().hasPermission(Permission.BAN_MEMBERS)) {
            Member banned = event.getOption("banned").getAsMember();

            if (banned == null){
                event.reply("The person is not included in the group list.").queue();
            }

            int days = event.getOption("days").getAsInt();
            banned.ban(days, DAYS).queue();
            event.reply("**" + banned + "** was banned by **" + event.getUser().getName() + "**!").queue();
            logger.info("**" + banned + "** was banned by **" + event.getUser().getName() + "**!");
        } else {
            event.reply("You do not have the required permission to execute this command!")
                    .setEphemeral(true)
                    .queue();
            logger.warn("Some error occurred in 'hello', try again!");
        }
    }
}