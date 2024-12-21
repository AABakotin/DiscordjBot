package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.events.ICommand;
import java.util.Collections;
import java.util.List;

public class UpdateCommandsSlashCommand implements ICommand {
    @Override
    public String getName() {
        return "update_commands";
    }

    @Override
    public String getDescription() {
        return "Update slash commands (Owner only)";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Проверяем, является ли пользователь владельцем сервера
        if (event.getGuild().getOwnerIdLong() != event.getUser().getIdLong()) {
            event.reply("❌ Только владелец сервера может использовать эту команду!")
                .setEphemeral(true)
                .queue();
            return;
        }

        event.deferReply().queue();
        event.getGuild().updateCommands().queue(
            success -> event.getHook().sendMessage("✅ Slash команды успешно обновлены!").queue(),
            error -> event.getHook().sendMessage("❌ Ошибка обновления команд: " + error.getMessage()).queue()
        );
    }
} 