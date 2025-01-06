package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;
import ru.discordj.bot.monitor.ServerMonitor;
import ru.discordj.bot.monitor.query.GameServerQuery;
import ru.discordj.bot.monitor.query.GameServerQueryFactory;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.ServerInfo;
import ru.discordj.bot.config.MonitoringManager;
import ru.discordj.bot.embed.EmbedFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@Component
@Slf4j
@RequiredArgsConstructor
public class MonitoringCommand implements BotCommandExecutor {
    private static final String HELP_MESSAGE = 
        "Использование команды мониторинга:\n" +
        "`!monitor start` - Запустить мониторинг\n" +
        "`!monitor stop` - Остановить мониторинг\n" +
        "`!monitor status` - Проверить статус\n" +
        "`!monitor channel <ID>` - Установить канал мониторинга\n" +
        "`!monitor add <ip> <port> <name> <game>` - Добавить сервер\n" +
        "`!monitor remove <ip> <port>` - Удалить сервер";

    private static final String ERROR_ARGS = "❌ Недостаточно аргументов. Используйте `!monitor help` для справки";
    private static final String ERROR_CHANNEL = "❌ Неверный ID канала";
    private static final String ERROR_PORT = "❌ Неверный порт";
    
    private final ServerMonitor serverMonitor;
    private final IJsonHandler jsonHandler;
    private final EmbedFactory embedFactory;

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 2) {
            event.getChannel().sendMessage(ERROR_ARGS).queue();
            return;
        }

        try {
            handleCommand(args, event, root);
        } catch (Exception e) {
            handleError(event, e);
        }
    }

    private void handleCommand(String[] args, MessageReceivedEvent event, Root root) {
        switch (args[1].toLowerCase()) {
            case "help":
                sendHelpMessage(event);
                break;
            case "start":
                startMonitoring(event, root);
                break;
            case "stop":
                stopMonitoring(event, root);
                break;
            case "status":
                checkStatus(event);
                break;
            case "channel":
                setMonitoringChannel(args, event, root);
                break;
            case "add":
                addServer(args, event, root);
                break;
            case "remove":
                removeServer(args, event, root);
                break;
            default:
                event.getChannel().sendMessage("❌ Неизвестная подкоманда").queue();
        }
    }

    private void sendHelpMessage(MessageReceivedEvent event) {
        event.getChannel().sendMessage(HELP_MESSAGE).queue();
    }

    private void startMonitoring(MessageReceivedEvent event, Root root) {
        if (root.getMonitoringChannelId() == null) {
            event.getChannel().sendMessage("❌ Сначала установите канал мониторинга").queue();
            return;
        }
        
        serverMonitor.start();
        event.getChannel().sendMessage("✅ Мониторинг запущен").queue();
        log.info("Monitoring started by user: {}", event.getAuthor().getName());
    }

    private void stopMonitoring(MessageReceivedEvent event, Root root) {
        serverMonitor.stop();
        event.getChannel().sendMessage("✅ Мониторинг остановлен").queue();
        log.info("Monitoring stopped by user: {}", event.getAuthor().getName());
    }

    private void checkStatus(MessageReceivedEvent event) {
        String status = serverMonitor.isRunning() ? "✅ Работает" : "⭕ Остановлен";
        event.getChannel().sendMessage("Статус мониторинга: " + status).queue();
    }

    private void setMonitoringChannel(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 3) {
            event.getChannel().sendMessage(ERROR_ARGS).queue();
            return;
        }

        try {
            root.setMonitoringChannelId(args[2]);
            updateConfigAndNotify(event, root);
            log.info("Monitoring channel set to {} by user: {}", args[2], event.getAuthor().getName());
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(ERROR_CHANNEL).queue();
        }
    }

    private void addServer(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 6) {
            event.getChannel().sendMessage(ERROR_ARGS).queue();
            return;
        }

        try {
            ServerInfo server = new ServerInfo();
            server.setIp(args[2]);
            server.setPort(Integer.parseInt(args[3]));
            server.setName(args[4]);
            server.setGame(args[5]);

            root.getServers().add(server);
            updateConfigAndNotify(event, root);
            log.info("Server added: {}:{} by user: {}", args[2], args[3], event.getAuthor().getName());
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage(ERROR_PORT).queue();
        }
    }

    private void removeServer(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 4) {
            event.getChannel().sendMessage(ERROR_ARGS).queue();
            return;
        }

        String ip = args[2];
        int port = Integer.parseInt(args[3]);

        root.getServers().removeIf(server -> 
            server.getIp().equals(ip) && server.getPort() == port
        );

        updateConfigAndNotify(event, root);
        log.info("Server removed: {}:{} by user: {}", ip, port, event.getAuthor().getName());
    }

    private void updateConfigAndNotify(MessageReceivedEvent event, Root root) {
        jsonHandler.write(root);
        serverMonitor.updateConfig(root);
        
        event.getChannel().sendMessageEmbeds(
            embedFactory.createConfigEmbed()
                .embedConfiguration()
        ).queue();
    }

    private void handleError(MessageReceivedEvent event, Exception e) {
        log.error("Error in monitoring command", e);
        event.getChannel().sendMessage("❌ Ошибка: " + e.getMessage()).queue();
    }
} 