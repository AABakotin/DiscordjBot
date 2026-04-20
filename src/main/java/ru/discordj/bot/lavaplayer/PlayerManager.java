package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.bandcamp.BandcampAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.local.LocalAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.vimeo.VimeoAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.AndroidWithThumbnail;
import dev.lavalink.youtube.clients.WebWithThumbnail;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private static PlayerManager instance;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    private PlayerManager() {
        musicManagers = new HashMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();

        // YouTube через InnerTube
        YoutubeAudioSourceManager yt = new YoutubeAudioSourceManager(
                true,
                new AndroidWithThumbnail(),
                new WebWithThumbnail()
        );
        audioPlayerManager.registerSourceManager(yt);

        // Остальные источники
        audioPlayerManager.registerSourceManager(new TwitchStreamAudioSourceManager());
        audioPlayerManager.registerSourceManager(new HttpAudioSourceManager());
        audioPlayerManager.registerSourceManager(new BandcampAudioSourceManager());
        audioPlayerManager.registerSourceManager(new VimeoAudioSourceManager());
        audioPlayerManager.registerSourceManager(SoundCloudAudioSourceManager.createDefault());
        audioPlayerManager.registerSourceManager(new LocalAudioSourceManager());

        // Настройки таймаутов
        System.setProperty("http.connection.timeout", "30000");
        System.setProperty("http.socket.timeout", "30000");
    }

    public static PlayerManager getInstance() {
        if (instance == null) instance = new PlayerManager();
        return instance;
    }

    public GuildMusicManager getGuildMusicManager(Guild guild) {
        return musicManagers.computeIfAbsent(guild.getIdLong(), id -> {
            GuildMusicManager gmm = new GuildMusicManager(audioPlayerManager, guild);
            guild.getAudioManager().setSendingHandler(gmm.getSendHandler());
            return gmm;
        });
    }

    public void play(TextChannel channel, String trackUrl) {
        GuildMusicManager manager = getGuildMusicManager(channel.getGuild());
        audioPlayerManager.loadItemOrdered(manager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                manager.getTrackScheduler().queue(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                if (!playlist.getTracks().isEmpty()) {
                    manager.getTrackScheduler().queue(playlist.getTracks().get(0));
                } else {
                    channel.sendMessage("Ошибка: Плейлист пуст")
                            .queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
                }
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Ошибка: Ничего не найдено по запросу " + trackUrl)
                        .queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Ошибка загрузки: " + exception.getMessage())
                        .queue(msg -> msg.delete().queueAfter(30, TimeUnit.SECONDS));
            }
        });
    }

    public void searchAndPlay(TextChannel channel, String query) {
        play(channel, "ytsearch:" + query);
    }

    public void removeGuildMusicManager(Guild guild) {
        GuildMusicManager manager = musicManagers.remove(guild.getIdLong());
        if (manager != null) manager.getPlayer().destroy();
    }

    public AudioPlayerManager getAudioPlayerManager() {
        return audioPlayerManager;
    }
}