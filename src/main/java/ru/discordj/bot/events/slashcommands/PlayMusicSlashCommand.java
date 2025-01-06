package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.PlayerManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * Обработчик slash-команды для воспроизведения музыки.
 * Позволяет добавлять треки в очередь воспроизведения по URL или поисковому запросу.
 * Поддерживает автоматическое подключение к голосовому каналу.
 */
@Component
public class PlayMusicSlashCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(PlayMusicSlashCommand.class);

    private final EmbedFactory embedFactory;

    @Autowired
    public PlayMusicSlashCommand(EmbedFactory embedFactory) {
        this.embedFactory = embedFactory;
    }

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Play a song with given name or URL";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "name", "Name of song or URL", true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!validateVoiceState(event)) {
            return;
        }

        String name = event.getOption("name").getAsString();
        connectToVoiceChannel(event);
        playTrack(event, name);
    }

    /**
     * Проверяет корректность голосового состояния.
     */
    private boolean validateVoiceState(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("You need to be in a voice channel").setEphemeral(true).queue();
            return false;
        }

        return true;
    }

    /**
     * Подключается к голосовому каналу пользователя.
     */
    private void connectToVoiceChannel(SlashCommandInteractionEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(member.getVoiceState().getChannel());
        }
    }

    /**
     * Воспроизводит трек или добавляет его в очередь.
     */
    private void playTrack(SlashCommandInteractionEvent event, String name) {
        try {
            // Сначала подтверждаем взаимодействие
            event.deferReply().setEphemeral(true).queue();
            
            // Определяем источник по префиксу
            String prefix = "";
            if (!name.startsWith("http")) {
                if (name.startsWith("sc:")) {
                    prefix = "scsearch:";
                    name = name.substring(3);
                } else if (name.startsWith("sp:")) {
                    prefix = "spsearch:";
                    name = name.substring(3);
                } else {
                    prefix = "ytsearch:";
                }
            }
            
            PlayerManager.getInstance().play(
                event.getChannel().asTextChannel(),
                prefix + name
            );
                
            // Обновляем плеер и удаляем ответ
            embedFactory.createMusicEmbed()
                .updatePlayerMessage(event.getChannel().asTextChannel(), null);
            event.getHook().deleteOriginal().queue();
                
            logger.info("Added track to queue: {}", name);
        } catch (Exception e) {
            logger.error("Failed to play track: {}", e.getMessage());
            event.getHook().sendMessage("Failed to play track: " + e.getMessage())
                .setEphemeral(true)
                .queue();
        }
    }

    public CommandData getCommandData() {
        return Commands.slash("play", "Воспроизвести музыку")
            .addOption(OptionType.STRING, "url", "URL трека или плейлиста", true);
    }
}