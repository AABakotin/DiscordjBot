package ru.discordj.bot.events.listener.configurator.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.discordj.bot.events.listener.configurator.BotCommandExecutor;

@Component
public class BotCommandFactory {
    @Autowired private ReadConfigCommand readConfigCommand;
    @Autowired private SetIdCommand setIdCommand;
    @Autowired private DeleteIdCommand deleteIdCommand;
    @Autowired private RoleAddCommand roleAddCommand;
    @Autowired private TokenSetCommand tokenSetCommand;
    @Autowired private LinkSetCommand linkSetCommand;
    @Autowired private RoleDeleteCommand roleDeleteCommand;
    @Autowired private MonitoringCommand monitoringCommand;

    public BotCommandExecutor getCommand(BotCommand command) {
        switch (command) {
            case READ_CONF: return readConfigCommand;
            case ID: return setIdCommand;
            case ID_DEL: return deleteIdCommand;
            case ROLE: return roleAddCommand;
            case TOKEN: return tokenSetCommand;
            case LINK: return linkSetCommand;
            case DEL_ROLE: return roleDeleteCommand;
            case MONITOR: return monitoringCommand;
            default: throw new IllegalArgumentException("Unknown command: " + command);
        }
    }
} 