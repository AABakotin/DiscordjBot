package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.ServerInfo;
import ru.discordj.bot.informer.ServerMonitor;
import ru.discordj.bot.informer.parser.Parser;
import ru.discordj.bot.informer.query.GameServerQueryFactory;
import ru.discordj.bot.informer.query.GameServerQuery;
import ru.discordj.bot.config.MonitoringManager;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringCommand extends BaseCommand {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringCommand.class);
    private final IJsonHandler jsonHandler = JsonParse.getInstance();

    @Override
    public void execute(String[] args, MessageReceivedEvent event, Root root) {
        if (args.length < 2) {
            sendMessage(event, "Использование: !monitor <channel|add|remove|list|start|stop>");
            return;
        }

        switch (args[1].toLowerCase()) {
            case "channel":
                if (args.length < 3) {
                    sendMessage(event, "Укажите ID канала");
                    return;
                }
                setMonitoringChannel(args[2], event, root);
                break;
            case "add":
                if (args.length < 4) {
                    sendMessage(event, "Использование: !monitor add <ip:port> <тип>\n" +
                        "Поддерживаемые типы:\n" +
                        "- arma3 - для серверов ARMA 3\n" +
                        "- dayz - для серверов DayZ\n" +
                        "- source - для Source серверов (CS:GO, TF2)\n" +
                        "Пример: !monitor add 192.168.1.1:27015 dayz");
                    return;
                }
                String[] serverAddress = args[2].split(":");
                if (serverAddress.length != 2) {
                    sendMessage(event, "Неверный формат адреса. Используйте: ip:port");
                    return;
                }
                addServer(serverAddress[0], serverAddress[1], args[3].toLowerCase(), event, root);
                break;
            case "remove":
                if (args.length < 3) {
                    sendMessage(event, "Укажите имя сервера для удаления");
                    return;
                }
                removeServer(args[2], event, root);
                break;
            case "list":
                listServers(event, root);
                break;
            case "start":
                if (MonitoringManager.getInstance().isMonitoringActive()) {
                    sendMessage(event, "Мониторинг уже запущен");
                    return;
                }
                if (root.getMonitoringChannelId() == null) {
                    sendMessage(event, "Сначала укажите канал для мониторинга: !monitor channel <id>");
                    return;
                }
                if (root.getServers().isEmpty()) {
                    sendMessage(event, "Добавьте серверы для мониторинга: !monitor add <ip:port>");
                    return;
                }
                MonitoringManager.getInstance().startMonitoring(root);
                sendMessage(event, "Мониторинг серверов запущен");
                break;
            case "stop":
                if (!MonitoringManager.getInstance().isMonitoringActive()) {
                    sendMessage(event, "Мониторинг не запущен");
                    return;
                }
                MonitoringManager.getInstance().stopMonitoring();
                sendMessage(event, "Мониторинг серверов остановлен");
                break;
        }
    }

    private void setMonitoringChannel(String channelId, MessageReceivedEvent event, Root root) {
        root.setMonitoringChannelId(channelId);
        jsonHandler.write(root);
        sendMessage(event, "Канал мониторинга установлен: " + channelId);
    }

    private void addServer(String ip, String portStr, String game, MessageReceivedEvent event, Root root) {
        try {
            int port = Integer.parseInt(portStr);
            
            // Получаем нужный обработчик запросов
            GameServerQuery queryHandler = GameServerQueryFactory.getQueryHandler(game);
            Map<String, String> serverInfo = queryHandler.query(ip, port);
            
            if (serverInfo.isEmpty()) {
                sendMessage(event, "Не удалось получить информацию о сервере. Проверьте IP и порт.");
                return;
            }

            ServerInfo server = new ServerInfo();
            server.setName(serverInfo.get("name"));
            server.setIp(ip);
            server.setPort(port);
            server.setGame(game);
            server.setProtocol(queryHandler.getProtocolName());
            server.setEnabled(true);
            
            root.getServers().add(server);
            jsonHandler.write(root);
            
            // Обновляем конфигурацию в существующем мониторе
            if (root.getCurrentMonitor() != null) {
                root.getCurrentMonitor().updateConfig(root);
            }
            
            sendMessage(event, String.format("Сервер добавлен: %s (%s:%d) [%s]", 
                server.getName(), ip, port, game));
            
        } catch (NumberFormatException e) {
            sendMessage(event, "Неверный формат порта");
        } catch (Exception e) {
            sendMessage(event, "Ошибка при добавлении сервера: " + e.getMessage());
        }
    }

    private void removeServer(String name, MessageReceivedEvent event, Root root) {
        root.getServers().removeIf(s -> s.getName().equals(name));
        jsonHandler.write(root);
        sendMessage(event, "Сервер удален: " + name);
    }

    private void listServers(MessageReceivedEvent event, Root root) {
        if (root.getServers().isEmpty()) {
            sendMessage(event, "Серверов не добавлено");
            return;
        }

        StringBuilder sb = new StringBuilder("Список серверов:\n");
        for (ServerInfo server : root.getServers()) {
            sb.append(String.format("%s (%s:%d) - %s\n", 
                server.getName(), 
                server.getIp(), 
                server.getPort(),
                server.isEnabled() ? "активен" : "отключен"));
        }
        sendMessage(event, sb.toString());
    }
} 