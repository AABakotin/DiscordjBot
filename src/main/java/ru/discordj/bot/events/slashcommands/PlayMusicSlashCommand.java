package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.utility.MessageCollector;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Обработчик slash-команды для воспроизведения музыки.
 * Позволяет добавлять треки в очередь воспроизведения по URL или поисковому запросу.
 * Поддерживает автоматическое подключение к голосовому каналу.
 */
public class PlayMusicSlashCommand implements ICommand {
    private final Map<String, MessageCollector> activeCollectors = new HashMap<>();
    
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
        return "Воспроизвести музыку по ссылке или поисковому запросу";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(
            OptionType.STRING, 
            "query", 
            "URL-ссылка или название трека для поиска", 
            true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Проверяем, находится ли пользователь в голосовом канале
        if (!validateVoiceState(event)) {
            return;
        }
        
        // Получаем поисковый запрос или URL, с проверкой на null
        var queryOption = event.getOption("query");
        if (queryOption == null) {
            event.reply("Пожалуйста, укажите URL-ссылку или поисковый запрос. Например: `/play https://youtube.com/...` или `/play название песни`")
                .setEphemeral(true)
                .queue(response -> {
                    response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                });
            return;
        }
        
        String query = queryOption.getAsString();
        
        // Подключаемся к голосовому каналу
        connectToVoiceChannel(event);
        
        // Определяем тип источника по URL или считаем поисковым запросом
        String searchQuery = determineSearchQuery(query);
        
        // Используем deferReply() для предотвращения сообщения "Приложение не отвечает"
        // и для длительной загрузки, особенно для Twitch-стримов
        if (searchQuery.contains("twitch.tv")) {
            event.deferReply(true).queue(response -> {
                // После завершения подготовки, воспроизводим музыку и удаляем сообщение
                PlayerManager.getInstance().play(
                    event.getChannel().asTextChannel(),
                    searchQuery
                );
                
                // Удаляем ответное сообщение через 2 секунды
                response.deleteOriginal().queueAfter(2, TimeUnit.SECONDS);
            });
        } else {
            // Для обычных запросов продолжаем без отложенного ответа
            PlayerManager.getInstance().play(
                event.getChannel().asTextChannel(),
                searchQuery
            );
        }
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
                System.out.println("Обнаружена ссылка на Twitch: " + query);
                
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
            // Если это не URL, считаем поисковым запросом YouTube
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
            event.reply("Вы должны находиться в голосовом канале")
                .setEphemeral(true)
                .queue(response -> {
                    response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
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
}