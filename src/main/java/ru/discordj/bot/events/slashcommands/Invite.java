package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.events.ICommand;

import java.util.Collections;
import java.util.List;

public class Invite implements ICommand {
    private static final IJsonHandler jsonHandler = JsonParse.getInstance();
    @Override
    public String getName() {
        return "invite";
    }

    @Override
    public String getDescription() {
        return "Invitation link.";
    }

    @Override
    public List<OptionData> getOptions() {
       return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply(jsonHandler.read().getInviteLink()).setEphemeral(true).queue();
    }
}
