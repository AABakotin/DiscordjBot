package ru.discordj.bot.events.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.Constant;
import ru.discordj.bot.events.ICommand;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
        DateTimeFormatter frm = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        Date date = new Date();

        User target = event.getOption("info", OptionMapping::getAsUser);
        Member member = event.getOption("info", OptionMapping::getAsMember);
        String avatar = target.getAvatarUrl();
        if (avatar == null) avatar = Constant.NON_AVATAR_URL;

        EmbedBuilder avatarEmbed = new EmbedBuilder()
                .setColor(Color.YELLOW)
                .setTitle(target.getName() + "'s info:")
                .setDescription("Join on " + member.getTimeJoined().format(frm))
                .addField("Name", target.getName(), true)
                .addField("Online Status: ", member.getOnlineStatus().getKey(), true)
                .addField("Avatar: ", "The Avatar is below ", false)
                .setImage(avatar)
                .setFooter("requested by " + date, event.getGuild().getIconUrl());

        event.reply("Requested 'info' @" + target.getName() + " by @" + event.getUser().getName())
                .setEmbeds(avatarEmbed.build())
                .setEphemeral(true)
                .queue(
                        success -> logger.info("requested 'info' @" + target.getName() + " by @" + event.getUser().getName()),
                        failure -> logger.error("Some error occurred in 'info', try again!")
                );
    }
}
