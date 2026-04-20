package ru.discordj.bot.monitor;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.JdaConfig;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.embed.ServerStatusEmbed;
import ru.discordj.bot.monitor.parser.Parser;
import ru.discordj.bot.utility.pojo.ServerRules;
import ru.discordj.bot.utility.pojo.ServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerMonitor {
    private static final Logger logger = LoggerFactory.getLogger(ServerMonitor.class);
    private static ServerMonitor instance;

    private ScheduledExecutorService scheduler;
    private ServerRules config;
    private final Parser parser;
    private Map<String, String> messageIds = new HashMap<>();
    private boolean isRunning = false;
    private Thread monitoringThread;

    public static ServerMonitor getInstance() {
        return instance;
    }

    public ServerMonitor(ServerRules config) {
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
            // Увеличил интервал до 60 секунд, чтобы снизить нагрузку на API
            scheduler.scheduleAtFixedRate(this::updateServersStatus, 0, 60, TimeUnit.SECONDS);
            isRunning = true;
            logger.info("Monitoring service started");
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
                logger.info("Monitoring service stopped");
            }
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    private void updateServersStatus() {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }

        if (config.getMonitoringChannelId() == null || config.getServers().isEmpty()) {
            return;
        }

        TextChannel channel = JdaConfig.getJda().getTextChannelById(config.getMonitoringChannelId());
        if (channel == null) return;

        // При первом запуске удаляем старые сообщения (только один раз)
        if (messageIds.isEmpty()) {
            channel.getIterableHistory()
                    .takeAsync(100)
                    .thenAccept(messages -> {
                        messages.stream()
                                .filter(m -> m.getAuthor().equals(JdaConfig.getJda().getSelfUser()))
                                .forEach(m -> m.delete().queue());
                    });
        }

        ServerStatusEmbed embedBuilder = EmbedFactory.createServerStatusEmbed();

        for (ServerInfo server : config.getServers()) {
            try {
                Map<String, String> serverInfo = parser.getServerInfo(server.getIp(), server.getPort());
                MessageEmbed embed = embedBuilder.createServerEmbed(server, serverInfo);
                String serverId = server.getIp() + ":" + server.getPort();
                updateMessage(channel, serverId, embed);
            } catch (Exception e) {
                MessageEmbed embed = embedBuilder.createErrorEmbed(server, e.getMessage());
                String serverId = server.getIp() + ":" + server.getPort();
                updateMessage(channel, serverId, embed);
            }
        }
    }

    private void updateMessage(TextChannel channel, String serverId, MessageEmbed embed) {
        String messageId = messageIds.get(serverId);
        if (messageId == null) {
            // Отправляем новое сообщение
            channel.sendMessageEmbeds(embed).queue(
                    message -> messageIds.put(serverId, message.getId()),
                    failure -> logger.error("Не удалось отправить сообщение для сервера {}: {}", serverId, failure.getMessage())
            );
        } else {
            // Пытаемся отредактировать существующее
            channel.editMessageEmbedsById(messageId, embed).queue(
                    success -> { /* OK */ },
                    failure -> {
                        // Если не удалось отредактировать (сообщение удалено или ошибка), удаляем ID и пробуем отправить новое
                        logger.warn("Не удалось отредактировать сообщение {} для сервера {}, отправляем новое: {}", messageId, serverId, failure.getMessage());
                        messageIds.remove(serverId);
                        channel.sendMessageEmbeds(embed).queue(
                                newMessage -> messageIds.put(serverId, newMessage.getId()),
                                newFailure -> logger.error("Не удалось отправить новое сообщение для сервера {}: {}", serverId, newFailure.getMessage())
                        );
                    }
            );
        }
    }

    public void updateConfig(ServerRules newConfig) {
        this.config = newConfig;
    }
}