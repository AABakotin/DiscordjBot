package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Обработчик управления музыкальным плеером через кнопки и реакции.
 */
@Slf4j
@Component
public class MusicControlHandler extends ListenerAdapter {
    // Константы для эмодзи
    private static final String EMOJI_PLAY_PAUSE = "⏯️";
    private static final String EMOJI_STOP = "⏹️";
    private static final String EMOJI_NEXT = "⏭️";
    private static final String EMOJI_REPEAT = "🔁";

    // Константы для ID кнопок
    private static final String BUTTON_PLAY = "play";
    private static final String BUTTON_PAUSE = "pause";
    private static final String BUTTON_STOP = "stop";
    private static final String BUTTON_SKIP = "skip";
    private static final String BUTTON_REPEAT = "repeat";

    // Сообщения
    private static final String LOG_CONTROL = "Управление музыкой: {} от пользователя {} в канале {}";
    private static final String ERROR_CONTROL = "❌ Ошибка при управлении музыкой: {}";
    private static final String SUCCESS_MESSAGE = "✅ Команда выполнена";

    private MusicService musicService;

    @Autowired
    public void setMusicService(MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        try {
            handleButtonControl(event);
        } catch (Exception e) {
            handleError(e, event);
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) return;

        try {
            handleReactionControl(event);
        } catch (Exception e) {
            log.error(ERROR_CONTROL, e.getMessage());
        }
    }

    /**
     * Обрабатывает управление через кнопки
     */
    private void handleButtonControl(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        String guildId = event.getGuild().getId();

        switch (buttonId) {
            case BUTTON_PLAY:
                handlePlay(guildId);
                break;
            case BUTTON_PAUSE:
                handlePause(guildId);
                break;
            case BUTTON_STOP:
                handleStop(guildId);
                break;
            case BUTTON_SKIP:
                handleSkip(guildId);
                break;
            case BUTTON_REPEAT:
                handleRepeat(guildId);
                break;
            default:
                log.warn("Неизвестная кнопка: {}", buttonId);
                return;
        }

        logControl("button-" + buttonId, event.getUser().getName(), event.getChannel().getName());
        event.reply(SUCCESS_MESSAGE).setEphemeral(true).queue();
    }

    /**
     * Обрабатывает управление через реакции
     */
    private void handleReactionControl(MessageReactionAddEvent event) {
        String emojiCode = event.getEmoji().getAsReactionCode();
        String guildId = event.getGuild().getId();

        switch (emojiCode) {
            case EMOJI_PLAY_PAUSE:
                handlePlayPause(guildId);
                break;
            case EMOJI_STOP:
                handleStop(guildId);
                break;
            case EMOJI_NEXT:
                handleSkip(guildId);
                break;
            case EMOJI_REPEAT:
                handleRepeat(guildId);
                break;
        }

        logControl("reaction-" + emojiCode, event.getUser().getName(), event.getChannel().getName());
        event.getReaction().removeReaction(event.getUser()).queue();
    }

    private void handlePlay(String guildId) {
        if (musicService.isPaused(guildId)) {
            musicService.resumeTrack(guildId);
        }
    }

    private void handlePause(String guildId) {
        if (!musicService.isPaused(guildId)) {
            musicService.pauseTrack(guildId);
        }
    }

    private void handlePlayPause(String guildId) {
        if (musicService.isPaused(guildId)) {
            musicService.resumeTrack(guildId);
        } else {
            musicService.pauseTrack(guildId);
        }
    }

    private void handleStop(String guildId) {
        musicService.stopTrack(guildId);
    }

    private void handleSkip(String guildId) {
        musicService.skipTrack(guildId);
    }

    private void handleRepeat(String guildId) {
        musicService.toggleRepeat(guildId);
    }

    private void handleError(Exception e, ButtonInteractionEvent event) {
        log.error(ERROR_CONTROL, e.getMessage());
        event.reply(ERROR_CONTROL.formatted(e.getMessage()))
            .setEphemeral(true)
            .queue();
    }

    private void logControl(String action, String userName, String channelName) {
        log.info(LOG_CONTROL, action, userName, channelName);
    }
} 