package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.utility.MessageCollector;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Обработчик slash-команды для воспроизведения радиостанций.
 * Позволяет пользователю выбрать радиостанцию из предоставленного списка.
 */
public class RadioSlashCommand implements ICommand {
    
    // Словарь радиостанций (название и URL-поток)
    private static final Map<String, String> RADIO_STATIONS = new LinkedHashMap<>();
    
    static {
        // Популярные российские радиостанции
        RADIO_STATIONS.put("Европа Плюс", "http://ep128.hostingradio.ru:8030/ep128");
        RADIO_STATIONS.put("Русское Радио", "http://rusradio.hostingradio.ru/rusradio96.aacp");
        RADIO_STATIONS.put("Радио Energy", "http://ic7.101.ru:8000/v4_1");
        RADIO_STATIONS.put("Хит FM", "http://hitfm.hostingradio.ru/hitfm96.aacp");
        RADIO_STATIONS.put("Ретро FM", "http://retro.hostingradio.ru:8043/retro256.mp3");
        
        // Radio Record - официальные потоки
        RADIO_STATIONS.put("Рекорд", "https://radiorecord.hostingradio.ru/rr96.aacp");
        RADIO_STATIONS.put("Russian Mix", "https://radiorecord.hostingradio.ru/rus96.aacp");
        RADIO_STATIONS.put("Deep", "https://radiorecord.hostingradio.ru/deep96.aacp");
        RADIO_STATIONS.put("Techno", "https://radiorecord.hostingradio.ru/techno96.aacp");
        RADIO_STATIONS.put("Trap", "https://radiorecord.hostingradio.ru/trap96.aacp");
        RADIO_STATIONS.put("Dubstep", "https://radiorecord.hostingradio.ru/dub96.aacp");
        RADIO_STATIONS.put("Hardstyle", "https://radiorecord.hostingradio.ru/teo96.aacp");
        RADIO_STATIONS.put("Breaks", "https://radiorecord.hostingradio.ru/brks96.aacp");
        RADIO_STATIONS.put("Супердискотека 90-х", "https://radiorecord.hostingradio.ru/sd9096.aacp");
        RADIO_STATIONS.put("Гоп FM", "https://radiorecord.hostingradio.ru/gop96.aacp");
        RADIO_STATIONS.put("Руки Вверх!", "https://radiorecord.hostingradio.ru/rv96.aacp");
        RADIO_STATIONS.put("Hypnotic", "https://radiorecord.hostingradio.ru/hypno96.aacp");
        RADIO_STATIONS.put("Рок", "https://radiorecord.hostingradio.ru/rock96.aacp");
        RADIO_STATIONS.put("Chill-Out", "https://radiorecord.hostingradio.ru/chil96.aacp");
        RADIO_STATIONS.put("Synthwave", "https://radiorecord.hostingradio.ru/synth96.aacp");
        
        // Другие станции
        RADIO_STATIONS.put("DFM", "http://dfm.hostingradio.ru/dfm96.aacp");
        RADIO_STATIONS.put("Радио MAXIMUM", "http://maximum.hostingradio.ru/maximum96.aacp");
        RADIO_STATIONS.put("Lofi Hip Hop", "http://hyades.shoutca.st:8043/stream");
        RADIO_STATIONS.put("Nightwave Plaza", "https://radio.plaza.one/mp3");
        RADIO_STATIONS.put("Радио Эрмитаж", "https://hermitage.hostingradio.ru/hermitage128.mp3");
    }

    @Override
    public String getName() {
        return "radio";
    }

    @Override
    public String getDescription() {
        return "Включить онлайн-радиостанцию";
    }

