package ru.discordj.bot.informer;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.JdaConfig;
import ru.discordj.bot.informer.parser.Parser;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.ServerInfo;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerMonitor {
    private static final Logger logger = LoggerFactory.getLogger(ServerMonitor.class);
    private static ServerMonitor instance;

    private ScheduledExecutorService scheduler;
    private Root config;
    private final Parser parser;
    private Map<String, String> messageIds = new HashMap<>();
    private boolean isRunning = false;
    private Thread monitoringThread;

    private static final Map<String, String> IP_REGIONS = new HashMap<>();

    static {
        // Российские IP диа��азоны (примеры)
        IP_REGIONS.put("195.18.", "ru");
        IP_REGIONS.put("185.189.", "ru");
        IP_REGIONS.put("62.122.", "ru");
        IP_REGIONS.put("51.79.", "sg"); // Сингапур
        IP_REGIONS.put("51.161.", "ca"); // Канада
    }

    public static ServerMonitor getInstance() {
        return instance;
    }

    public ServerMonitor(Root config) {
        this.config = config;
        this.parser = new Parser();
        instance = this;
    }

    public void start() {
        if (!isRunning) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                monitoringThread = new Thread(r, "MonitoringThread");
                return monitoringThread;
            });
            scheduler.scheduleAtFixedRate(this::updateServersStatus, 0, 5, TimeUnit.SECONDS);
            isRunning = true;
            logger.info("Server monitoring started");
        }
    }

    public void stop() {
        if (isRunning) {
            try {
                scheduler.shutdownNow();
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    if (monitoringThread != null) {
                        monitoringThread.interrupt();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isRunning = false;
                messageIds.clear();
                instance = null;
                scheduler = null;
                monitoringThread = null;
                logger.info("Server monitoring forcefully stopped");
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void updateServersStatus() {
        if (Thread.currentThread().isInterrupted()) {
            logger.info("Monitoring thread interrupted, stopping...");
            return;
        }
        
        if (config.getMonitoringChannelId() == null || config.getServers().isEmpty()) {
            return;
        }

        TextChannel channel = JdaConfig.getJda().getTextChannelById(config.getMonitoringChannelId());
        if (channel == null) return;

        // При первом запуске удаляем старые сообщения
        if (messageIds.isEmpty()) {
            channel.getIterableHistory()
                .takeAsync(100)
                .thenAccept(messages -> {
                    messages.stream()
                        .filter(m -> m.getAuthor().equals(JdaConfig.getJda().getSelfUser()))
                        .forEach(m -> m.delete().queue());
                    logger.info("Deleted old bot messages from channel");
                });
        }

        // Обновляем каждый сервер отдельно
        for (ServerInfo server : config.getServers()) {
            Map<String, String> serverInfo = new HashMap<>();
            EmbedBuilder embed = new EmbedBuilder()
                .setTitle("🎮 Статус игрового сервера")
                .setColor(getStatusColor(serverInfo))
                .setTimestamp(Instant.now());

            try {
                serverInfo = parser.getServerInfo(server.getIp(), server.getPort());
                if (!serverInfo.isEmpty()) {
                    String[] players = serverInfo.get("players").split("/");
                    int current = Integer.parseInt(players[0]);
                    int max = Integer.parseInt(players[1]);
                    
                    embed.setDescription("**" + serverInfo.get("name") + "**")
                        .addField("📊 Статус", "🟢 Онлайн", true)
                        .addField("🌍 Регион", getRegionFlag(server.getIp()), true)
                        .addField("🎲 Игра", server.getGame().toUpperCase(), true)
                        .addField("🔗 Подключение", String.format("`%s:%d`", server.getIp(), server.getPort()), true)
                        .addField("🗺️ Карта", serverInfo.get("map"), true)
                        .addField("👥 Игроки", String.format("%d/%d %s", 
                            current, max, 
                            getProgressBar(current, max)), false);

                    // Добавляем дополнительные поля если они есть
                    if (serverInfo.containsKey("version")) {
                        embed.addField("📌 Версия", serverInfo.get("version"), true);
                    }
                    if (serverInfo.containsKey("mode")) {
                        embed.addField("🎯 Режим", serverInfo.get("mode"), true);
                    }
                } else {
                    embed.setDescription("**" + server.getName() + "**")
                        .addField("📊 Статус", "🔴 Оффлайн", true)
                        .addField("🌍 Регион", getRegionFlag(server.getIp()), true)
                        .addField("🎲 Игра", server.getGame().toUpperCase(), true)
                        .addField("🔗 Подключение", String.format("`%s:%d`", server.getIp(), server.getPort()), true);
                }

                // Добавляем подвал с временем обновления
                embed.setFooter("Последнее обновление", null);

            } catch (Exception e) {
                embed.setDescription("**" + server.getName() + "**")
                    .addField("📊 Статус", "⚠️ Ошибка", true)
                    .addField("🌍 Регион", getRegionFlag(server.getIp()), true)
                    .addField("🎲 Игра", server.getGame().toUpperCase(), true)
                    .addField("🔗 Подключение", String.format("`%s:%d`", server.getIp(), server.getPort()), true)
                    .addField("❌ Ошибка", e.getMessage(), false);
            }

            String serverId = server.getIp() + ":" + server.getPort();
            String messageId = messageIds.get(serverId);

            if (messageId == null) {
                // Первая отправка сообщения для этого сервера
                channel.sendMessageEmbeds(embed.build())
                    .queue(message -> messageIds.put(serverId, message.getId()));
            } else {
                // Обновление существующего сообщения
                channel.editMessageEmbedsById(messageId, embed.build())
                    .queue(null, error -> {
                        // Если сообщение не найдено, создаем новое
                        channel.sendMessageEmbeds(embed.build())
                            .queue(message -> messageIds.put(serverId, message.getId()));
                    });
            }
        }
    }

    private Color getStatusColor(Map<String, String> serverInfo) {
        if (serverInfo.isEmpty()) {
            return Color.RED; // Оффлайн
        }
        String[] players = serverInfo.get("players").split("/");
        int current = Integer.parseInt(players[0]);
        int max = Integer.parseInt(players[1]);
        
        if (current >= max) {
            return Color.decode("#ff5555"); // Сервер полный
        } else if (current >= max * 0.7) {
            return Color.decode("#ffaa00"); // Сервер почти полный
        } else {
            return Color.decode("#55ff55"); // Есть места
        }
    }

    private String getProgressBar(int current, int max) {
        int bars = 10;
        int filled = (int) Math.round((double) current / max * bars);
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < bars; i++) {
            if (i < filled) {
                sb.append("█");
            } else {
                sb.append("░");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private String getRegionFlag(String ip) {
        return IP_REGIONS.entrySet().stream()
            .filter(entry -> ip.startsWith(entry.getKey()))
            .map(Map.Entry::getValue)
            .findFirst()
            .map(code -> ":flag_" + code + ":")
            .orElse(":flag_white:"); // Неизвестный регион
    }

    public void updateConfig(Root newConfig) {
        this.config = newConfig;
    }
} 