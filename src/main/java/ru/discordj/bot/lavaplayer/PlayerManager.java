package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.discordj.bot.embed.EmbedFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sedmelluq.discord.lavaplayer.player.AudioConfiguration;
import java.util.concurrent.TimeUnit;


public class PlayerManager {


    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();

        try {
            // Создаем и настраиваем YouTube API с увеличенными таймаутами
            YoutubeAudioSourceManager youtubeManager = new YoutubeAudioSourceManager();
            
            // Используем новый класс для настройки
            youtubeManager = YoutubeConfig.configure(youtubeManager);
            
            // Регистрация источников музыки
            audioPlayerManager.registerSourceManager(youtubeManager);  // YouTube
        } catch (Exception e) {
            System.out.println("Ошибка при настройке YouTube: " + e.getMessage());
            // В случае ошибки, создаем обычный YoutubeAudioSourceManager
            audioPlayerManager.registerSourceManager(new YoutubeAudioSourceManager());
        }
        
        // Настройка таймаутов для LavaPlayer
        System.setProperty("http.connection.timeout", "30000");  // 30 секунд
        System.setProperty("http.socket.timeout", "30000");      // 30 секунд
        
        // Регистрация остальных источников музыки
        audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());   // Bandcamp
        audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());      // Vimeo
        audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager()); // Twitch
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault()); // SoundCloud с настройками по умолчанию
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());       // HTTP URLs
        audioPlayerManager.registerSourceManager(new LocalAudioSourceManager());      // Local files

        // Настройка качества и буферизации
        audioPlayerManager.getConfiguration()
            .setFilterHotSwapEnabled(true);
        audioPlayerManager.getConfiguration()
            .setOpusEncodingQuality(10);
        audioPlayerManager.getConfiguration()
            .setResamplingQuality(AudioConfiguration.ResamplingQuality.HIGH);
    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }


    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(this.audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }


    public void play(TextChannel textChannel, String trackUrl) {
        GuildMusicManager musicManager = getGuildMusicManager(textChannel.getGuild());
        
        // Создаем сообщение плеера, если его еще нет
        if (musicManager.getTrackScheduler().getPlayerMessageId() == null) {
            textChannel.sendMessage(EmbedFactory.getInstance().createMusicEmbed()
                .createPlayerMessage(textChannel).build())
                .queue(message -> 
                    musicManager.getTrackScheduler().setPlayerMessage(textChannel, message.getId())
                );
        }
        
        // Используем AlternativeSourceFinder для автоматического выбора источника
        AlternativeSourceFinder.tryLoadWithAlternatives(audioPlayerManager, musicManager, textChannel, trackUrl);
    }

    public void searchAndPlay(TextChannel textChannel, String searchQuery) {
        final GuildMusicManager musicManager = getGuildMusicManager(textChannel.getGuild());

        audioPlayerManager.loadItemOrdered(musicManager, "ytsearch:" + searchQuery, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack track = playlist.getTracks().get(0);
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Ничего не найдено по запросу: " + searchQuery).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                String errorMessage;
                if (exception.getCause() instanceof java.net.SocketTimeoutException 
                    || (exception.getMessage() != null && exception.getMessage().contains("timeout"))) {
                    errorMessage = "Время ожидания ответа от YouTube истекло. Возможные причины:\n"
                        + "• Медленное интернет-соединение\n"
                        + "• YouTube временно блокирует запросы\n"
                        + "Попробуйте воспроизвести трек позже или использовать другой источник.";
                } else {
                    errorMessage = "Ошибка при загрузке: " + exception.getMessage();
                }
                
                textChannel.sendMessage(errorMessage)
                    .queue(message -> {
                        // Удаляем сообщение через 15 секунд
                        message.delete().queueAfter(15, TimeUnit.SECONDS);
                    });
            }
        });
    }

    // Метод для воспроизведения музыки с разных источников
    public void loadAndPlay(TextChannel channel, String trackUrl) {
        final GuildMusicManager musicManager = this.getGuildMusicManager(channel.getGuild());

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.getTrackScheduler().queue(track);
                String message = String.format("Добавлено в очередь: `%s` от `%s`", 
                    track.getInfo().title,
                    track.getInfo().author);
                channel.sendMessage(message).queue();
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                final List<AudioTrack> tracks = playlist.getTracks();
                if (!tracks.isEmpty()) {
                    musicManager.getTrackScheduler().queue(tracks.get(0));
                    String message = String.format("Добавлено в очередь: `%s` от `%s`",
                        tracks.get(0).getInfo().title,
                        tracks.get(0).getInfo().author);
                    channel.sendMessage(message).queue();
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Трек не найден: " + trackUrl)
                    .queue(message -> {
                        // Удаляем сообщение через 15 секунд
                        message.delete().queueAfter(15, TimeUnit.SECONDS);
                    });
            }

            @Override
            public void loadFailed(FriendlyException e) {
                String errorMessage;
                if (e.getCause() instanceof java.net.SocketTimeoutException 
                    || (e.getMessage() != null && e.getMessage().contains("timeout"))) {
                    errorMessage = "Время ожидания ответа от YouTube истекло. Возможные причины:\n"
                        + "• Медленное интернет-соединение\n"
                        + "• YouTube временно блокирует запросы\n"
                        + "Попробуйте воспроизвести трек позже или использовать другой источник.";
                } else {
                    errorMessage = "Не удалось воспроизвести: " + e.getMessage();
                }
                
                channel.sendMessage(errorMessage)
                    .queue(message -> {
                        // Удаляем сообщение через 15 секунд (увеличено время для чтения ошибки)
                        message.delete().queueAfter(15, TimeUnit.SECONDS);
                    });
            }
        });
    }
}