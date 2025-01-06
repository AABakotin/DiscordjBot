package ru.discordj.bot.events.listener.configurator.command;

import org.springframework.stereotype.Component;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import java.util.HashMap;
import javax.annotation.PostConstruct;

/**
 * Фабрика для создания и управления командами бота.
 * Использует паттерн Factory для создания команд.
 */
@Component
@RequiredArgsConstructor
public class BotCommandFactory {
    private final ReadConfigCommand readConfigCommand;
    private final SetIdCommand setIdCommand;
    private final DeleteIdCommand deleteIdCommand;
    private final RoleAddCommand roleAddCommand;
    private final TokenSetCommand tokenSetCommand;
    private final LinkSetCommand linkSetCommand;
    private final RoleDeleteCommand roleDeleteCommand;
    private final MonitoringCommand monitoringCommand;

    private final Map<BotCommand, BotCommandExecutor> commandMap = new HashMap<>();

    @PostConstruct
    private void initCommandMap() {
        commandMap.put(BotCommand.READ_CONF, readConfigCommand);
        commandMap.put(BotCommand.ID, setIdCommand);
        commandMap.put(BotCommand.ID_DEL, deleteIdCommand);
        commandMap.put(BotCommand.ROLE, roleAddCommand);
        commandMap.put(BotCommand.TOKEN, tokenSetCommand);
        commandMap.put(BotCommand.LINK, linkSetCommand);
        commandMap.put(BotCommand.DEL_ROLE, roleDeleteCommand);
        commandMap.put(BotCommand.MONITOR, monitoringCommand);
    }

    /**
     * Возвращает исполнителя команды по её типу
     * @param command тип команды
     * @return исполнитель команды
     * @throws IllegalArgumentException если команда не найдена
     */
    public BotCommandExecutor getCommand(BotCommand command) {
        BotCommandExecutor executor = commandMap.get(command);
        if (executor == null) {
            throw new IllegalArgumentException("Неизвестная команда: " + command);
        }
        return executor;
    }

    /**
     * Проверяет наличие команды в фабрике
     * @param command тип команды
     * @return true если команда существует
     */
    public boolean hasCommand(BotCommand command) {
        return commandMap.containsKey(command);
    }

    /**
     * Возвращает количество зарегистрированных команд
     */
    public int getCommandCount() {
        return commandMap.size();
    }
} 