package ru.discordj.bot.utility.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения радиостанций конкретной гильдии.
 * Используется для сериализации/десериализации списка радиостанций в JSON формат.
 */
public class GuildRadioStations {
    
    /** Список радиостанций гильдии */
    private List<RadioStation> stations = new ArrayList<>();
    
    /**
     * Создает пустой объект с радиостанциями гильдии.
     */
    public GuildRadioStations() {
        this.stations = new ArrayList<>();
    }
    
    /**
     * Возвращает список радиостанций гильдии.
     *
     * @return список радиостанций
     */
    public List<RadioStation> getStations() {
        return stations;
    }
    
    /**
     * Устанавливает список радиостанций гильдии.
     *
     * @param stations новый список радиостанций
     */
    public void setStations(List<RadioStation> stations) {
        this.stations = stations;
    }
    
    /**
     * Добавляет радиостанцию в список.
     *
     * @param station радиостанция для добавления
     */
    public void addStation(RadioStation station) {
        this.stations.add(station);
    }
    
    /**
     * Удаляет радиостанцию из списка по имени.
     *
     * @param stationName имя радиостанции для удаления
     * @return true, если радиостанция была удалена, иначе false
     */
    public boolean removeStation(String stationName) {
        return stations.removeIf(station -> station.getName().equals(stationName));
    }
    
    /**
     * Проверяет, существует ли радиостанция с указанным именем.
     *
     * @param stationName имя радиостанции для проверки
     * @return true, если радиостанция существует, иначе false
     */
    public boolean hasStation(String stationName) {
        return stations.stream().anyMatch(station -> station.getName().equals(stationName));
    }
    
    /**
     * Возвращает радиостанцию по имени.
     *
     * @param stationName имя радиостанции
     * @return радиостанция или null, если не найдена
     */
    public RadioStation getStation(String stationName) {
        return stations.stream()
                .filter(station -> station.getName().equals(stationName))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Возвращает количество радиостанций в списке.
     *
     * @return количество радиостанций
     */
    public int getStationsCount() {
        return stations.size();
    }
} 