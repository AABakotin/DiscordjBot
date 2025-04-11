package ru.discordj.bot.events;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.Collections;
import java.util.List;

public interface ICommand {

    String getName();

    String getDescription();

    List<OptionData> getOptions();

    void execute(SlashCommandInteractionEvent event);

    default void onStringSelectInteraction(StringSelectInteractionEvent event) {}
    
    /**
     * Возвращает настройки прав доступа для команды по умолчанию.
     * 
     * @return DefaultMemberPermissions для команды
     */
    default DefaultMemberPermissions getDefaultMemberPermissions() {
        return DefaultMemberPermissions.ENABLED;
    }
    
    /**
     * Возвращает список подкоманд для slash-команды, если они есть.
     * 
     * @return список подкоманд или пустой список
     */
    default List<SubcommandData> getSubcommands() {
        return Collections.emptyList();
    }
}
