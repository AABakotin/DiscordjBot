package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
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

/**
 * Slash-команда для воспроизведения радиостанции.
 */
public class RadioPlaySlashCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(RadioPlaySlashCommand.class);
    private final JsonParse jsonHandler = JsonParse.getInstance();
    
    @Override
    public String getName() {
        return "radio";
    }
    
    @Override
    public String getDescription() {
        return "Воспроизвести радиостанцию";
    }
    
    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Название радиостанции", true));
        return options;
    }
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            // Проверяем, находится ли пользователь в голосовом канале
            Member member = event.getMember();
            GuildVoiceState voiceState = member.getVoiceState();
            
            if (!voiceState.inAudioChannel()) {
                event.reply("❌ Вы должны находиться в голосовом канале!")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
            
            // Получаем название радиостанции
            String stationName = event.getOption("name").getAsString();
            
            // Получаем конфигурацию гильдии
            ServerRules guildConfig = jsonHandler.read(event.getGuild());
            
            // Ищем радиостанцию
            RadioStation station = findStation(guildConfig.getRadioStations(), stationName);
            
            if (station == null) {
                event.reply("❌ Радиостанция **" + stationName + "** не найдена! Проверьте список доступных станций с помощью команды `/radio_list`")
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
                return;
            }
            
            // Подключаемся к голосовому каналу
            AudioManager audioManager = event.getGuild().getAudioManager();
            if (!audioManager.isConnected()) {
                audioManager.openAudioConnection(voiceState.getChannel());
            }
            
            // Воспроизводим радиостанцию
            PlayerManager.getInstance().play(
                event.getChannel().asTextChannel(),
                station.getUrl()
            );
            
            // Отправляем пустой ответ и сразу удаляем его, чтобы скрыть сообщение о запуске
            event.reply("⠀") // Невидимый символ Unicode для "пустого" сообщения
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queue());
                
        } catch (Exception e) {
            logger.error("Ошибка при воспроизведении радио: {}", e.getMessage(), e);
            event.reply("❌ Произошла ошибка: " + e.getMessage())
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
        }
    }
    
    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Не используется
    }
    
    /**
     * Поиск радиостанции по имени
     */
    private RadioStation findStation(List<RadioStation> stations, String name) {
        return stations.stream()
                .filter(station -> station.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
} 