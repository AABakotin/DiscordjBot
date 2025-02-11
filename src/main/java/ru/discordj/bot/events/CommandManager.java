package ru.discordj.bot.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import ru.discordj.bot.events.slashcommands.PlayMusicSlashCommand;
import ru.discordj.bot.utility.MessageCollector;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CommandManager extends ListenerAdapter {

    private final List<ICommand> commands = new ArrayList<>();
    private final Map<String, ICommand> commandsMap = new HashMap<>();

    public void add(ICommand command) {
        commands.add(command);
        commandsMap.put(command.getName(), command);
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            for (ICommand command : commands) {
                if (command.getOptions() == null) {
                    guild.upsertCommand(command.getName(), command.getDescription()).queue();
                } else {
                    guild.upsertCommand(command.getName(), command.getDescription())
                        .addOptions(command.getOptions())
                        .queue();
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        ICommand command = commandsMap.get(commandName);
        
        if (command != null) {
            command.execute(event);
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String commandName = event.getComponentId().split("_")[0]; // play_source -> play
        ICommand command = commandsMap.get(commandName);
        
        if (command instanceof PlayMusicSlashCommand) {
            ((PlayMusicSlashCommand) command).onStringSelectInteraction(event);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        for (ICommand command : commands) {
            if (command instanceof PlayMusicSlashCommand) {
                MessageCollector collector = ((PlayMusicSlashCommand) command)
                    .getActiveCollectors()
                    .get(event.getAuthor().getId());
                    
                if (collector != null) {
                    collector.handleMessage(event.getMessage());
                }
            }
        }
    }

    public static void registerCommands(JDA jda) {
        // Регистрируем команду /play без параметров
        jda.upsertCommand(
            Commands.slash("play", "Play a song")
        ).queue();
    }
}