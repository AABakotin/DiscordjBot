package ru.discordj.bot.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ICommand {

    String getName();

    String getDescription();

    List<OptionData> getOptions();

    void execute(SlashCommandInteractionEvent event);

    default void onStringSelectInteraction(StringSelectInteractionEvent event) {}
}
