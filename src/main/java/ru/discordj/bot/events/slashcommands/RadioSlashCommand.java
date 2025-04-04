package ru.discordj.bot.events.slashcommands;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.managers.AudioManager;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.lavaplayer.PlayerManager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Обработчик slash-команды для воспроизведения радиостанций.
 * Позволяет пользователю выбрать радиостанцию из предоставленного списка.
 */
public class RadioSlashCommand implements ICommand {
    
    // Словарь радиостанций (название и URL-поток)
    private static final Map<String, RadioStation> RADIO_STATIONS = new LinkedHashMap<>();
    private static final String RADIO_STATIONS_FILE = "json/radio_stations.json";
    private static final ObjectMapper mapper = new ObjectMapper();
    
    /**
     * Класс для хранения информации о радиостанции
     */
    public static class RadioStation {
        private String name;
        private String url;
        private String description;
        private String category;
        
        public RadioStation() {
            // Пустой конструктор для Jackson
        }
        
        public RadioStation(String name, String url, String description, String category) {
            this.name = name;
            this.url = url;
            this.description = description;
            this.category = category;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getCategory() {
            return category;
        }
        
        public void setCategory(String category) {
            this.category = category;
        }
    }
    
    /**
     * Инициализация списка радиостанций из JSON-файла или создание стандартного списка
     */
    static {
        loadStationsFromJson();
    }
    
    /**
     * Загружает список радиостанций из JSON-файла
     * Если файл не существует, создает его со стандартными станциями
     */
    private static void loadStationsFromJson() {
        try {
            File stationsFile = new File(getRadioStationsPath());
            
            // Если файл не существует, создаем его со стандартными станциями
            if (!stationsFile.exists()) {
                createDefaultRadioStationsFile();
            }
            
            // Загружаем радиостанции из JSON
            List<RadioStation> stations = mapper.readValue(stationsFile, 
                new TypeReference<List<RadioStation>>() {});
            
            // Очищаем и заполняем словарь
            RADIO_STATIONS.clear();
            for (RadioStation station : stations) {
                RADIO_STATIONS.put(station.getName(), station);
            }
            
            System.out.println("Загружено " + RADIO_STATIONS.size() + " радиостанций из " + RADIO_STATIONS_FILE);
        } catch (Exception e) {
            System.err.println("Ошибка загрузки радиостанций: " + e.getMessage());
            e.printStackTrace();
            
            // В случае ошибки загружаем стандартные станции
            createDefaultStations();
        }
    }
    
    /**
     * Создает файл radio_stations.json со стандартными радиостанциями
     */
    private static void createDefaultRadioStationsFile() throws IOException {
        createDefaultStations();
        saveStationsToJson();
    }
    
    /**
     * Создает стандартный список радиостанций
     */
    private static void createDefaultStations() {
        RADIO_STATIONS.clear();
        
        // Популярные российские радиостанции
        addStation("Европа Плюс", "http://ep128.hostingradio.ru:8030/ep128", 
                "Популярная российская музыка, хиты", "Популярные");
        addStation("Русское Радио", "http://rusradio.hostingradio.ru/rusradio96.aacp", 
                "Российская популярная музыка", "Популярные");
        addStation("Радио Energy", "https://pub0302.101.ru:8443/stream/air/aac/64/99", 
                "Зарубежные хиты, танцевальная музыка", "Популярные");
        addStation("Радио Романтика", "http://ic7.101.ru:8000/v4_1", 
                "Романтическая музыка", "Популярные");
        addStation("Хит FM", "http://hitfm.hostingradio.ru/hitfm96.aacp", 
                "Популярные хиты России и Запада", "Популярные");
        addStation("Ретро FM", "http://retro.hostingradio.ru:8043/retro256.mp3", 
                "Хиты 70-х, 80-х, 90-х годов", "Популярные");
        addStation("Relax FM Hi", "http://ic6.101.ru:8000/v13_1", 
                "Расслабляющая музыка для отдыха", "Популярные");
        
        // Radio Record - официальные потоки
        addStation("Рекорд", "https://radiorecord.hostingradio.ru/rr96.aacp", 
                "Главный танцевальный радиоканал России", "Record");
        addStation("Russian Mix", "https://radiorecord.hostingradio.ru/rus96.aacp", 
                "Русские хиты в танцевальной обработке", "Record");
        addStation("Deep", "https://radiorecord.hostingradio.ru/deep96.aacp", 
                "Deep House музыка", "Record");
        addStation("Techno", "https://radiorecord.hostingradio.ru/techno96.aacp", 
                "Техно музыка, минимал, техно-хаус", "Record");
        addStation("Trap", "https://radiorecord.hostingradio.ru/trap96.aacp", 
                "Трап музыка и электро-хип-хоп", "Record");
        addStation("Dubstep", "https://radiorecord.hostingradio.ru/dub96.aacp", 
                "Дабстеп, бейс и электро", "Record");
        addStation("Hardstyle", "https://radiorecord.hostingradio.ru/teo96.aacp", 
                "Хардстайл и хардкор", "Record");
        addStation("Breaks", "https://radiorecord.hostingradio.ru/brks96.aacp", 
                "Брейкбит музыка", "Record");
        addStation("Супердискотека 90-х", "https://radiorecord.hostingradio.ru/sd9096.aacp", 
                "Хиты 90-х в танцевальной обработке", "Record");
        addStation("Гоп FM", "https://radiorecord.hostingradio.ru/gop96.aacp", 
                "Русский шансон и блатная музыка", "Record");
        addStation("Руки Вверх!", "https://radiorecord.hostingradio.ru/rv96.aacp", 
                "Хиты группы 'Руки Вверх' и похожих исполнителей", "Record");
        addStation("Hypnotic", "https://radiorecord.hostingradio.ru/hypno96.aacp", 
                "Гипнотический транс и психоделика", "Record");
        addStation("Рок", "https://radiorecord.hostingradio.ru/rock96.aacp", 
                "Русский и зарубежный рок", "Record");
        addStation("Chill-Out", "https://radiorecord.hostingradio.ru/chil96.aacp", 
                "Расслабляющая музыка и лаунж", "Record");
        addStation("Synthwave", "https://radiorecord.hostingradio.ru/synth96.aacp", 
                "Синтвейв, ретровейв и электроника в стиле 80-х", "Record");
        
        // Другие станции
        addStation("DFM", "http://dfm.hostingradio.ru/dfm96.aacp", 
                "Танцевальная музыка, клубные хиты", "Другие");
        addStation("Радио MAXIMUM", "http://maximum.hostingradio.ru/maximum96.aacp", 
                "Альтернативный рок, новая и классическая рок-музыка", "Другие");
        addStation("Lofi Hip Hop", "http://hyades.shoutca.st:8043/stream", 
                "Расслабляющий лоу-фай хип-хоп, идеально для работы/учебы", "Другие");
        addStation("Nightwave Plaza", "https://radio.plaza.one/mp3", 
                "Вейпорвейв, синтвейв и ретро-электроника", "Другие");
    }
    
