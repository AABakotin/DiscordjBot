package ru.discordj.bot.utility.pojo;

public class ServerInfo {
    private String ip;
    private int port;
    private String name;
    private boolean enabled;

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
} 