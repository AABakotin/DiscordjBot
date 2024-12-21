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
import java.text.SimpleDateFormat;
import java.util.Date;

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
        RulesMessage rules = JsonParse.getInstance().readRules();
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.setColor(Color.decode("#2f3136"));
        
        if (rules.getTitle() != null) {
            embed.setTitle(rules.getTitle());
        }
        
        if (rules.getWelcomeField() != null) {
            embed.addField("", rules.getWelcomeField(), false);
        }
        
        if (rules.getRulesField() != null) {
            embed.addField("", rules.getRulesField(), false);
        }
        
        if (rules.getFooter() != null) {
            String footer = rules.getFooter()
                .replace("{author}", event.getUser().getName())
                .replace("{date}", new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            embed.setFooter(footer);
        }
        
        event.replyEmbeds(embed.build()).queue();
    }
}
