package ru.discordj.bot.events.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;


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
        } else {
            event.reply("You do not have the required permission to execute this command!")
                    .setEphemeral(true)
                    .queue();
        }
    }
//        User target = event.getOption("user", OptionMapping::getAsUser);
//        Member member = event.getOption("user", OptionMapping::getAsMember);
//
//        if (member == null || target == null) {
//            event.reply("The person is not included in the group list.").setEphemeral(true).queue();
//            logger.info("The person is not included in the group list");
//            return;
//        }
//
//        if (member.hasPermission(Permission.BAN_MEMBERS)) {
//            event.reply("You ban members!").setEphemeral(true).queue();
//        } else{
//            event.reply("You do not have required permission to execute this command.").setEphemeral(true).queue();
//        }
//
//        if (!event.getMember().canInteract(member)) {
//            event.reply("You cannot ban this user.").setEphemeral(true).queue();
//        }
//        if (target.isBot()) {
//            event.reply("You cannot ban BOT.").setEphemeral(true).queue();
//            logger.info("You cannot ban BOT.");
//        }
//        String reason = event.getOption("reason", OptionMapping::getAsString);
//        AuditableRestAction<Void> action = Objects.requireNonNull(
//                        event.getGuild())
//                .ban(target, 0, SECONDS);
//        if (reason != null)
//            action = action.reason(reason);
//        action.queue(
//                success ->
//                        event.getHook()
//                                .editOriginal("**" + target.getName() + "** was banned by **" + event.getUser().getName() + "**!")
//                                .queue(info ->
//                                        logger.info(target.getName() + " was banned by " + event.getUser().getName() + "**!")),
//                failure ->
//                        event.getHook()
//                                .editOriginal("Some error occurred in 'ban', try again!")
//                                .queue(warn ->
//                                        logger.warn("Some error occurred in 'ban', try again!")));
//    }
}