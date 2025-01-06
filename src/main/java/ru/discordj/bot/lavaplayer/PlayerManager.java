package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import ru.discordj.bot.embed.EmbedFactory;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.discordj.bot.utility.Spring;
import dev.lavalink.youtube.YoutubeAudioSourceManager;

@Component
public class PlayerManager implements ApplicationContextAware {

    private final EmbedFactory embedFactory;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    @Autowired
    public PlayerManager(EmbedFactory embedFactory) {
        this.embedFactory = embedFactory;
        this.musicManagers = new HashMap<>();
        this.audioPlayerManager = new DefaultAudioPlayerManager();
        
        // Регистрируем YouTube source manager
        YoutubeAudioSourceManager youtube = new dev.lavalink.youtube.YoutubeAudioSourceManager();
        audioPlayerManager.registerSourceManager(youtube);
        
        // Регистрируем остальные источники
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        
        // Настраиваем конфигурацию
        this.audioPlayerManager.getConfiguration().setFilterHotSwapEnabled(true);
    }

    public static PlayerManager getInstance() {
        return Spring.getBean(PlayerManager.class);
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), guildId -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(
                this.audioPlayerManager, 
                guild,
                this.embedFactory
            );
            guild.getAudioManager().setSendingHandler(guildMusicManager.getSendHandler());
            return guildMusicManager;
        });
    }

    public void play(TextChannel textChannel, String trackUrl) {
        final GuildMusicManager musicManager = getGuildMusicManager(textChannel.getGuild());
        
        // Создаем сообщение плеера, если его еще нет
        if (musicManager.getTrackScheduler().getPlayerMessageId() == null) {
            textChannel.sendMessage(embedFactory.createMusicEmbed()
                .createPlayerMessage(textChannel).build())
                .queue(message -> 
                    musicManager.getTrackScheduler().setPlayerMessage(textChannel, message.getId())
                );
        }

        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                // Берем только первый трек из плейлиста
                AudioTrack track = playlist.getTracks().get(0);
                musicManager.getTrackScheduler().queue(track);
            }

            @Override
            public void noMatches() {
                textChannel.sendMessage("Трек не найден: " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                textChannel.sendMessage("Ошибка загрузки: " + exception.getMessage()).queue();
            }
        });
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
                textChannel.sendMessage("Ошибка при загрузке: " + exception.getMessage()).queue();
            }
        });
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        // This method is required by ApplicationContextAware interface
    }
}