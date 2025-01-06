package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.events.slashcommands.PlayMusicSlashCommand;
import ru.discordj.bot.events.slashcommands.RulesSlashcommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Слушатель события готовности бота.
 * Регистрирует слэш-команды и выполняет инициализацию при запуске.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReadyListener extends ListenerAdapter {
    private static final String LOG_READY = "Бот {} готов к работе!";
    private static final String LOG_COMMANDS = "Зарегистрированы команды: {}";
    private static final String ERROR_COMMANDS = "❌ Ошибка при регистрации команд: {}";

    private final PlayMusicSlashCommand playMusicCommand;
    private final RulesSlashcommand rulesCommand;

    @Override
    public void onReady(ReadyEvent event) {
        try {
            registerCommands(event);
            logBotReady(event);
        } catch (Exception e) {
            log.error(ERROR_COMMANDS, e.getMessage());
        }
    }

    /**
     * Регистрирует слэш-команды
     */
    private void registerCommands(ReadyEvent event) {
        event.getJDA().updateCommands()
            .addCommands(
                playMusicCommand.getCommandData(),
                rulesCommand.getCommandData()
            )
            .queue(
                commands -> log.info(LOG_COMMANDS, 
                    commands.stream()
                        .map(cmd -> cmd.getName())
                        .toList()
                ),
                error -> log.error(ERROR_COMMANDS, error.getMessage())
            );
    }

    /**
     * Логирует успешный запуск бота
     */
    private void logBotReady(ReadyEvent event) {
        log.info(LOG_READY, 
            event.getJDA().getSelfUser().getName()
        );
    }
} 