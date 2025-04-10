package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;

import java.util.Collections;
import java.util.List;

public class PingSlashcommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(PingSlashcommand.class);

    @Override
    public String getName() {
        return "ping";
    }

    @Override
    public String getDescription() {
        return "Ping your source to Bot.";
    }

    @Override
    public List<OptionData> getOptions() {
       return Collections.emptyList();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        long time = System.currentTimeMillis();
        event.reply("Pong!").setEphemeral(true)
                .flatMap(v ->
                        event.getHook()
                                .editOriginalFormat(
                                        "Pong: %d ms", System.currentTimeMillis() - time))
                .queue(
                        success -> logger.info("Юзер {} запросил 'ping'", event.getUser().getName()),
                        failure -> logger.error("Ошибка при запросе 'ping', попробуйте снова!")
                );
    }
}
