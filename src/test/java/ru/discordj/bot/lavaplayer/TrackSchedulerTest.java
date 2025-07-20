package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TrackSchedulerTest {
    private AudioPlayer player;
    private TrackScheduler scheduler;
    private AudioTrack track;

    @BeforeEach
    void setUp() {
        player = mock(AudioPlayer.class);
        scheduler = new TrackScheduler(player);
        track = mock(AudioTrack.class);
        when(track.makeClone()).thenReturn(track);
    }

    @Test
    void testQueueAndStop() {
        scheduler.queue(track);
        assertFalse(scheduler.getQueue().isEmpty());
        scheduler.stop();
        assertTrue(scheduler.getQueue().isEmpty());
    }

    @Test
    void testTogglePause() {
        when(player.isPaused()).thenReturn(false);
        scheduler.togglePause();
        verify(player).setPaused(true);
    }

    @Test
    void testRepeat() {
        scheduler.toggleRepeat();
        assertTrue(scheduler.isRepeat());
    }

    @Test
    void testRemoveTrack() {
        scheduler.queue(track);
        when(track.getInfo()).thenReturn(new AudioTrackInfoStub("test"));
        scheduler.removeTrack("test");
        assertTrue(scheduler.getQueue().isEmpty());
    }

    // Вспомогательный stub для AudioTrackInfo
    static class AudioTrackInfoStub extends com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo {
        public AudioTrackInfoStub(String title) {
            super(title, "author", 100, "id", false, "uri");
        }
    }
} 