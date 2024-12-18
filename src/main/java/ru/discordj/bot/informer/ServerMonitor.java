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
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerMonitor {
    private static final Logger logger = LoggerFactory.getLogger(ServerMonitor.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final Root config;
    private final Parser parser;
    private String lastMessageId;

    public ServerMonitor(Root config) {
        this.config = config;
        this.parser = new Parser();
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::updateServersStatus, 0, 5, TimeUnit.SECONDS);
    }

    private void updateServersStatus() {
        if (config.getMonitoringChannelId() == null || config.getServers().isEmpty()) {
            return;
        }

        TextChannel channel = JdaConfig.getJda().getTextChannelById(config.getMonitoringChannelId());
        if (channel == null) return;

        EmbedBuilder embed = new EmbedBuilder()
            .setTitle("🎮 Мониторинг игровых серверов")
            .setColor(Color.decode("#171a21"))
            .setTimestamp(Instant.now());

        for (ServerInfo server : config.getServers()) {
            try {
                Map<String, String> serverInfo = parser.getServerInfo(server.getIp(), server.getPort());
                if (!serverInfo.isEmpty()) {
                    embed.addField("",
                        String.format("**%s**\n" +
                            "```\n" +
                            "Статус:   🟢 Онлайн\n" +
                            "Карта:    %s\n" +
                            "Игроки:   %s\n" +
                            "IP:Порт:  %s:%d\n" +
                            "```",
                            serverInfo.get("name"),
                            serverInfo.get("map"),
                            serverInfo.get("players"),
                            server.getIp(), server.getPort()),
                        false);
                } else {
                    embed.addField("",
                        String.format("**%s**\n" +
                            "```\n" +
                            "Статус:   🔴 Оффлайн\n" +
                            "IP:Порт:  %s:%d\n" +
                            "```",
                            server.getName(),
                            server.getIp(), server.getPort()),
                        false);
                }
            } catch (Exception e) {
                embed.addField("",
                    String.format("**%s**\n" +
                        "```\n" +
                        "Статус:   ⚠️ Ошибка\n" +
                        "IP:Порт:  %s:%d\n" +
                        "Причина:  %s\n" +
                        "```",
                        server.getName(),
                        server.getIp(), server.getPort(),
                        e.getMessage()),
                    false);
                logger.error("Error updating server status: {}", e.getMessage());
            }
        }

        if (lastMessageId == null) {
            // Первая отправка сообщения
            channel.sendMessageEmbeds(embed.build())
                .queue(message -> lastMessageId = message.getId());
        } else {
            // Обновление существующего сообщения
            channel.editMessageEmbedsById(lastMessageId, embed.build())
                .queue(null, error -> {
                    // Если сообщение не найдено, создаем новое
                    channel.sendMessageEmbeds(embed.build())
                        .queue(message -> lastMessageId = message.getId());
                });
        }
    }

    public void stop() {
        scheduler.shutdown();
    }
} 