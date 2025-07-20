package ru.discordj.bot.service;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import java.util.List;

public class VoiceChannelService {
    public boolean isBotAlone(VoiceChannel voiceChannel) {
        List<Member> membersInChannel = voiceChannel.getMembers();
        for (Member member : membersInChannel) {
            if (!member.getUser().isBot()) {
                return false;
            }
        }
        return true;
    }
} 