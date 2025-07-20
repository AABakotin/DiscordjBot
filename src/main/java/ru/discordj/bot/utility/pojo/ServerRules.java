package ru.discordj.bot.utility.pojo;

import java.util.List;

import ru.discordj.bot.monitor.ServerMonitor;

import java.util.ArrayList;

/**
 * POJO класс, представляющий корневую конфигурацию бота Discord.
 * Используется для сериализации/десериализации настроек бота в JSON формат.
 *
 * 
 * @version 1.0
 */
public class ServerRules {
    
    /** Ссылка для приглашения бота на сервер */
    private String inviteLink;

    /** Список правил выдачи ролей */
    private List<Roles> roles = new ArrayList<>();

    /** ID канала мониторинга */
    private String monitoringChannelId;

    /** Список серверов для мониторинга */
    private List<ServerInfo> servers = new ArrayList<>();

    /** Состояние мониторинга (включен/выключен) */
    private boolean monitoringEnabled = false;

    /** Правила сервера */
    private RulesMessage rules;

    /** Список каналов мониторинга */
    private List<String> monitoring = new ArrayList<>();
    
    /** Радиостанции гильдии */
    private List<RadioStation> radioStations = new ArrayList<>();

    /**
     * Создает пустой объект конфигурации.
     */
    public ServerRules() {
        this.inviteLink = "empty";
        this.roles = new ArrayList<>();
        this.monitoringChannelId = "empty";
        this.servers = new ArrayList<>();
        this.monitoringEnabled = false;
        this.rules = new RulesMessage();
        this.monitoring = new ArrayList<>();
        this.radioStations = new ArrayList<>();
        
        // Добавляем радиостанции по умолчанию из списка juniper.bot
        
        // Radio Record (популярные)
        this.radioStations.add(new RadioStation(
            "Record Deep", 
            "https://radiorecord.hostingradio.ru/deep96.aacp"
        ));
        
        this.radioStations.add(new RadioStation(
            "Record Chill House", 
            "https://radiorecord.hostingradio.ru/chillhouse96.aacp"
        ));
        
        this.radioStations.add(new RadioStation(
            "Record Synthwave", 
            "https://radiorecord.hostingradio.ru/synth96.aacp"
        ));
        
        this.radioStations.add(new RadioStation(
            "Record EDM", 
            "https://radiorecord.hostingradio.ru/club96.aacp"
        ));
        
        this.radioStations.add(new RadioStation(
            "Record Tropical", 
            "https://radiorecord.hostingradio.ru/trop96.aacp"
        ));     
        // Другие популярные радиостанции
        this.radioStations.add(new RadioStation(
            "Nightwave Plaza", 
            "https://radio.plaza.one/mp3"
        ));
        
        this.radioStations.add(new RadioStation(
            "Европа Плюс", 
            "http://ep128.hostingradio.ru:8030/ep128"
        ));
        
        this.radioStations.add(new RadioStation(
            "Европа Плюс Top 40", 
            "http://eptop128server.streamr.ru:8033/eptop128"
        ));
        
        this.radioStations.add(new RadioStation(
            "Радио Эрмитаж", 
            "https://hermitage.hostingradio.ru/hermitage128.mp3"
        ));
        
        // Дополнительные станции из списка Radio Record
        this.radioStations.add(new RadioStation(
            "Record Superdiskoteka 90's", 
            "https://radiorecord.hostingradio.ru/sd9096.aacp"
        ));
        
        this.radioStations.add(new RadioStation(
            "Record Techno", 
            "https://radiorecord.hostingradio.ru/techno96.aacp"
        ));
        
        this.radioStations.add(new RadioStation(
            "Record Trance", 
            "https://radiorecord.hostingradio.ru/tm96.aacp"
        ));
    }

    /**
     * Возвращает ссылку для приглашения бота.
     *
     * @return ссылка-приглашение на сервер Discord
     */
    public String getInviteLink() {
        return inviteLink;
    }

    /**
     * Устанавливает ссылку для приглашения бота.
     *
     * @param inviteLink новая ссылка-приглашение
     */
    public void setInviteLink(String inviteLink) {
        this.inviteLink = inviteLink;
    }

    /**
     * Возвращает список правил выдачи ролей.
     *
     * @return список объектов {@link Roles}, описывающих правила выдачи ролей
     */
    public List<Roles> getRoles() {
        return roles;
    }

    /**
     * Устанавливает список правил выдачи ролей.
     *
     * @param roles новый список правил выдачи ролей
     */
    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    /**
     * Возвращает строковое представление конфигурации бота.
     *
     * @return строка с информацией о владельце, ссылке и правилах ролей
     */
    @Override
    public String toString() {
        return "ServerRules{" +
                ", inviteLink='" + inviteLink + '\'' +
                ", roles=" + roles +
                ", monitoringChannelId='" + monitoringChannelId + '\'' +
                ", servers=" + servers +
                ", monitoringEnabled=" + monitoringEnabled +
                ", rules=" + rules +
                ", monitoring=" + monitoring +
                '}';
    }

    public String getMonitoringChannelId() {
        return monitoringChannelId;
    }

    public void setMonitoringChannelId(String monitoringChannelId) {
        this.monitoringChannelId = monitoringChannelId;
    }

    public List<ServerInfo> getServers() {
        return servers;
    }

    public void setServers(List<ServerInfo> servers) {
        this.servers = servers;
    }

    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }

    public RulesMessage getRules() {
        return rules;
    }

    public void setRules(RulesMessage rules) {
        this.rules = rules;
    }

    public List<String> getMonitoring() {
        return monitoring;
    }

    public void setMonitoring(List<String> monitoring) {
        this.monitoring = monitoring;
    }

    private transient ServerMonitor currentMonitor;

    public ServerMonitor getCurrentMonitor() {
        return currentMonitor;
    }

    public void setCurrentMonitor(ServerMonitor monitor) {
        this.currentMonitor = monitor;
    }

    /**
     * Возвращает радиостанции гильдии.
     *
     * @return радиостанции гильдии
     */
    public List<RadioStation> getRadioStations() {
        return radioStations;
    }
    
    /**
     * Устанавливает радиостанции гильдии.
     *
     * @param radioStations новые радиостанции гильдии
     */
    public void setRadioStations(List<RadioStation> radioStations) {
        this.radioStations = radioStations;
    }

    public void addRadioStation(RadioStation station) {
        if (this.radioStations == null) {
            this.radioStations = new ArrayList<>();
        }
        this.radioStations.add(station);
    }

    public boolean removeRadioStation(String name) {
        if (this.radioStations == null) {
            return false;
        }
        return this.radioStations.removeIf(station -> station.getName().equals(name));
    }

    public RadioStation findRadioStation(String name) {
        if (this.radioStations == null) {
            return null;
        }
        return this.radioStations.stream()
            .filter(station -> station.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
} 