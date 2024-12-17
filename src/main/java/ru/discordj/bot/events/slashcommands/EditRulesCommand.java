package ru.discordj.bot.events.slashcommands;


import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ISlashCommandWithSubcommands;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;

import java.util.List;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.utility.pojo.RulesMessage;

public class EditRulesCommand implements ISlashCommandWithSubcommands {
    private static final Logger logger = LoggerFactory.getLogger(EditRulesCommand.class);
    private final IJsonHandler jsonHandler = JsonParse.getInstance();

    @Override
    public String getName() {
        return "editrules";
    }

    @Override
    public String getDescription() {
        return "Edit server rules";
    }

    @Override
    public List<SubcommandData> getSubcommands() {
        return List.of(
            new SubcommandData("title", "Edit rules server name")
                .addOption(OptionType.STRING, "text", "Title text", true),
            new SubcommandData("welcome", "Edit welcome text")
                .addOption(OptionType.STRING, "text", "Welcome text", true),
            new SubcommandData("rules", "Edit rules text")
                .addOption(OptionType.STRING, "text", "Rules text", true),
            new SubcommandData("footer", "Edit footer text")
                .addOption(OptionType.STRING, "text", "Footer text", true)
        );
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!event.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            event.reply("You need administrator permissions").setEphemeral(true).queue();
            return;
        }

        String subcommand = event.getSubcommandName();
        if (subcommand == null) {
            event.reply("Please use one of the subcommands: /editrules title, welcome, rules, or footer")
                .setEphemeral(true)
                .queue();
            return;
        }

        var textOption = event.getOption("text");
        if (textOption == null) {
            event.reply("Please provide text to update").setEphemeral(true).queue();
            return;
        }
        String text = textOption.getAsString();

        try {
            RulesMessage rules = jsonHandler.readRules();
            
            switch (subcommand) {
                case "title":
                    rules.setTitle("█▓▒░⡷⠂" + text + "⠐⢾░▒▓█");
                    break;
                case "welcome":
                    rules.setWelcomeField(text.toUpperCase());
                    break;
                case "rules":
                    rules.setRulesField(text);
                    break;
                case "footer":
                    rules.setFooter(text);
                    break;
                default:
                    event.reply("Invalid subcommand").setEphemeral(true).queue();
                    return;
            }

            jsonHandler.writeRules(rules);
            event.getChannel()
                .sendMessageEmbeds(EmbedFactory.getWelcomeCreator().embedWelcomeGuild(event.getMember()))
                .queue();
            event.reply("Rules updated successfully").setEphemeral(true).queue();

        } catch (Exception e) {
            logger.error("Failed to update rules: {}", e.getMessage(), e);
            event.reply("Failed to update rules: " + e.getMessage()).setEphemeral(true).queue();
        }
    }
} 