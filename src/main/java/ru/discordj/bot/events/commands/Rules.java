package ru.discordj.bot.events.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.EmbedCreation;
import ru.discordj.bot.events.ICommand;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ru.discordj.bot.config.Constant.INVITATION_LINK;

public class Rules implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Rules.class);

    public String getName() {
        return "rules";
    }

    @Override
    public String getDescription() {
        return "Rules this TSD Discord";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String imageServer = event.getGuild().getIconUrl();
        String author = event.getUser().getName();

        event.getUser()
                .openPrivateChannel()
                .complete()
                .sendMessageEmbeds(EmbedCreation.embedWelcome(imageServer, author))
                .queue(
                        success -> event.reply("Rules has been sent to private message!")
                                .setEphemeral(true)
                                .queue(s -> logger.info("requested 'rules' by @" + author)),
                        failure -> logger.error("Some error occurred in 'ping', try again!")
                );
    }
}
