package ru.discordj.bot.events.slashcommands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.MonitoringManager;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.utility.pojo.Roles;
import ru.discordj.bot.utility.pojo.ServerInfo;
import ru.discordj.bot.utility.pojo.ServerRules;
import ru.discordj.bot.utility.pojo.RulesMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда для управления настройками гильдии.
 * Доступна только для администраторов.
 */
public class GuildConfigSlashCommand implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(GuildConfigSlashCommand.class);
    private final JsonParse jsonHandler = JsonParse.getInstance();

    @Override
    public String getName() {
        return "guild-config";
    }

    @Override
    public String getDescription() {
        return "Управление настройками гильдии (только для администраторов)";
    }

    @Override
    public List<OptionData> getOptions() {
        return new ArrayList<>();
    }
    
    @Override
    public DefaultMemberPermissions getDefaultMemberPermissions() {
        // Ограничиваем команду только для пользователей с правами администратора
        return DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR);
    }
    
    @Override
    public List<SubcommandData> getSubcommands() {
        List<SubcommandData> subcommands = new ArrayList<>();
        
        // Подкоманда для просмотра текущей конфигурации
        subcommands.add(new SubcommandData("view", "Просмотр текущей конфигурации гильдии"));
        
        // Подкоманда для установки ссылки-приглашения
        SubcommandData setInviteLink = new SubcommandData("set-invite", "Установить ссылку-приглашение для гильдии");
        setInviteLink.addOption(OptionType.STRING, "link", "Ссылка-приглашение", true);
        subcommands.add(setInviteLink);
        
        // Подкоманда для добавления роли с эмодзи
        SubcommandData addRole = new SubcommandData("add-role", "Добавить роль для выдачи по эмодзи");
        addRole.addOption(OptionType.ROLE, "role", "Роль для выдачи", true);
        addRole.addOption(OptionType.CHANNEL, "channel", "Канал, где будет работать реакция", true);
        addRole.addOption(OptionType.STRING, "emoji", "Эмодзи для реакции", true);
        subcommands.add(addRole);
        
        // Подкоманда для удаления роли
        SubcommandData removeRole = new SubcommandData("remove-role", "Удалить роль из автовыдачи");
        removeRole.addOption(OptionType.ROLE, "role", "Роль для удаления", true);
        subcommands.add(removeRole);
        
        // Подкоманда для мониторинга
        SubcommandData monitoringCommand = new SubcommandData("monitoring", "Управление мониторингом серверов");
        monitoringCommand.addOption(OptionType.STRING, "action", "Действие (enable/disable)", true);
        monitoringCommand.addOption(OptionType.CHANNEL, "channel", "Канал для отображения статуса (при enable)", false);
        subcommands.add(monitoringCommand);
        
        // Подкоманда для добавления сервера для мониторинга
        SubcommandData addServerCommand = new SubcommandData("add-server", "Добавить сервер для мониторинга");
        addServerCommand.addOption(OptionType.STRING, "name", "Название сервера", true);
        addServerCommand.addOption(OptionType.STRING, "host", "Хост или IP-адрес", true);
        addServerCommand.addOption(OptionType.INTEGER, "port", "Порт", true);
        subcommands.add(addServerCommand);
        
        // Подкоманда для удаления сервера из мониторинга
        SubcommandData removeServerCommand = new SubcommandData("remove-server", "Удалить сервер из мониторинга");
        removeServerCommand.addOption(OptionType.STRING, "name", "Название сервера", true);
        subcommands.add(removeServerCommand);
        
        // Подкоманда для редактирования правил сервера
        SubcommandData editRulesCommand = new SubcommandData("edit-rules", "Редактировать правила сервера");
        editRulesCommand.addOption(OptionType.STRING, "title", "Заголовок правил", false);
        editRulesCommand.addOption(OptionType.STRING, "welcome", "Приветственное сообщение", false);
        editRulesCommand.addOption(OptionType.STRING, "rules", "Текст правил", false);
        editRulesCommand.addOption(OptionType.STRING, "footer", "Текст внизу (используйте {author} и {date})", false);
        subcommands.add(editRulesCommand);
        
        return subcommands;
    }
    
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        // Проверяем наличие гильдии
        Guild guild = event.getGuild();
        if (guild == null) {
            event.reply("Эта команда доступна только на серверах Discord").setEphemeral(true).queue();
            return;
        }
        
        String subcommand = event.getSubcommandName();
        if (subcommand == null) {
            event.reply("Необходимо указать подкоманду").setEphemeral(true).queue();
            return;
        }
        
        switch (subcommand) {
            case "view":
                handleViewConfig(event, guild);
                break;
            case "set-invite":
                handleSetInvite(event, guild);
                break;
            case "add-role":
                handleAddRole(event, guild);
                break;
            case "remove-role":
                handleRemoveRole(event, guild);
                break;
            case "monitoring":
                handleMonitoring(event, guild);
                break;
            case "add-server":
                handleAddServer(event, guild);
                break;
            case "remove-server":
                handleRemoveServer(event, guild);
                break;
            case "edit-rules":
                handleEditRules(event, guild);
                break;
            default:
                event.reply("Неизвестная подкоманда: " + subcommand).setEphemeral(true).queue();
        }
    }

    private void handleViewConfig(SlashCommandInteractionEvent event, Guild guild) {
        ServerRules config = jsonHandler.read(guild);
        RulesMessage rules = jsonHandler.readRules(guild);
        
        StringBuilder response = new StringBuilder();
        response.append("**Конфигурация гильдии \"").append(guild.getName()).append("\"**\n\n");
        
        // Правила сервера
        response.append("**Правила сервера:**\n");
        response.append("• Заголовок: ").append(rules.getTitle()).append("\n");
        response.append("• Приветствие: ").append(rules.getWelcomeField()).append("\n");
        response.append("• Правила: ").append(rules.getFormattedRulesField()).append("\n\n");
        
        // Основные настройки
        response.append("**Основные настройки:**\n");
        response.append("• Ссылка-приглашение: ").append(config.getInviteLink()).append("\n\n");
        
        // Роли для автовыдачи
        response.append("**Роли для автовыдачи:**\n");
        if (config.getRoles() != null && !config.getRoles().isEmpty()) {
            for (int i = 0; i < config.getRoles().size(); i++) {
                Roles role = config.getRoles().get(i);
                response.append(i + 1).append(". Канал: ").append(role.getChannelId())
                    .append(", Роль: ").append(role.getRoleId())
                    .append(", Эмодзи: ").append(role.getEmojiId()).append("\n");
            }
        } else {
            response.append("Нет настроенных ролей для автовыдачи.\n");
        }
        
        // Мониторинг
        response.append("\n**Мониторинг:**\n");
        response.append("• Статус: ").append(config.isMonitoringEnabled() ? "Включен" : "Выключен").append("\n");
        response.append("• Канал: ").append(config.getMonitoringChannelId()).append("\n");
        
        // Серверы для мониторинга
        response.append("\n**Серверы для мониторинга:**\n");
        if (config.getServers() != null && !config.getServers().isEmpty()) {
            for (ServerInfo server : config.getServers()) {
                response.append("• ").append(server.getName())
                    .append(" (").append(server.getIp()).append(":").append(server.getPort()).append(")\n");
            }
        } else {
            response.append("Нет настроенных серверов для мониторинга.\n");
        }
        
        event.reply(response.toString()).setEphemeral(true).queue();
    }

    private void handleSetInvite(SlashCommandInteractionEvent event, Guild guild) {
        String inviteLink = event.getOption("link", "", OptionMapping::getAsString);
        
        if (inviteLink.isBlank()) {
            event.reply("Необходимо указать ссылку-приглашение").setEphemeral(true).queue();
            return;
        }
        
        ServerRules config = jsonHandler.read(guild);
        config.setInviteLink(inviteLink);
        jsonHandler.write(guild, config);
        
        logger.info("Гильдия {}: установлена ссылка-приглашение", guild.getName());
        event.reply("Ссылка-приглашение успешно установлена: " + inviteLink)
            .setEphemeral(true)
            .queue();
    }

    private void handleAddRole(SlashCommandInteractionEvent event, Guild guild) {
        OptionMapping roleOption = event.getOption("role");
        OptionMapping channelOption = event.getOption("channel");
        OptionMapping emojiOption = event.getOption("emoji");
        
        if (roleOption == null || channelOption == null || emojiOption == null) {
            event.reply("Необходимо указать роль, канал и эмодзи").setEphemeral(true).queue();
            return;
        }
        
        String roleId = roleOption.getAsRole().getId();
        String channelId = channelOption.getAsChannel().getId();
        String emoji = emojiOption.getAsString();
        
        // Проверяем, существует ли уже роль с таким эмодзи
        ServerRules config = jsonHandler.read(guild);
        if (config.getRoles() == null) {
            config.setRoles(new ArrayList<>());
        }
        
        boolean roleExists = config.getRoles().stream()
            .anyMatch(r -> r.getEmojiId().equals(emoji));
        
        if (roleExists) {
            event.reply("Роль с эмодзи " + emoji + " уже существует").setEphemeral(true).queue();
            return;
        }
        
        // Добавляем новую роль
        Roles newRole = new Roles();
        newRole.setRoleId(roleId);
        newRole.setChannelId(channelId);
        newRole.setEmojiId(emoji);
        
        config.getRoles().add(newRole);
        jsonHandler.write(guild, config);
        
        logger.info("Гильдия {}: добавлена роль {} с эмодзи {}", 
            guild.getName(), 
            roleOption.getAsRole().getName(),
            emoji);
        
        event.reply("Роль " + roleOption.getAsRole().getAsMention() + 
            " успешно добавлена для выдачи по эмодзи " + emoji + 
            " в канале " + channelOption.getAsChannel().getAsMention())
            .setEphemeral(true)
            .queue();
    }

    private void handleRemoveRole(SlashCommandInteractionEvent event, Guild guild) {
        OptionMapping roleOption = event.getOption("role");
        
        if (roleOption == null) {
            event.reply("Необходимо указать роль").setEphemeral(true).queue();
            return;
        }
        
        String roleId = roleOption.getAsRole().getId();
        
        // Удаляем роль из конфигурации
        ServerRules config = jsonHandler.read(guild);
        if (config.getRoles() == null || config.getRoles().isEmpty()) {
            event.reply("В конфигурации нет настроенных ролей").setEphemeral(true).queue();
            return;
        }
        
        boolean removed = config.getRoles().removeIf(r -> r.getRoleId().equals(roleId));
        
        if (!removed) {
            event.reply("Роль " + roleOption.getAsRole().getAsMention() + " не найдена в конфигурации")
                .setEphemeral(true)
                .queue();
            return;
        }
        
        jsonHandler.write(guild, config);
        
        logger.info("Гильдия {}: удалена роль {}", 
            guild.getName(), 
            roleOption.getAsRole().getName());
        
        event.reply("Роль " + roleOption.getAsRole().getAsMention() + " успешно удалена из автовыдачи")
            .setEphemeral(true)
            .queue();
    }

    private void handleMonitoring(SlashCommandInteractionEvent event, Guild guild) {
        String action = event.getOption("action", "", OptionMapping::getAsString);
        
        if (action.isBlank()) {
            event.reply("Необходимо указать действие (enable/disable)").setEphemeral(true).queue();
            return;
        }
        
        ServerRules config = jsonHandler.read(guild);
        
        switch (action.toLowerCase()) {
            case "enable":
                OptionMapping channelOption = event.getOption("channel");
                if (channelOption == null) {
                    event.reply("Для включения мониторинга необходимо указать канал").setEphemeral(true).queue();
                    return;
                }
                
                String channelId = channelOption.getAsChannel().getId();
                config.setMonitoringChannelId(channelId);
                
                if (config.getServers() == null || config.getServers().isEmpty()) {
                    event.reply("Мониторинг настроен, но для работы необходимо добавить серверы " +
                        "с помощью команды `/guild-config add-server`")
                        .setEphemeral(true)
                        .queue();
                } else {
                    // Запускаем мониторинг
                    MonitoringManager.getInstance().startMonitoring(guild);
                    event.reply("Мониторинг серверов включен в канале " + 
                        channelOption.getAsChannel().getAsMention())
                        .setEphemeral(true)
                        .queue();
                }
                break;
                
            case "disable":
                // Останавливаем мониторинг
                MonitoringManager.getInstance().stopMonitoring(guild);
                event.reply("Мониторинг серверов выключен")
                    .setEphemeral(true)
                    .queue();
                break;
                
            default:
                event.reply("Неизвестное действие: " + action + ". Доступные действия: enable, disable")
                    .setEphemeral(true)
                    .queue();
        }
    }

    private void handleAddServer(SlashCommandInteractionEvent event, Guild guild) {
        String name = event.getOption("name", "", OptionMapping::getAsString);
        String host = event.getOption("host", "", OptionMapping::getAsString);
        int port = event.getOption("port", 0, OptionMapping::getAsInt);
        
        if (name.isBlank() || host.isBlank() || port <= 0) {
            event.reply("Необходимо указать название сервера, хост и порт").setEphemeral(true).queue();
            return;
        }
        
        ServerRules config = jsonHandler.read(guild);
        if (config.getServers() == null) {
            config.setServers(new ArrayList<>());
        }
        
        // Проверяем, существует ли уже сервер с таким именем
        boolean serverExists = config.getServers().stream()
            .anyMatch(s -> s.getName().equalsIgnoreCase(name));
        
        if (serverExists) {
            event.reply("Сервер с именем " + name + " уже существует").setEphemeral(true).queue();
            return;
        }
        
        // Добавляем новый сервер
        ServerInfo newServer = new ServerInfo();
        newServer.setName(name);
        newServer.setIp(host);
        newServer.setPort(port);
        
        config.getServers().add(newServer);
        jsonHandler.write(guild, config);
        
        logger.info("Гильдия {}: добавлен сервер {} ({}:{})", 
            guild.getName(), name, host, port);
        
        event.reply("Сервер " + name + " (" + host + ":" + port + ") успешно добавлен для мониторинга")
            .setEphemeral(true)
            .queue();
        
        // Если мониторинг активен, перезапускаем его с новой конфигурацией
        if (config.isMonitoringEnabled() && MonitoringManager.getInstance().isMonitoringActive(guild)) {
            MonitoringManager.getInstance().startMonitoring(guild);
        }
    }

    private void handleRemoveServer(SlashCommandInteractionEvent event, Guild guild) {
        String serverName = event.getOption("name").getAsString();
        
        // Получаем текущую конфигурацию
        ServerRules config = jsonHandler.read(guild);
        if (config == null) {
            event.reply("Ошибка при чтении конфигурации гильдии").setEphemeral(true).queue();
            return;
        }
        
        // Удаляем сервер из списка
        boolean removed = config.getServers().removeIf(server -> 
            server.getName().equalsIgnoreCase(serverName));
        
        if (removed) {
            // Сохраняем обновленную конфигурацию
            jsonHandler.write(guild, config);
            logger.info("Гильдия {}: сервер {} удален из мониторинга", guild.getName(), serverName);
            event.reply("Сервер " + serverName + " успешно удален из мониторинга!")
                .setEphemeral(true)
                .queue();
        } else {
            event.reply("Сервер " + serverName + " не найден в списке мониторинга")
                .setEphemeral(true)
                .queue();
        }
    }

    private void handleEditRules(SlashCommandInteractionEvent event, Guild guild) {
        // Получаем текущие правила
        RulesMessage rules = jsonHandler.readRules(guild);
        boolean changed = false;
        
        // Проверяем, какие поля были изменены
        OptionMapping titleOption = event.getOption("title");
        if (titleOption != null) {
            String title = titleOption.getAsString();
            if (!title.equals(rules.getTitle())) {
                rules.setTitle(title);
                changed = true;
            }
        }
        
        OptionMapping welcomeOption = event.getOption("welcome");
        if (welcomeOption != null) {
            String welcome = welcomeOption.getAsString();
            if (!welcome.equals(rules.getWelcomeField())) {
                rules.setWelcomeField(welcome);
                changed = true;
            }
        }
        
        OptionMapping rulesOption = event.getOption("rules");
        if (rulesOption != null) {
            String rulesText = rulesOption.getAsString();
            if (!rulesText.equals(rules.getRulesField())) {
                // Используем новый метод для обработки переносов строк
                rules.setFormattedRulesField(rulesText);
                changed = true;
            }
        }
        
        OptionMapping footerOption = event.getOption("footer");
        if (footerOption != null) {
            String footer = footerOption.getAsString();
            if (!footer.equals(rules.getFooter())) {
                rules.setFooter(footer);
                changed = true;
            }
        }
        
        // Если что-то изменилось, сохраняем правила
        if (changed) {
            try {
                jsonHandler.writeRules(guild, rules);
                logger.info("Гильдия {}: правила сервера обновлены", guild.getName());
                event.reply("Правила сервера успешно обновлены!")
                    .setEphemeral(true)
                    .queue();
            } catch (Exception e) {
                logger.error("Ошибка при обновлении правил сервера: {}", e.getMessage());
                event.reply("Произошла ошибка при обновлении правил сервера: " + e.getMessage())
                    .setEphemeral(true)
                    .queue();
            }
        } else {
            event.reply("Ничего не было изменено в правилах сервера.")
                .setEphemeral(true)
                .queue();
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        // Не используется для этой команды
    }
} 