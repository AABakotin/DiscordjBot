package ru.discordj.bot.events.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class RulesInfo implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(RulesInfo.class);

    public String getName() {
        return "rules";
    }

    @Override
    public String getDescription() {
        return "Rules this TSD Discord";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> optionData = new ArrayList<>();
        optionData.add(new OptionData(OptionType.STRING, "rules", "Rules this TSD Discord", false));
        return optionData;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String imageServer = Objects.requireNonNull(event.getGuild()).getIconUrl();
        EmbedBuilder embed = (new EmbedBuilder())
                .setColor(Color.BLUE)
                .setTitle("*The Stealth Dudes*")
                .setImage(imageServer)
                .addField(
                        "Правила:",
                        " 1. Не матерится \n" +
                                "2. Не орать \n" +
                                "3. Уважать других\n" +
                                "4. Потом еще напишу)",
                        false);
        String author = event.getUser().getName();
        EmbedBuilder builder = embed.setFooter("requested by @" + author + " " + new Date());
        event.getUser()
                .openPrivateChannel()
                .complete()
                .sendMessageEmbeds(builder.build())
                .queue();
        event.reply("Rules has been sent to private message!")
                .setEphemeral(true)
                .queue();
        logger.info("requested 'rules' by @" + author + " " + formatter.format(date));
    }
}
