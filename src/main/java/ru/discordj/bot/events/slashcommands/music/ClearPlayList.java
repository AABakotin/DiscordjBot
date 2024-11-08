package ru.discordj.bot.events.slashcommands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import ru.discordj.bot.embed.EmbedCreation;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.GuildMusicManager;
import ru.discordj.bot.lavaplayer.PlayerManager;

import java.util.ArrayList;
import java.util.List;

public class ClearPlayList implements ICommand {
    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getDescription() {
        return "Clear play list";
    }

    @Override
    public List<OptionData> getOptions() {
        return null;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel").setEphemeral(true).queue();
            return;
        }

        Member self = event.getGuild().getSelfMember();
        GuildVoiceState selfVoiceState = self.getVoiceState();

        if (!selfVoiceState.inAudioChannel()) {
            event.reply("I am not in an audio channel").setEphemeral(true).queue();
            return;
        }

        if (selfVoiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("You are not in the same channel as me").setEphemeral(true).queue();
            return;
        }

        GuildMusicManager guildMusicManager = PlayerManager.get().getGuildMusicManager(event.getGuild());
        List<AudioTrack> queue = new ArrayList<>(guildMusicManager.getTrackScheduler().getQueue());
        if (queue.isEmpty()) {
            event.reply("Queue is empty").setEphemeral(true).queue();
        } else {
            guildMusicManager.getTrackScheduler().getQueue().clear();
            event.reply("The playlist is now clean").queue();
            EmbedCreation.get().playListEmbed(event.getChannel().asTextChannel());

        }
    }
}
