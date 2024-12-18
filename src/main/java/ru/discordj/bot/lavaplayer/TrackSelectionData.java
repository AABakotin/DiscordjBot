package ru.discordj.bot.lavaplayer;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.List;

public class TrackSelectionData {
    private final List<AudioTrack> tracks;
    private final String messageId;

    public TrackSelectionData(List<AudioTrack> tracks, String messageId) {
        this.tracks = tracks;
        this.messageId = messageId;
    }

    public List<AudioTrack> getTracks() {
        return tracks;
    }

    public String getMessageId() {
        return messageId;
    }
} 