    @Override
    public List<OptionData> getOptions() {
        return Collections.emptyList(); // Параметры не требуются, выбор через меню
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Проверяем, находится ли пользователь в голосовом канале
        if (!validateVoiceState(event)) {
            return;
        }
        
        // Подключаемся к голосовому каналу
        connectToVoiceChannel(event);
        
        // Создаем выпадающее меню с радиостанциями
        StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("radio_select")
            .setPlaceholder("Выберите радиостанцию")
            .setMaxValues(1)
            .setMinValues(1);
            
        // Добавляем популярные российские радиостанции
        menuBuilder.addOption("Европа Плюс", RADIO_STATIONS.get("Европа Плюс"), "Популярная российская музыка, хиты");
        menuBuilder.addOption("Русское Радио", RADIO_STATIONS.get("Русское Радио"), "Российская популярная музыка");
        menuBuilder.addOption("Радио Energy", RADIO_STATIONS.get("Радио Energy"), "Зарубежные хиты, танцевальная музыка");
        menuBuilder.addOption("Хит FM", RADIO_STATIONS.get("Хит FM"), "Популярные хиты России и Запада");
        menuBuilder.addOption("Ретро FM", RADIO_STATIONS.get("Ретро FM"), "Хиты 70-х, 80-х, 90-х годов");
        
        // Добавляем Radio Record
        menuBuilder.addOption("Радио Рекорд", RADIO_STATIONS.get("Рекорд"), "Главный танцевальный радиоканал России");
        menuBuilder.addOption("Record: Russian Mix", RADIO_STATIONS.get("Russian Mix"), "Русские хиты в танцевальной обработке");
        menuBuilder.addOption("Record: Deep", RADIO_STATIONS.get("Deep"), "Deep House музыка");
        menuBuilder.addOption("Record: Techno", RADIO_STATIONS.get("Techno"), "Техно музыка, минимал, техно-хаус");
        menuBuilder.addOption("Record: Trap", RADIO_STATIONS.get("Trap"), "Трап музыка и электро-хип-хоп");
        menuBuilder.addOption("Record: Dubstep", RADIO_STATIONS.get("Dubstep"), "Дабстеп, бейс и электро");
        menuBuilder.addOption("Record: Hardstyle", RADIO_STATIONS.get("Hardstyle"), "Хардстайл и хардкор");
        menuBuilder.addOption("Record: Breaks", RADIO_STATIONS.get("Breaks"), "Брейкбит музыка");
        menuBuilder.addOption("Record: Супердискотека 90-х", RADIO_STATIONS.get("Супердискотека 90-х"), "Хиты 90-х в танцевальной обработке");
        menuBuilder.addOption("Record: Гоп FM", RADIO_STATIONS.get("Гоп FM"), "Русский шансон и блатная музыка");
        menuBuilder.addOption("Record: Руки Вверх!", RADIO_STATIONS.get("Руки Вверх!"), "Хиты группы 'Руки Вверх' и похожих исполнителей");
        menuBuilder.addOption("Record: Hypnotic", RADIO_STATIONS.get("Hypnotic"), "Гипнотический транс и психоделика");
        menuBuilder.addOption("Record: Рок", RADIO_STATIONS.get("Рок"), "Русский и зарубежный рок");
        menuBuilder.addOption("Record: Chill-Out", RADIO_STATIONS.get("Chill-Out"), "Расслабляющая музыка и лаунж");
        menuBuilder.addOption("Record: Synthwave", RADIO_STATIONS.get("Synthwave"), "Синтвейв, ретровейв и электроника в стиле 80-х");
        
        // Добавляем другие станции
        menuBuilder.addOption("DFM", RADIO_STATIONS.get("DFM"), "Танцевальная музыка, клубные хиты");
        menuBuilder.addOption("Радио MAXIMUM", RADIO_STATIONS.get("Радио MAXIMUM"), "Альтернативный рок, новая и классическая рок-музыка");
        menuBuilder.addOption("Lofi Hip Hop", RADIO_STATIONS.get("Lofi Hip Hop"), "Расслабляющий лоу-фай хип-хоп, идеально для работы/учебы");
        menuBuilder.addOption("Nightwave Plaza", RADIO_STATIONS.get("Nightwave Plaza"), "Вейпорвейв, синтвейв и ретро-электроника");
        menuBuilder.addOption("Радио Эрмитаж", RADIO_STATIONS.get("Радио Эрмитаж"), "Классическая музыка и джаз");
        
        // Отправляем меню
        event.reply("Выберите радиостанцию для прослушивания:")
            .addComponents(ActionRow.of(menuBuilder.build()))
            .queue();
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (event.getComponentId().equals("radio_select")) {
            String selectedUrl = event.getValues().get(0);
            String stationName = findStationNameByUrl(selectedUrl);
            
            // Удаляем сообщение с меню выбора
            event.getMessage().delete().queue();
            
            // Отправляем временное сообщение о воспроизведении (через 3 секунды удалим)
            event.reply("🎵 Воспроизвожу радиостанцию: **" + stationName + "**")
                .queue(response -> {
                    // Воспроизводим выбранную радиостанцию
                    PlayerManager.getInstance().play(
                        event.getChannel().asTextChannel(),
                        selectedUrl
                    );
                    
                    // Удаляем сообщение об успешном воспроизведении через 3 секунды
                    response.deleteOriginal().queueAfter(3, TimeUnit.SECONDS);
                });
        }
    }
    
    private String findStationNameByUrl(String url) {
        for (Map.Entry<String, String> station : RADIO_STATIONS.entrySet()) {
            if (station.getValue().equals(url)) {
                return station.getKey();
            }
        }
        return "Радиостанция";
    }

    private boolean validateVoiceState(IReplyCallback event) {
        Member member = event.getMember();
        GuildVoiceState memberVoiceState = member.getVoiceState();

        if (!memberVoiceState.inAudioChannel()) {
            event.reply("Вы должны находиться в голосовом канале")
                .setEphemeral(true)
                .queue(response -> {
                    response.deleteOriginal().queueAfter(10, TimeUnit.SECONDS);
                });
            return false;
        }
        return true;
    }

    private void connectToVoiceChannel(IReplyCallback event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(member.getVoiceState().getChannel());
        }
    }
} 