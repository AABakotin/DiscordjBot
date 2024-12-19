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
public class Root {

    /** Токен авторизации бота Discord */
    private String token;
    
    /** ID пользователя Discord, имеющего права администратора бота */
    private String owner;
    
    /** Ссылка для приглашения бота на сервер */
    private String inviteLink;

    /** Список правил автоматической выдачи ролей */
    private List<Roles> roles;

    /** ID канала мониторинга */
    private String monitoringChannelId;

    /** Список серверов для мониторинга */
    private List<ServerInfo> servers = new ArrayList<>();

    /** Состояние мониторинга (включен/выключен) */
    private boolean monitoringEnabled = false;

    /**
     * Создает пустой объект конфигурации.
     */
    public Root() {
    }

    /**
     * Создает объект конфигурации с заданными параметрами.
     *
     * @param token токен авторизации бота
     * @param owner ID администратора бота
     * @param inviteLink ссылка для приглашения
     * @param roles список правил выдачи ролей
     */
    public Root(String token, String owner, String inviteLink, List<Roles> roles) {
        this.token = token;
        this.owner = owner;
        this.inviteLink = inviteLink;
        this.roles = roles;
    }

    /**
     * Возвращает токен авторизации бота.
     *
     * @return токен Discord бота
     */
    public String getToken() {
        return token;
    }

    /**
     * Устанавливает токен авторизации бота.
     *
     * @param token новый токен Discord бота
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Возвращает ID владельца (администратора) бота.
     *
     * @return ID пользователя Discord, который является администратором
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Устанавливает ID владельца (администратора) бота.
     *
     * @param owner новый ID администратора бота
     */
    public void setOwner(String owner) {
        this.owner = owner;
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
     * Возвращает список правил автоматической выдачи ролей.
     *
     * @return список объектов {@link Roles}, описывающих правила выдачи ролей
     */
    public List<Roles> getRoles() {
        return roles;
    }

    /**
     * Устанавливает список правил автоматической выдачи ролей.
     *
     * @param roles новый список правил выдачи ролей
     */
    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    /**
     * Возвращает стр��ковое представление конфигурации бота.
     *
     * @return строка с информацией о токене, владельце, ссылке и правилах ролей
     */
    @Override
    public String toString() {
        return "Root{" +
                "token='" + token + '\'' +
                ", owner='" + owner + '\'' +
                ", inviteLink='" + inviteLink + '\'' +
                ", roles=" + roles +
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

    private transient ServerMonitor currentMonitor;

    public ServerMonitor getCurrentMonitor() {
        return currentMonitor;
    }

    public void setCurrentMonitor(ServerMonitor monitor) {
        this.currentMonitor = monitor;
    }
}
