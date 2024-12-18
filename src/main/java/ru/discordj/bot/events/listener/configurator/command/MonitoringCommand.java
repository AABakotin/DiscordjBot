package ru.discordj.bot.events.listener.configurator.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import ru.discordj.bot.events.listener.configurator.BaseCommand;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.Root;
import ru.discordj.bot.utility.pojo.ServerInfo;
import ru.discordj.bot.informer.ServerMonitor;
import ru.discordj.bot.informer.parser.Parser;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MonitoringCommand extends BaseCommand {
    private static final Logger logger = LoggerFactory.getLogger(MonitoringCommand.class);
    private final IJsonHandler jsonHandler = JsonParse.getInstance();
    private ServerMonitor currentMonitor;

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
                if (args.length < 3) {
                    sendMessage(event, "Использование: !monitor add <ip:port>");
                    return;
                }
                String[] serverAddress = args[2].split(":");
                if (serverAddress.length != 2) {
                    sendMessage(event, "Неверный формат адреса. Используйте: ip:port");
                    return;
                }
                addServer(serverAddress[0], serverAddress[1], event, root);
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
                if (currentMonitor != null) {
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
                startMonitoring(event, root);
                break;
            case "stop":
                if (currentMonitor == null) {
                    sendMessage(event, "Мониторинг не запущен");
                    return;
                }
                stopMonitoring(event, root);
                break;
        }
    }

    private void setMonitoringChannel(String channelId, MessageReceivedEvent event, Root root) {
        root.setMonitoringChannelId(channelId);
        jsonHandler.write(root);
        sendMessage(event, "Канал мониторинга установлен: " + channelId);
    }

    private void addServer(String ip, String portStr, MessageReceivedEvent event, Root root) {
        try {
            int port = Integer.parseInt(portStr);
            
            // Получаем информацию о сервере
            Parser parser = new Parser();
            Map<String, String> serverInfo = parser.getServerInfo(ip, port);
            
            if (serverInfo.isEmpty()) {
                sendMessage(event, "Не удалось получить информацию о сервере. Проверьте IP и порт.");
                return;
            }

            // Создаем объект сервера с полученным именем
            ServerInfo server = new ServerInfo();
            server.setName(serverInfo.get("name"));
            server.setIp(ip);
            server.setPort(port);
            server.setEnabled(true);
            
            root.getServers().add(server);
            jsonHandler.write(root);
            
            // Если монитор запущен, перезапускаем его с новой конфигурацией
            if (currentMonitor != null) {
                currentMonitor.stop();
                currentMonitor = new ServerMonitor(root);
                currentMonitor.start();
            }
            
            sendMessage(event, String.format("Сервер добавлен: %s (%s:%d)", 
                server.getName(), ip, port));
            
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

    private void startMonitoring(MessageReceivedEvent event, Root root) {
        currentMonitor = new ServerMonitor(root);
        currentMonitor.start();
        root.setMonitoringEnabled(true);
        jsonHandler.write(root);
        sendMessage(event, "Мониторинг серверов запущен");
    }

    private void stopMonitoring(MessageReceivedEvent event, Root root) {
        if (currentMonitor != null) {
            currentMonitor.stop();
            currentMonitor = null;
            root.setMonitoringEnabled(false);
            jsonHandler.write(root);
            sendMessage(event, "Мониторинг серверов остановлен");
        }
    }

    // Метод для автоматического запуска мониторинга при старте бота
    public void initMonitoring() {
        Root root = jsonHandler.read();
        if (root.isMonitoringEnabled() && root.getMonitoringChannelId() != null && !root.getServers().isEmpty()) {
            currentMonitor = new ServerMonitor(root);
            currentMonitor.start();
            logger.info("Monitoring auto-started");
        }
    }
} 