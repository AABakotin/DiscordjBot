package ru.discordj.bot.utility.pojo;

import java.util.Map;

public class ServerInfo {
    private String ip;
    private int port;
    private String name;
    private String game;
    private boolean enabled;
    private String protocol;
    private Map<String, String> customFields;

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }
    
    public String getProtocol() { return protocol; }
    public void setProtocol(String protocol) { this.protocol = protocol; }
    
    public Map<String, String> getCustomFields() { return customFields; }
    public void setCustomFields(Map<String, String> customFields) { this.customFields = customFields; }
} 