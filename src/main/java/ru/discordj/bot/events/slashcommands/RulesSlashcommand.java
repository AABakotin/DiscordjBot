package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.RulesMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import java.awt.Color;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

public class RulesSlashcommand implements ICommand {
    private final IJsonHandler jsonHandler = JsonParse.getInstance();

    @Override
    public String getName() {
        return "rules";
    }

    @Override
    public String getDescription() {
        return "Show server rules";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        RulesMessage rules = jsonHandler.readRules();
        
        EmbedBuilder builder = new EmbedBuilder()
            .setColor(Color.BLUE)
            .setTitle(rules.getTitle())
            .addField("Добро пожаловать!", rules.getWelcomeField(), false)
            .addField("✨ ***ВНИМАНИЕ*** ✨", rules.getRulesField(), false)
            .setFooter(rules.getFooter()
                .replace("{date}", ZonedDateTime.now().format(DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy")))
                .replace("{author}", event.getMember().getUser().getName()));

        event.replyEmbeds(builder.build()).queue();
    }
}
