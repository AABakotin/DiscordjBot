package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.RadioStation;
import ru.discordj.bot.utility.pojo.ServerRules;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.EmbedBuilder;

/**
 * Обработчик slash-команды для управления радиостанциями.
 * Позволяет просматривать, добавлять, удалять радиостанции и останавливать воспроизведение.
 */
public class RadioControlSlashCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(RadioControlSlashCommand.class);
    private final JsonParse jsonHandler = JsonParse.getInstance();

    @Override
    public String getName() {
        return "radio";
    }

    @Override
    public String getDescription() {
        return "Управление радиостанциями";
    }

    @Override
    public List<OptionData> getOptions() {
        // Не используем опции на верхнем уровне команды
        return new ArrayList<>();
    }
    
    @Override
    public List<SubcommandData> getSubcommands() {
        List<SubcommandData> subcommands = new ArrayList<>();
        
        // Подкоманда для остановки воспроизведения
        subcommands.add(new SubcommandData("stop", "Остановить воспроизведение"));
        
        // Подкоманда для отображения списка радиостанций
        subcommands.add(new SubcommandData("list", "Показать список доступных радиостанций"));
        
        // Подкоманда для добавления радиостанции
        SubcommandData addRadio = new SubcommandData("add", "Добавить радиостанцию");
        addRadio.addOption(OptionType.STRING, "name", "Название радиостанции", true);
        addRadio.addOption(OptionType.STRING, "url", "URL радиостанции", true);
        subcommands.add(addRadio);
        
        // Подкоманда для удаления радиостанции
        SubcommandData removeRadio = new SubcommandData("remove", "Удалить радиостанцию");
        removeRadio.addOption(OptionType.STRING, "name", "Название радиостанции", true);
        subcommands.add(removeRadio);
        
        // Подкоманда для воспроизведения радиостанции
        SubcommandData playRadio = new SubcommandData("play", "Воспроизвести радиостанцию");
        playRadio.addOption(OptionType.STRING, "name", "Название радиостанции", true);
        subcommands.add(playRadio);
        
        return subcommands;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Получаем имя подкоманды
        String subcommand = event.getSubcommandName();
        
        if (subcommand == null) {
            event.reply("Пожалуйста, укажите подкоманду")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }
        
        switch (subcommand) {
            case "stop":
                handleStop(event);
                break;
            case "list":
                handleRadioList(event);
                break;
            case "add":
                handleRadioAdd(event);
                break;
            case "remove":
                handleRadioRemove(event);
                break;
            case "play":
                handleRadioPlay(event);
                break;
            default:
                event.reply("Неизвестная подкоманда: " + subcommand)
                    .setEphemeral(true)
                    .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
        }
    }
    
    /**
     * Обработчик команды остановки воспроизведения
     */
    private void handleStop(SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Эта команда доступна только на серверах Discord")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        AudioManager audioManager = guild.getAudioManager();
        if (audioManager.isConnected()) {
            audioManager.closeAudioConnection();
            event.reply("✅ Воспроизведение остановлено")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
        } else {
            event.reply("❌ Бот не подключен к голосовому каналу")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
        }
    }
    
    /**
     * Обработчик команды отображения списка радиостанций
     */
    private void handleRadioList(SlashCommandInteractionEvent event) {
        ServerRules guildConfig = jsonHandler.read(event.getGuild());
        List<RadioStation> stations = guildConfig.getRadioStations();
        
        if (stations.isEmpty()) {
            event.reply("Список радиостанций пуст. Используйте `/radio add` для добавления радиостанций.")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }
        
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("Список радиостанций")
            .setColor(Color.BLUE)
            .setDescription("Доступные радиостанции на этом сервере:")
            .setFooter("Используйте /radio play name:название для воспроизведения");
        
        for (RadioStation station : stations) {
            embed.addField(station.getName(), "URL: " + station.getUrl(), false);
        }
        
        event.replyEmbeds(embed.build())
            .setEphemeral(true)
            .queue(response -> response.deleteOriginal().queueAfter(60, TimeUnit.SECONDS));
    }
    
    /**
     * Обработчик команды добавления радиостанции
     */
    private void handleRadioAdd(SlashCommandInteractionEvent event) {
        String stationName = event.getOption("name").getAsString();
        String stationUrl = event.getOption("url").getAsString();
        
        ServerRules guildConfig = jsonHandler.read(event.getGuild());
        
        // Проверяем, существует ли уже радиостанция с таким именем
        if (findStation(guildConfig.getRadioStations(), stationName) != null) {
            event.reply("❌ Радиостанция с таким именем уже существует")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }
        
        // Создаем новую радиостанцию
        RadioStation newStation = new RadioStation(stationName, stationUrl);
        
        // Добавляем радиостанцию в конфигурацию
        guildConfig.getRadioStations().add(newStation);
        
        // Сохраняем обновленную конфигурацию
        jsonHandler.write(event.getGuild(), guildConfig);
        
        event.reply("✅ Радиостанция \"" + stationName + "\" успешно добавлена!")
            .setEphemeral(true)
            .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
    }
    
    /**
     * Обработчик команды удаления радиостанции
     */
    private void handleRadioRemove(SlashCommandInteractionEvent event) {
        String name = event.getOption("name").getAsString();
        
        if (name == null || name.isEmpty()) {
            event.reply("❌ Необходимо указать название радиостанции")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("❌ Эта команда доступна только на серверах Discord")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        ServerRules guildConfig = jsonHandler.read(guild);
        if (guildConfig.getRadioStations().removeIf(station -> station.getName().equals(name))) {
            jsonHandler.write(guild, guildConfig);
            event.reply("✅ Радиостанция '" + name + "' успешно удалена")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
        } else {
            event.reply("❌ Радиостанция '" + name + "' не найдена")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
        }
    }
    
    /**
     * Обработчик команды воспроизведения радиостанции
     */
    private void handleRadioPlay(SlashCommandInteractionEvent event) {
        String stationName = event.getOption("name").getAsString();
        ServerRules guildConfig = jsonHandler.read(event.getGuild());
        RadioStation radioStation = findStation(guildConfig.getRadioStations(), stationName);

        if (radioStation == null) {
            event.reply("❌ Не удалось найти радиостанцию")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }

        // Проверяем, что пользователь находится в голосовом канале
        if (event.getMember().getVoiceState() == null || event.getMember().getVoiceState().getChannel() == null) {
            event.reply("❌ Вы должны быть в голосовом канале!")
                .setEphemeral(true)
                .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
            return;
        }
        
        // Подключаемся к голосовому каналу пользователя
        event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        
        // Отвечаем на команду и направляем на команду play
        event.reply("Для воспроизведения радиостанции используйте команду `/play " + stationName + "`")
            .setEphemeral(true)
            .queue(response -> response.deleteOriginal().queueAfter(30, TimeUnit.SECONDS));
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

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Метод оставлен пустым, так как не используется
    }
} 