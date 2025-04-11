package ru.discordj.bot.utility.pojo;

/**
 * Класс для хранения информации о радиостанции.
 * Используется для сериализации/десериализации в JSON формат.
 */
public class RadioStation {
    
    /** Название радиостанции */
    private String name;
    
    /** URL потока радиостанции */
    private String url;
    
    /**
     * Создает пустой объект радиостанции.
     */
    public RadioStation() {
        // Пустой конструктор для Jackson
    }
    
    /**
     * Создает объект радиостанции с заданными параметрами.
     *
     * @param name название радиостанции
     * @param url URL потока радиостанции
     */
    public RadioStation(String name, String url) {
        this.name = name;
        this.url = url;
    }
    
    /**
     * Возвращает название радиостанции.
     *
     * @return название радиостанции
     */
    public String getName() {
        return name;
    }
    
    /**
     * Устанавливает название радиостанции.
     *
     * @param name новое название радиостанции
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Возвращает URL потока радиостанции.
     *
     * @return URL потока радиостанции
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Устанавливает URL потока радиостанции.
     *
     * @param url новый URL потока радиостанции
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public String toString() {
        return "RadioStation{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
} 