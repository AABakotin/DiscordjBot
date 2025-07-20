package ru.discordj.bot.lavaplayer;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.managers.AudioManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerManagerTest {
    @Test
    void testCreateAndRemoveGuildMusicManager() {
        PlayerManager manager = PlayerManager.getInstance();
        Guild guild = mock(Guild.class);
        AudioManager audioManager = mock(AudioManager.class);
        when(guild.getIdLong()).thenReturn(123L);
        when(guild.getAudioManager()).thenReturn(audioManager);

        GuildMusicManager musicManager = manager.getGuildMusicManager(guild);
        assertNotNull(musicManager);

        manager.removeGuildMusicManager(guild);
        // Повторный вызов должен создать новый объект
        GuildMusicManager newManager = manager.getGuildMusicManager(guild);
        assertNotSame(musicManager, newManager);
    }
} 