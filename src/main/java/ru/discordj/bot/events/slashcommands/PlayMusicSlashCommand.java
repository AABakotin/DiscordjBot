package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Обработчик slash-команды для воспроизведения музыки.
 * Позволяет добавлять треки в очередь воспроизведения по URL или поисковому запросу.
 */
public class PlayMusicSlashCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(PlayMusicSlashCommand.class);
    private final Map<String, MessageCollector> activeCollectors = new HashMap<>();
    private final JsonParse jsonHandler = JsonParse.getInstance();
    
    // Регулярные выражения для определения источника по URL
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
            "(?:https?://)?(?:www\\.)?(?:youtube\\.com|youtu\\.be).*");
    private static final Pattern TWITCH_URL_PATTERN = Pattern.compile(
            "(?:https?://)?(?:www\\.)?twitch\\.tv.*");
    private static final Pattern BANDCAMP_URL_PATTERN = Pattern.compile(
            "(?:https?://)?(?:[a-zA-Z0-9-]+\\.)?bandcamp\\.com.*");

    public Map<String, MessageCollector> getActiveCollectors() {
        return activeCollectors;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Воспроизведение музыки и радиостанций";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(
            OptionType.STRING, 
            "query", 
            "URL, название трека или радиостанции", 
            true)); // Сделали опцию обязательной
        return options;
    }
    
    @Override
    public List<SubcommandData> getSubcommands() {
        // Не используем подкоманды
        return new ArrayList<>();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Проверяем, находится ли пользователь в голосовом канале
        if (!validateVoiceState(event)) {
            return;
        }
        
        // Получаем запрос - он может быть URL, названием трека или названием радиостанции
        String query = event.getOption("query").getAsString();
        
        // Подключаемся к голосовому каналу
        connectToVoiceChannel(event);
        
        // Определяем тип источника (URL, трек или радиостанция)
        String searchQuery = determineSearchQuery(query);
        
        // Отвечаем на команду и сразу удаляем ответ
        event.deferReply(true).queue(response -> {
            // Воспроизводим музыку
            PlayerManager.getInstance().play(
                event.getChannel().asTextChannel(),
                searchQuery
            );
            
            // Удаляем наш ответ сразу
            response.deleteOriginal().queue();
        });
    }
    
    /**
     * Определяет поисковый запрос на основе введенного текста
     * @param query Текст запроса или URL
     * @return Подготовленный запрос для LavaPlayer
     */
    private String determineSearchQuery(String query) {
        // Удаляем символ @ в начале ссылки (если он есть)
        if (query.startsWith("@")) {
            query = query.substring(1);
        }
        
        // Если это URL, определяем его тип
        if (query.startsWith("http") || query.startsWith("www")) {
            // Проверяем, корректен ли URL для Twitch
            if (TWITCH_URL_PATTERN.matcher(query).matches()) {
                // Убедимся, что URL имеет схему https://
                if (!query.startsWith("http")) {
                    query = "https://" + query;
                }
                
                return query; // Прямая ссылка на Twitch
            } else if (YOUTUBE_URL_PATTERN.matcher(query).matches()) {
                return query; // Прямая ссылка на YouTube
            } else if (BANDCAMP_URL_PATTERN.matcher(query).matches()) {
                return query; // Прямая ссылка на Bandcamp
            } else {
                return query; // Любая другая ссылка
            }
        } else {
            // Проверяем, есть ли в списке радиостанций станция с таким именем
            ServerRules guildConfig = jsonHandler.read(null); // Используем null для получения глобальной конфигурации
            RadioStation station = findStation(guildConfig.getRadioStations(), query);
            
            if (station != null) {
                return station.getUrl(); // Если нашли радиостанцию, возвращаем её URL
            }
            
            // Если это не URL и не имя радиостанции, считаем поисковым запросом YouTube
            return "ytsearch:" + query;
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Метод оставлен для обратной совместимости, но не используется
    }

    private boolean validateVoiceState(IReplyCallback event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("❌ Вы должны находиться в голосовом канале")
                .setEphemeral(true)
                .queue(response -> {
                    response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS);
                });
            return false;
        }
        return true;
    }

    private void connectToVoiceChannel(IReplyCallback event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(member.getVoiceState().getChannel());
        }
    }
    
    /**
     * Поиск радиостанции по имени
     */
    private RadioStation findStation(List<RadioStation> stations, String name) {
        return stations.stream()
                .filter(station -> station.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}