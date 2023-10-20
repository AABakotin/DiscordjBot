package ru.discordj.bot.events.lavaplayer;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.playback.MutableAudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;

public class AudioPlayerSendHandler implements AudioSendHandler {

    private final AudioPlayer player;
    private final Guild guild;
    private final ByteBuffer buffer;
    private final MutableAudioFrame frame;
    private int time;

    public AudioPlayerSendHandler(AudioPlayer player, Guild guild) {
        this.buffer = ByteBuffer.allocate(1024);
        this.frame = new MutableAudioFrame();
        this.player = player;
        this.guild = guild;
        frame.setBuffer(buffer);
    }

    @Override
    public boolean canProvide() {
        boolean canProvide = player.provide(frame);
        if (!canProvide) {
            time += 20;
            if (time >= 40000) {
                time = 0;
                guild.getAudioManager().closeAudioConnection();
            }
        } else {
            time = 0;
        }
        return canProvide;
    }

    @Nullable
    @Override
    public ByteBuffer provide20MsAudio() {
        return this.buffer.flip();
    }

    @Override
    public boolean isOpus() {
        return true;
    }
}