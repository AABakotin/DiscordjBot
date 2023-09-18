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

import static java.util.concurrent.TimeUnit.SECONDS;


public class Ban implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Ban.class);

    @Override
    public String getName() {
        return "ban";
    }

    @Override
    public String getDescription() {
        return "Ban users";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> dataList = new ArrayList<>();
        dataList.add(new OptionData(
                OptionType.USER, "ban", "Ban user", true));
        return dataList;

    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.BAN_MEMBERS)) {
            event.reply("You cannot ban members! Nice try ;)").setEphemeral(true).queue();
            return;
        }
        User target = event.getOption("user", OptionMapping::getAsUser);
        Member member = event.getOption("user", OptionMapping::getAsMember);
        if (member == null || target == null) {
            event.reply("User has been Ban or No in Your guild.").setEphemeral(true).queue();
            logger.info("User has been Ban or No in Your guild");
            return;
        }
        if (!event.getMember().canInteract(member)) {
            event.reply("You cannot ban this user.").setEphemeral(true).queue();
            return;
        }
        if (Objects.requireNonNull(target).isBot()) {
            event.reply("You cannot ban BOT.").setEphemeral(true).queue();
            logger.info("You cannot ban BOT.");
            return;
        }

        event.deferReply().queue();
        String reason = event.getOption("reason", OptionMapping::getAsString);
        AuditableRestAction<Void> action = Objects.requireNonNull(
                        event.getGuild())
                .ban(target, 0, SECONDS);
        if (reason != null)
            action = action.reason(reason);
        action.queue(
                success ->
                        event.getHook()
                                .editOriginal("**" + target.getName() + "** was banned by **" + event.getUser().getName() + "**!")
                                .queue(info ->
                                        logger.info("**" + target + "** was banned by **" + event.getUser() + "**!")),
                error ->
                        event.getHook()
                                .editOriginal("Some error occurred, try again!")
                                .queue(warn ->
                                        logger.warn("Some error occurred, try again!")));
    }
}