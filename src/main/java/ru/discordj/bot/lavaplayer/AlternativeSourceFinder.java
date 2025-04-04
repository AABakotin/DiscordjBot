package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Класс для поиска музыки через альтернативные источники, когда основной источник (YouTube) не работает
 */
public class AlternativeSourceFinder {
    private static final Logger log = LoggerFactory.getLogger(AlternativeSourceFinder.class);
    
    /**
     * Пытается загрузить трек, используя разные источники последовательно
     * @param playerManager менеджер плеера
     * @param guildMusicManager музыкальный менеджер гильдии
     * @param textChannel текстовый канал для сообщений
     * @param query запрос или URL
     */
    public static void tryLoadWithAlternatives(
            AudioPlayerManager playerManager, 
            GuildMusicManager guildMusicManager,
            TextChannel textChannel,
            String query) {
        
        // Сначала пробуем с YouTube
        AtomicBoolean trackLoaded = new AtomicBoolean(false);
        
        playerManager.loadItemOrdered(guildMusicManager, query, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackLoaded.set(true);
                guildMusicManager.getTrackScheduler().queue(track);
                log.info("Трек загружен успешно: {}", track.getInfo().title);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                trackLoaded.set(true);
                if (!playlist.getTracks().isEmpty()) {
                    AudioTrack track = playlist.getTracks().get(0);
                    guildMusicManager.getTrackScheduler().queue(track);
                    log.info("Трек загружен из плейлиста: {}", track.getInfo().title);
                }
            }

            @Override
            public void noMatches() {
                if (!trackLoaded.get()) {
                    tryAlternativeSources(playerManager, guildMusicManager, textChannel, query);
                }
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                if (!trackLoaded.get()) {
                    log.warn("Ошибка при загрузке с основного источника: {}", exception.getMessage());
                    
                    if (exception.getCause() instanceof java.net.SocketTimeoutException 
                            || exception.getMessage().contains("timeout")) {
                        // Без отображения сообщения о пробе альтернативных источников
                        tryAlternativeSources(playerManager, guildMusicManager, textChannel, query);
                    } else {
                        textChannel.sendMessage("❌ Не удалось воспроизвести: " + exception.getMessage())
                            .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
                    }
                }
            }
        });
    }
    
    /**
     * Пробует загрузить трек через альтернативные источники
     */
    private static void tryAlternativeSources(
            AudioPlayerManager playerManager, 
            GuildMusicManager guildMusicManager,
            TextChannel textChannel,
            String query) {
        
        // Проверяем, является ли запрос URL-адресом
        if (!query.startsWith("http")) {
            trySearchWithSoundCloud(playerManager, guildMusicManager, textChannel, query);
        } else {
            // Если это URL, сообщаем об ошибке
            textChannel.sendMessage("❌ Не удалось загрузить по URL. Попробуйте другой источник или поисковый запрос.")
                .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
        }
    }
    
    /**
     * Пробует поиск через SoundCloud
     */
    private static void trySearchWithSoundCloud(
            AudioPlayerManager playerManager, 
            GuildMusicManager guildMusicManager,
            TextChannel textChannel,
            String query) {
            
        String searchQuery = extractSearchQuery(query);
        String soundCloudQuery = "scsearch:" + searchQuery;
        log.info("Пробуем SoundCloud с запросом: {}", soundCloudQuery);
        
        AtomicBoolean trackFound = new AtomicBoolean(false);
        
        playerManager.loadItemOrdered(guildMusicManager, soundCloudQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                trackFound.set(true);
                guildMusicManager.getTrackScheduler().queue(track);
                // Без уведомления об успешной загрузке
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (!playlist.getTracks().isEmpty()) {
                    trackFound.set(true);
                    AudioTrack track = playlist.getTracks().get(0);
                    guildMusicManager.getTrackScheduler().queue(track);
                    // Без уведомления об успешной загрузке
                }
            }

            @Override
            public void noMatches() {
                // Если на SoundCloud не найдено, пробуем Bandcamp
                trySearchWithBandcamp(playerManager, guildMusicManager, textChannel, searchQuery);
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.warn("Ошибка при загрузке с SoundCloud: {}", exception.getMessage());
                // Если на SoundCloud произошла ошибка, пробуем Bandcamp
                trySearchWithBandcamp(playerManager, guildMusicManager, textChannel, searchQuery);
            }
        });
    }
    
    /**
     * Пробует поиск через Bandcamp
     */
    private static void trySearchWithBandcamp(
            AudioPlayerManager playerManager, 
            GuildMusicManager guildMusicManager,
            TextChannel textChannel,
            String searchQuery) {
            
        String bandcampQuery = "bcsearch:" + searchQuery;
        log.info("Пробуем Bandcamp с запросом: {}", bandcampQuery);
        
        playerManager.loadItemOrdered(guildMusicManager, bandcampQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                guildMusicManager.getTrackScheduler().queue(track);
                // Без уведомления об успешной загрузке
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (!playlist.getTracks().isEmpty()) {
                    AudioTrack track = playlist.getTracks().get(0);
                    guildMusicManager.getTrackScheduler().queue(track);
                    // Без уведомления об успешной загрузке
                }
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("❌ Не удалось найти трек ни на YouTube, ни на SoundCloud, ни на Bandcamp")
                    .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                log.warn("Ошибка при загрузке с Bandcamp: {}", exception.getMessage());
                textChannel.sendMessage("❌ Не удалось найти трек. Попробуйте другой источник или прямую ссылку.")
                    .queue(msg -> msg.delete().queueAfter(10, TimeUnit.SECONDS));
            }
        });
    }
    
    /**
     * Извлекает поисковый запрос из строки запроса
     */
    private static String extractSearchQuery(String query) {
        if (query.startsWith("ytsearch:")) {
            return query.substring("ytsearch:".length());
        }
        return query;
    }
} 