    /**
     * Добавляет радиостанцию в словарь
     */
    private static void addStation(String name, String url, String description, String category) {
        RADIO_STATIONS.put(name, new RadioStation(name, url, description, category));
    }
    
    /**
     * Сохраняет список радиостанций в JSON-файл
     */
    public static void saveStationsToJson() {
        try {
            File stationsFile = new File(getRadioStationsPath());
            
            // Создаем директорию, если её нет
            stationsFile.getParentFile().mkdirs();
            
            // Преобразуем Map в List для сохранения
            List<RadioStation> stations = new ArrayList<>(RADIO_STATIONS.values());
            
            // Сохраняем в JSON с отступами для читаемости
            mapper.writerWithDefaultPrettyPrinter()
                .writeValue(stationsFile, stations);
            
            System.out.println("Сохранено " + stations.size() + " радиостанций в " + RADIO_STATIONS_FILE);
        } catch (Exception e) {
            System.err.println("Ошибка сохранения радиостанций: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Получает путь к файлу radio_stations.json относительно директории запуска бота
     */
    private static String getRadioStationsPath() {
        String jarPath = new File(RadioSlashCommand.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath())
                .getParent();
        return jarPath + File.separator + RADIO_STATIONS_FILE;
    }
    
    /**
     * Обновляет URL радиостанции 
     */
    public static void updateRadioStationUrl(String stationName, String newUrl) {
        if (RADIO_STATIONS.containsKey(stationName)) {
            RadioStation station = RADIO_STATIONS.get(stationName);
            station.setUrl(newUrl);
            System.out.println("Обновлена ссылка для радиостанции: " + stationName);
            
            // Сохраняем изменения в файл
            saveStationsToJson();
        } else {
            System.err.println("Радиостанция не найдена: " + stationName);
        }
    }
    
    /**
     * Добавляет новую радиостанцию и сохраняет в JSON
     */
    public static void addRadioStation(String name, String url, String description, String category) {
        addStation(name, url, description, category);
        saveStationsToJson();
        System.out.println("Добавлена новая радиостанция: " + name);
    }
    
    /**
     * Удаляет радиостанцию и сохраняет изменения
     */
    public static void removeRadioStation(String name) {
        if (RADIO_STATIONS.remove(name) != null) {
            saveStationsToJson();
            System.out.println("Удалена радиостанция: " + name);
        } else {
            System.err.println("Радиостанция для удаления не найдена: " + name);
        }
    }
    
    /**
     * Перезагружает список радиостанций из JSON-файла
     */
    public static void reloadStations() {
        loadStationsFromJson();
    }
    
    /**
     * Возвращает количество радиостанций в списке
     * @return количество радиостанций
     */
    public static int getStationsCount() {
        return RADIO_STATIONS.size();
    }
    
    /**
     * Проверяет, существует ли радиостанция с указанным именем
     * @param stationName имя радиостанции для проверки
     * @return true если радиостанция существует, иначе false
     */
    public static boolean hasStation(String stationName) {
        return RADIO_STATIONS.containsKey(stationName);
    }
    
    /**
     * Возвращает список радиостанций, сгруппированных по категориям
     * @return Map, где ключ - название категории, значение - список радиостанций в этой категории
     */
    public static Map<String, List<RadioStation>> getStationsByCategory() {
        Map<String, List<RadioStation>> result = new HashMap<>();
        
        // Группируем станции по категориям
        for (RadioStation station : RADIO_STATIONS.values()) {
            String category = station.getCategory();
            
            // Если категории еще нет в результате, создаем для нее пустой список
            if (!result.containsKey(category)) {
                result.put(category, new ArrayList<>());
            }
            
            // Добавляем станцию в соответствующую категорию
            result.get(category).add(station);
        }
        
        return result;
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
        try {
            // Проверяем, находится ли пользователь в голосовом канале
            if (!validateVoiceState(event)) {
                return;
            }
            
            // Подключаемся к голосовому каналу
            connectToVoiceChannel(event);
            
            // Создаем меню с полным списком радиостанций (до 25 станций)
            StringSelectMenu.Builder menuBuilder = StringSelectMenu.create("radio_select")
                .setPlaceholder("Выберите радиостанцию")
                .setMaxValues(1)
                .setMinValues(1);
            
            // Добавляем радиостанции в меню (максимум 25)
            int count = 0;
            for (RadioStation station : RADIO_STATIONS.values()) {
                if (count < 25) {
                    menuBuilder.addOption(station.getName(), station.getUrl(), station.getDescription());
                    count++;
                } else {
                    break;
                }
            }
            
            // Отправляем меню с полным списком радиостанций
            event.reply("Выберите радиостанцию для прослушивания:")
                .addComponents(ActionRow.of(menuBuilder.build()))
                .queue(response -> {
                    System.out.println("Меню радиостанций успешно отправлено");
                }, error -> {
                    System.err.println("Ошибка при отправке меню: " + error.getMessage());
                    event.reply("❌ Произошла ошибка при загрузке списка радиостанций. Пожалуйста, повторите позже.")
                        .setEphemeral(true)
                        .queue();
                });
        } catch (Exception e) {
            System.err.println("Ошибка в execute: " + e.getMessage());
            e.printStackTrace();
            event.reply("❌ Произошла непредвиденная ошибка: " + e.getMessage())
                .setEphemeral(true)
                .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        try {
            if (event.getComponentId().equals("radio_select")) {
                String selectedUrl = event.getValues().get(0);
                String stationName = findStationNameByUrl(selectedUrl);
                
                System.out.println("Выбрана радиостанция: " + stationName + " с URL: " + selectedUrl);
                
                // Удаляем сообщение с меню выбора
                event.getMessage().delete().queue(success -> {
                    System.out.println("Сообщение с меню успешно удалено");
                }, error -> {
                    System.err.println("Ошибка при удалении сообщения: " + error.getMessage());
                });
                
                // Отправляем временное сообщение о воспроизведении (через 3 секунды удалим)
                event.reply("🎵 Воспроизвожу радиостанцию: **" + stationName + "**")
                    .queue(response -> {
                        try {
                            System.out.println("Начинаю воспроизведение радиостанции: " + stationName);
                            
                            // Воспроизводим выбранную радиостанцию
                            PlayerManager.getInstance().play(
                                event.getChannel().asTextChannel(),
                                selectedUrl
                            );
                            
                            System.out.println("Воспроизведение запущено успешно");
                            
                            // Удаляем сообщение об успешном воспроизведении через 3 секунды
                            response.deleteOriginal().queueAfter(3, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            System.err.println("Ошибка воспроизведения: " + e.getMessage());
                            e.printStackTrace();
                            
                            // В случае ошибки выводим сообщение об ошибке
                            response.editOriginal("❌ Ошибка воспроизведения: " + e.getMessage()).queue();
                        }
                    }, error -> {
                        System.err.println("Ошибка взаимодействия: " + error.getMessage());
                        error.printStackTrace();
                    });
            }
        } catch (Exception e) {
            System.err.println("Ошибка в onStringSelectInteraction: " + e.getMessage());
            e.printStackTrace();
            
            try {
                event.reply("❌ Произошла ошибка: " + e.getMessage())
                    .setEphemeral(true)
                    .queue();
            } catch (Exception ex) {
                System.err.println("Не удалось отправить сообщение об ошибке: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Находит название радиостанции по URL потока
     */
    private String findStationNameByUrl(String url) {
        for (Map.Entry<String, RadioStation> entry : RADIO_STATIONS.entrySet()) {
            if (entry.getValue().getUrl().equals(url)) {
                return entry.getKey();
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