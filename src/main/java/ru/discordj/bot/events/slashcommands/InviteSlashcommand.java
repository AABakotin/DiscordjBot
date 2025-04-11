package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.events.ICommand;

import java.util.Collections;
import java.util.List;

public class InviteSlashcommand implements ICommand {
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
        // Получаем ссылку-приглашение для текущей гильдии
        String inviteLink = jsonHandler.read(event.getGuild()).getInviteLink();
        
        // Если ссылка не настроена для гильдии, используем глобальную
        if (inviteLink == null || inviteLink.equals("empty")) {
            inviteLink = jsonHandler.read().getInviteLink();
        }
        
        event.reply(inviteLink).setEphemeral(true).queue();
    }
}
