package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.MessageCollector;
import ru.discordj.bot.utility.pojo.RadioStation;
import ru.discordj.bot.utility.pojo.ServerRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PlayMusicSlashCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(PlayMusicSlashCommand.class);
    private final JsonParse jsonHandler = JsonParse.getInstance();

    // Для обратной совместимости с CommandManager (не используется, но метод нужен)
    private final Map<String, MessageCollector> activeCollectors = new HashMap<>();

    // Регулярные выражения для определения источника по URL
    private static final Pattern TWITCH_URL_PATTERN = Pattern.compile(
            "(?:https?://)?(?:www\\.)?twitch\\.tv.*");
    private static final Pattern HTTP_PATTERN = Pattern.compile(
            "^(https?://|www\\.).*");

    // Метод для CommandManager
    public Map<String, MessageCollector> getActiveCollectors() {
        return activeCollectors;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Воспроизведение музыки, радиостанций или поиск трека";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(
                OptionType.STRING,
                "query",
                "URL, название радиостанции или поисковый запрос",
                true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Проверка голосового канала
        if (!validateVoiceState(event)) {
            return;
        }

        // Получаем запрос
        String query = event.getOption("query").getAsString().trim();
        if (query.isEmpty()) {
            event.reply("❌ Запрос не может быть пустым")
                    .setEphemeral(true)
                    .queue(r -> r.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        // Определяем, что воспроизводить
        String playUrl = determinePlayUrl(event, query);
        if (playUrl == null) {
            // Сообщение об ошибке уже отправлено в determinePlayUrl
            return;
        }

        // Подключаемся к голосовому каналу (если ещё не подключены)
        connectToVoiceChannel(event);

        // Отправляем эфемерный ответ (будет сразу удалён)
        event.deferReply(true).queue(hook -> {
            // Запускаем воспроизведение
            PlayerManager.getInstance().play(event.getChannel().asTextChannel(), playUrl);
            // Удаляем эфемерное сообщение, чтобы не засорять чат
            hook.deleteOriginal().queue();
        });
    }

    /**
     * Определяет URL для воспроизведения на основе введённого запроса.
     * @param event событие (для отправки сообщений об ошибке)
     * @param query введённая строка
     * @return URL для воспроизведения или null, если ничего не найдено
     */
    private String determinePlayUrl(SlashCommandInteractionEvent event, String query) {
        if (query.startsWith("@")) {
            query = query.substring(1);
        }

        if (HTTP_PATTERN.matcher(query).matches()) {
            // Для Twitch убедимся, что есть схема https://
            if (TWITCH_URL_PATTERN.matcher(query).matches() && !query.startsWith("http")) {
                query = "https://" + query;
            }
            return query;
        }

        // Поиск радиостанции по имени
        ServerRules guildConfig = jsonHandler.read(event.getGuild());
        if (guildConfig != null && guildConfig.getRadioStations() != null) {
            RadioStation station = findStation(guildConfig.getRadioStations(), query);
            if (station != null) {
                return station.getUrl();
            }
        }

        return "ytsearch:" + query;
    }

    /**
     * Поиск радиостанции по имени (без учёта регистра).
     */
    private RadioStation findStation(List<RadioStation> stations, String name) {
        return stations.stream()
                .filter(station -> station.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    /**
     * Проверяет, находится ли пользователь в голосовом канале.
     */
    private boolean validateVoiceState(IReplyCallback event) {
        Member member = event.getMember();
        if (member == null) return false;
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.reply("❌ Вы должны находиться в голосовом канале")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return false;
        }
        return true;
    }

    /**
     * Подключается к голосовому каналу пользователя, если ещё не подключён.
     */
    private void connectToVoiceChannel(IReplyCallback event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (!audioManager.isConnected()) {
            Member member = event.getMember();
            if (member != null && member.getVoiceState() != null) {
                audioManager.openAudioConnection(member.getVoiceState().getChannel());
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Не используется
    }
}