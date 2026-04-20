package ru.discordj.bot.lavaplayer;


import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.util.List;

public record TrackSelectionData(List<AudioTrack> tracks, String messageId) {}