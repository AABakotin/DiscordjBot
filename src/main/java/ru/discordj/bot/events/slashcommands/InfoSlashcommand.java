package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.awt.Color;
import java.time.Instant;
import ru.discordj.bot.utility.BotConstants;

/**
 * Слэш-команда для отображения информации о боте.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InfoSlashcommand extends ListenerAdapter {
    private static final String COMMAND_NAME = "info";
    private static final String COMMAND_DESCRIPTION = "Показать информацию о боте";
    private static final String EMBED_TITLE = BotConstants.BOT_NAME + " - Информация";
    private static final Color EMBED_COLOR = new Color(BotConstants.EMBED_COLOR_HEX);
    
    private static final String FIELD_VERSION = "Версия";
    private static final String FIELD_AUTHOR = "Автор";
    private static final String FIELD_GITHUB = "GitHub";
    
    private static final String LOG_COMMAND = "Выполнена команда /info от пользователя: {}";
    private static final String ERROR_COMMAND = "❌ Ошибка при выполнении команды /info: {}";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(COMMAND_NAME)) return;

        try {
            handleInfoCommand(event);
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    /**
     * Возвращает данные для регистрации команды
     */
    public CommandData getCommandData() {
        return Commands.slash(COMMAND_NAME, COMMAND_DESCRIPTION);
    }

    /**
     * Обрабатывает команду /info
     */
    private void handleInfoCommand(SlashCommandInteractionEvent event) {
        EmbedBuilder embed = createInfoEmbed();
        
        event.replyEmbeds(embed.build())
            .setEphemeral(true)
            .queue(success -> log.info(LOG_COMMAND, event.getUser().getName()));
    }

    /**
     * Создает эмбед с информацией о боте
     */
    private EmbedBuilder createInfoEmbed() {
        return new EmbedBuilder()
            .setTitle(EMBED_TITLE)
            .setDescription(BotConstants.BOT_DESCRIPTION)
            .setColor(EMBED_COLOR)
            .addField(FIELD_VERSION, BotConstants.BOT_VERSION, true)
            .addField(FIELD_AUTHOR, BotConstants.BOT_AUTHOR, true)
            .addField(FIELD_GITHUB, BotConstants.GITHUB_LINK, false)
            .setTimestamp(Instant.now())
            .setFooter(BotConstants.BOT_NAME, null);
    }

    /**
     * Обрабатывает ошибки выполнения команды
     */
    private void handleError(SlashCommandInteractionEvent event, Exception e) {
        log.error(ERROR_COMMAND, e.getMessage());
        event.reply(ERROR_COMMAND.formatted(e.getMessage()))
            .setEphemeral(true)
            .queue();
    }
}
