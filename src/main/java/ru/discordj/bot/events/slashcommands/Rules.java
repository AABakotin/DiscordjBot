package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.embed.EmbedCreation;
import ru.discordj.bot.events.ICommand;

import java.util.Collections;
import java.util.List;

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
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String imageServer = event.getGuild().getIconUrl();
        String author = event.getUser().getName();

        event.getUser()
                .openPrivateChannel()
                .complete()
                .sendMessageEmbeds(EmbedCreation.get().embedWelcomeGuild(imageServer, author))
                .queue(
                        success -> event.reply("Rules has been sent to private message!")
                                .setEphemeral(true)
                                .queue(s -> logger.info("requested 'rules' by @{}", author)),
                        failure -> logger.error("Some error occurred in 'ping', try again!")
                );
    }
}
