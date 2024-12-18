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

/**
 * Обработчик slash-команды для воспроизведения музыки.
 * Позволяет добавлять треки в очередь воспроизведения по URL или поисковому запросу.
 * Поддерживает автоматическое подключение к голосовому каналу.
 */
public class PlayMusicSlashCommands implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(PlayMusicSlashCommands.class);

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
            
            // Затем выполняем действия
            PlayerManager.getInstance()
                .play(
                    event.getChannel().asTextChannel(),
                    name.startsWith("http") ? name : "ytsearch:" + name
                );
                
            // Обновляем плеер и удаляем ответ
            EmbedFactory.getInstance().createMusicEmbed()
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
}