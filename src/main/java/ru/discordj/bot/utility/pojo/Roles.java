package ru.discordj.bot.utility.pojo;

/**
 * POJO класс, представляющий правило автоматической выдачи роли в Discord.
 * Каждый объект описывает связь между каналом, ролью и эмодзи для автоматической выдачи ролей.
 *
 * 
 * @version 1.0
 */
public class Roles {

    /** ID канала Discord, где действует правило */
    private String channelId;
    /** ID роли Discord, которая будет выдаваться */
    private String roleId;
    /** ID эмодзи Discord, при реакции которым выдается роль */
    private String emojiId;

    /**
     * Создает новое правило выдачи роли с указанными параметрами.
     *
     * @param channelId ID канала Discord, где будет действовать правило
     * @param roleId ID роли Discord, которая будет выдаваться
     * @param emojiId ID эмодзи Discord, при реакции которым будет выдаваться роль
     */
    public Roles(String channelId, String roleId, String emojiId) {
        this.channelId = channelId;
        this.roleId = roleId;
        this.emojiId = emojiId;
    }
    /**
     * Создает пустое правило выдачи роли.
     */
    public Roles() {
    }

    /**
     * Возвращает ID канала Discord.
     *
     * @return ID канала, где действует правило
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Устанавливает ID канала Discord.
     *
     * @param channelId новый ID канала
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Возвращает ID роли Discord.
     *
     * @return ID роли, которая будет выдаваться
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * Устанавливает ID роли Discord.
     *
     * @param roleId новый ID роли
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    /**
     * Возвращает ID эмодзи Discord.
     *
     * @return ID эмодзи, при реакции которым будет выдаваться роль
     */
    public String getEmojiId() {
        return emojiId;
    }

    /**
     * Устанавливает ID эмодзи Discord.
     *
     * @param emojiId новый ID эмодзи
     */
    public void setEmojiId(String emojiId) {
        this.emojiId = emojiId;
    }

    /**
     * Возвращает строковое представление правила выдачи роли.
     *
     * @return строка с информацией о канале, роли и эмодзи
     */
    @Override
    public String toString() {
        return "Roles{" +
                "channelId='" + channelId + '\'' +
                ", roleId='" + roleId + '\'' +
                ", emojiId='" + emojiId + '\'' +
                '}';
    }
}
