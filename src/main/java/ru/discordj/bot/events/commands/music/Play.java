package ru.discordj.bot.events.commands.music;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.embed.EmbedCreation;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.events.lavaplayer.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Play implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Play.class);

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Will play a song";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Name of the song to play", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();
        final GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
        final AudioManager audioManager = event.getGuild().getAudioManager();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel").setEphemeral(true).queue();
            return;
        }
        if (!voiceState.inAudioChannel()) {
            audioManager.openAudioConnection(memberVoiceState.getChannel());
        } else if (voiceState.getChannel() != memberVoiceState.getChannel()) {
            event.reply("You need to be in the same channel as me").setEphemeral(true).queue();
            return;
        }
        String link = event.getOption("name").getAsString();

        if (!isUrl(link)) {
            link = "ytsearch:" + link;
        }

        PlayerManager.get().play(event.getChannel().asTextChannel(), link);
        event.reply("▶️ " + " Adding to play list:").queue();

        logger.info("Playing music for " + member.getUser().getName());
    }

    private boolean isUrl(String link) {
        try {
            new URI(link);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}