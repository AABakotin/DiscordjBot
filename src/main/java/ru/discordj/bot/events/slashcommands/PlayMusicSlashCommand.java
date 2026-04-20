package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.RadioStation;
import ru.discordj.bot.utility.pojo.ServerRules;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class PlayMusicSlashCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(PlayMusicSlashCommand.class);
    private final JsonParse jsonHandler = JsonParse.getInstance();
    private static final Pattern TWITCH_URL_PATTERN = Pattern.compile("(?:https?://)?(?:www\\.)?twitch\\.tv.*");
    private static final Pattern HTTP_PATTERN = Pattern.compile("^(https?://|www\\.).*");

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Воспроизведение музыки, радиостанций или поиск трека";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "query", "URL, название радиостанции или поисковый запрос", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (!validateVoiceState(event)) return;

        String query = event.getOption("query").getAsString().trim();
        if (query.isEmpty()) {
            event.reply("❌ Запрос не может быть пустым").setEphemeral(true).queue(r -> r.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        String playUrl = determinePlayUrl(event, query);
        if (playUrl == null) return;

        connectToVoiceChannel(event);

        event.deferReply(true).queue(hook -> {
            PlayerManager.getInstance().play(event.getChannel().asTextChannel(), playUrl);
            hook.deleteOriginal().queue();
        });
    }

    private String determinePlayUrl(SlashCommandInteractionEvent event, String query) {
        if (query.startsWith("@")) query = query.substring(1);
        if (HTTP_PATTERN.matcher(query).matches()) {
            if (TWITCH_URL_PATTERN.matcher(query).matches() && !query.startsWith("http"))
                query = "https://" + query;
            return query;
        }

        ServerRules guildConfig = jsonHandler.read(event.getGuild());
        if (guildConfig != null && guildConfig.getRadioStations() != null) {
            RadioStation station = findStation(guildConfig.getRadioStations(), query);
            if (station != null) return station.getUrl();
        }
        return "ytsearch:" + query;
    }

    private RadioStation findStation(List<RadioStation> stations, String name) {
        return stations.stream().filter(s -> s.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private boolean validateVoiceState(SlashCommandInteractionEvent event) {
        Member member = event.getMember();
        if (member == null) return false;
        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.reply("❌ Вы должны находиться в голосовом канале").setEphemeral(true).queue(r -> r.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return false;
        }
        return true;
    }

    private void connectToVoiceChannel(SlashCommandInteractionEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (!audioManager.isConnected()) {
            Member member = event.getMember();
            if (member != null && member.getVoiceState() != null)
                audioManager.openAudioConnection(member.getVoiceState().getChannel());
        }
    }

    // Этот метод нужен для CommandManager (onMessageReceived)
    public java.util.Map<String, ru.discordj.bot.utility.MessageCollector> getActiveCollectors() {
        return new java.util.HashMap<>();
    }

}