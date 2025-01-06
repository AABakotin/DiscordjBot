package ru.discordj.bot.embed;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.ServerInfo;
import java.awt.Color;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ServerStatusEmbed extends BaseEmbed {
    private static final String ONLINE = "🟢 Онлайн";
    private static final String OFFLINE = "🔴 Оффлайн";
    private static final Color ONLINE_COLOR = Color.GREEN;
    private static final Color OFFLINE_COLOR = Color.RED;
    
    private static final Map<String, String> GAME_ICONS = Map.ofEntries(
        Map.entry("minecraft", "⛏️"),
        Map.entry("csgo", "🔫"),
        Map.entry("valorant", "🎯"),
        Map.entry("default", "🎮")
    );

    @Autowired
    public ServerStatusEmbed(IJsonHandler jsonHandler) {
        super(jsonHandler);
    }

    /**
     * Создает embed для отображения статуса сервера
     */
    public MessageEmbed createServerEmbed(ServerInfo server, Map<String, String> serverInfo) {
        EmbedBuilder builder = createDefaultBuilder()
            .setTitle(getServerTitle(server, serverInfo))
            .setColor(isServerOnline(serverInfo) ? ONLINE_COLOR : OFFLINE_COLOR);

        addServerFields(builder, server, serverInfo);
        return builder.build();
    }

    /**
     * Создает embed с сообщением об ошибке
     */
    public MessageEmbed createErrorEmbed(ServerInfo server, String errorMessage) {
        return createDefaultBuilder()
            .setTitle(getGameIcon(server.getGame()) + " " + server.getName())
            .setColor(OFFLINE_COLOR)
            .addField("Статус", OFFLINE, false)
            .addField("Ошибка", errorMessage, false)
            .build();
    }

    private String getServerTitle(ServerInfo server, Map<String, String> serverInfo) {
        String status = isServerOnline(serverInfo) ? ONLINE : OFFLINE;
        return String.format("%s %s | %s", 
            getGameIcon(server.getGame()),
            server.getName(),
            status
        );
    }

    private void addServerFields(EmbedBuilder builder, ServerInfo server, Map<String, String> serverInfo) {
        // Базовая информация
        builder.addField("IP", formatAddress(server.getIp(), server.getPort()), true)
               .addField("Игра", server.getGame(), true);

        // Информация о карте и игроках
        if (isServerOnline(serverInfo)) {
            addOnlineServerInfo(builder, serverInfo);
        }
    }

    private void addOnlineServerInfo(EmbedBuilder builder, Map<String, String> serverInfo) {
        String map = serverInfo.get("map");
        if (map != null && !map.isEmpty()) {
            builder.addField("Карта", map, true);
        }

        String players = serverInfo.get("players");
        String maxPlayers = serverInfo.get("maxPlayers");
        if (players != null && maxPlayers != null) {
            builder.addField("Игроки", String.format("%s/%s", players, maxPlayers), true);
        }

        String version = serverInfo.get("version");
        if (version != null && !version.isEmpty()) {
            builder.addField("Версия", version, true);
        }
    }

    private String formatAddress(String ip, int port) {
        return String.format("%s:%d", ip, port);
    }

    private String getGameIcon(String game) {
        return GAME_ICONS.getOrDefault(game.toLowerCase(), GAME_ICONS.get("default"));
    }

    private boolean isServerOnline(Map<String, String> serverInfo) {
        return serverInfo != null && !serverInfo.isEmpty();
    }
} 