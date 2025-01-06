package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Обработчик приветственной слэш-команды.
 * Отправляет приветственное сообщение в ответ на команду /hello.
 */
@Slf4j
@Component
public class HelloSlashcommand extends ListenerAdapter {
    private static final String COMMAND_NAME = "hello";
    private static final String COMMAND_DESCRIPTION = "Получить приветственное сообщение";
    private static final String GREETING_FORMAT = "Привет, %s! 👋";
    private static final String LOG_COMMAND = "Выполнена команда /hello от пользователя: {}";
    private static final String ERROR_COMMAND = "❌ Ошибка при выполнении команды /hello: {}";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals(COMMAND_NAME)) return;

        try {
            handleHelloCommand(event);
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
     * Обрабатывает команду /hello
     */
    private void handleHelloCommand(SlashCommandInteractionEvent event) {
        String userName = event.getUser().getName();
        String response = String.format(GREETING_FORMAT, userName);
        
        event.reply(response)
            .setEphemeral(true)
            .queue(success -> log.info(LOG_COMMAND, userName));
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