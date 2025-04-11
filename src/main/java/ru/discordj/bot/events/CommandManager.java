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
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import ru.discordj.bot.events.slashcommands.RadioPlaySlashCommand;
import ru.discordj.bot.events.slashcommands.RadioListSlashCommand;
import ru.discordj.bot.events.slashcommands.RadioAddSlashCommand;
import ru.discordj.bot.events.slashcommands.RadioRemoveSlashCommand;
import ru.discordj.bot.events.slashcommands.RadioReloadSlashCommand;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CommandManager extends ListenerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);
    private final List<ICommand> commands = new ArrayList<>();
    private final Map<String, ICommand> commandsMap = new HashMap<>();

    public CommandManager() {
        // Регистрируем команды для управления радио
        add(new RadioPlaySlashCommand());
        add(new RadioListSlashCommand());
        add(new RadioAddSlashCommand());
        add(new RadioRemoveSlashCommand());
        add(new RadioReloadSlashCommand());
    }

    public void add(ICommand command) {
        commands.add(command);
        commandsMap.put(command.getName(), command);
    }

    @Override
    public void onReady(ReadyEvent event) {
        for (Guild guild : event.getJDA().getGuilds()) {
            List<SlashCommandData> guildCommands = new ArrayList<>();
            
            for (ICommand command : commands) {
                SlashCommandData slashCommand = Commands.slash(
                    command.getName(), 
                    command.getDescription())
                    .setDefaultPermissions(command.getDefaultMemberPermissions());
                
                // Добавляем опции если они есть
                if (command.getOptions() != null && !command.getOptions().isEmpty()) {
                    slashCommand.addOptions(command.getOptions());
                }
                
                // Добавляем подкоманды если они есть
                if (!command.getSubcommands().isEmpty()) {
                    slashCommand.addSubcommands(command.getSubcommands());
                }
                
                guildCommands.add(slashCommand);
                logger.info("Зарегистрирована команда: /{}", command.getName());
            }
            
            // Обновляем все команды гильдии за один запрос
            guild.updateCommands().addCommands(guildCommands).queue(
                success -> logger.info("Команды успешно обновлены для гильдии: {}", guild.getName()),
                error -> logger.error("Ошибка при обновлении команд для гильдии {}: {}", 
                    guild.getName(), error.getMessage())
            );
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        ICommand command = commandsMap.get(commandName);
        
        if (command != null) {
            try {
                command.execute(event);
            } catch (Exception e) {
                logger.error("Ошибка при выполнении команды {}: {}", commandName, e.getMessage(), e);
                event.reply("❌ Произошла ошибка при выполнении команды: " + e.getMessage())
                    .setEphemeral(true)
                    .queue();
            }
        }
    }

    @Override
    public void onStringSelectInteraction(@NotNull StringSelectInteractionEvent event) {
        String componentId = event.getComponentId();
        String commandName = componentId.split("_")[0]; // play_source -> play, radio_select -> radio
        ICommand command = commandsMap.get(commandName);
        
        if (command != null) {
            try {
                command.onStringSelectInteraction(event);
            } catch (Exception e) {
                logger.error("Ошибка при обработке взаимодействия с выбором для команды {}: {}", 
                    commandName, e.getMessage(), e);
                event.reply("❌ Произошла ошибка: " + e.getMessage())
                    .setEphemeral(true)
                    .queue();
            }
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