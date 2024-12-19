package ru.discordj.bot.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ru.discordj.bot.utility.pojo.ServerInfo;
import java.awt.Color;
import java.util.Map;

public class ServerStatusEmbed extends BaseEmbed {
    
    public MessageEmbed createServerEmbed(ServerInfo server, Map<String, String> serverInfo) {
        EmbedBuilder embed = new EmbedBuilder()
            .setTitle(null)
            .setColor(getStatusColor(serverInfo));

        if (!serverInfo.isEmpty()) {
            embed.setDescription(String.format("**%s**\n" +
                "```\n" +
                "Status    : %s\n" +
                "Map       : %s\n" +
                "Players   : %s\n" +
                "Address   : %s:%d\n" +
                "```",
                serverInfo.get("name"),
                "🟢 Online",
                serverInfo.get("map"),
                serverInfo.get("players"),
                server.getIp(), server.getPort()));
        } else {
            embed.setDescription(String.format("**%s**\n" +
                "```\n" +
                "Status    : %s\n" +
                "Address   : %s:%d\n" +
                "```",
                server.getName(),
                "🔴 Offline",
                server.getIp(), server.getPort()));
        }

        return embed.build();
    }

    public MessageEmbed createErrorEmbed(ServerInfo server, String error) {
        return new EmbedBuilder()
            .setTitle(null)
            .setColor(Color.decode("#2f3136"))
            .setDescription(String.format("**%s**\n" +
                "```\n" +
                "Status    : %s\n" +
                "Address   : %s:%d\n" +
                "Error     : %s\n" +
                "```",
                server.getName(),
                "⚠️ Error",
                server.getIp(), server.getPort(),
                error))
            .build();
    }

    private Color getStatusColor(Map<String, String> serverInfo) {
        if (serverInfo.isEmpty()) {
            return Color.decode("#2f3136");
        }

        try {
            String[] players = serverInfo.get("players").split("/");
            int current = Integer.parseInt(players[0]);
            int max = Integer.parseInt(players[1]);
            float percentage = (float) current / max;
            
            if (percentage < 0.3) {
                return Color.decode("#43b581");
            } else if (percentage < 0.7) {
                return Color.decode("#faa61a");
            } else if (percentage < 0.9) {
                return Color.decode("#f26522");
            } else {
                return Color.decode("#f04747");
            }
        } catch (Exception e) {
            return Color.decode("#2f3136");
        }
    }
} 