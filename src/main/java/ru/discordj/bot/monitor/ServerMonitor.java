package ru.discordj.bot.monitor;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.discordj.bot.config.JdaConfig;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.embed.ServerStatusEmbed;
import ru.discordj.bot.monitor.parser.Parser;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.ServerInfo;
import ru.discordj.bot.utility.IJsonHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Класс для мониторинга состояния игровых серверов.
 * Реализует периодический опрос серверов и отображение их статуса в Discord канале.
 * Использует паттерн Singleton для обеспечения единственного экземпляра монитора.
 */
@Component
public class ServerMonitor {
    private static final Logger logger = LoggerFactory.getLogger(ServerMonitor.class);

    private ScheduledExecutorService scheduler;
    @Autowired
    private Root root;
    private final Parser parser;
    private Map<String, String> messageIds = new HashMap<>();
    private boolean isRunning = false;
    private Thread monitoringThread;

    @Autowired
    private IJsonHandler jsonHandler;
    @Autowired
    private EmbedFactory embedFactory;

    /**
     * Создает новый экземпляр монитора с указанной конфигурацией.
     *
     * @param config конфигурация с настройками серверов и каналов
     */
    public ServerMonitor() {
        this.parser = new Parser();
    }

    /**
     * Запускает мониторинг серверов.
     * Создает отдельный поток для периодического опроса серверов.
     */
    public void start() {
        if (!isRunning) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                monitoringThread = new Thread(r, "MonitoringThread");
                return monitoringThread;
            });
            scheduler.scheduleAtFixedRate(this::updateServersStatus, 0, 30, TimeUnit.SECONDS);
            isRunning = true;
            logger.info("Monitoring service started");
        }
    }

    /**
     * Останавливает мониторинг серверов.
     * Освобождает все ресурсы и очищает сообщения.
     */
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
                scheduler = null;
                monitoringThread = null;
                logger.info("Monitoring service stopped");
            }
        }
    }

    /**
     * Проверяет, запущен ли мониторинг.
     *
     * @return true если мониторинг активен, false в противном случае
     */
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Обновляет статус всех серверов.
     * Опрашивает каждый сервер и обновляет сообщения в Discord канале.
     */
    private void updateServersStatus() {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        
        if (root.getMonitoringChannelId() == null || root.getServers().isEmpty()) {
            return;
        }

        TextChannel channel = JdaConfig.getJda().getTextChannelById(root.getMonitoringChannelId());
        if (channel == null) return;

        // При первом запуске удаляем старые сообщения
        if (messageIds.isEmpty()) {
            channel.getIterableHistory()
                .takeAsync(100)
                .thenAccept(messages -> {
                    messages.stream()
                        .filter(m -> m.getAuthor().equals(JdaConfig.getJda().getSelfUser()))
                        .forEach(m -> m.delete().queue());
                });
        }

        ServerStatusEmbed embedBuilder = embedFactory.createServerStatusEmbed();

        for (ServerInfo server : root.getServers()) {
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

    /**
     * Обновляет или создает сообщение в канале Discord.
     *
     * @param channel канал для отправки сообщения
     * @param serverId идентификатор сервера
     * @param embed сообщение для отправки
     */
    private void updateMessage(TextChannel channel, String serverId, MessageEmbed embed) {
        String messageId = messageIds.get(serverId);
        if (messageId == null) {
            channel.sendMessageEmbeds(embed)
                .queue(message -> messageIds.put(serverId, message.getId()));
        } else {
            channel.editMessageEmbedsById(messageId, embed)
                .queue(null, error -> {
                    channel.sendMessageEmbeds(embed)
                        .queue(message -> messageIds.put(serverId, message.getId()));
                });
        }
    }

    /**
     * Обновляет конфигурацию монитора.
     *
     * @param newConfig новая конфигурация
     */
    public void updateConfig(Root newConfig) {
        this.root = newConfig;
    }
} 