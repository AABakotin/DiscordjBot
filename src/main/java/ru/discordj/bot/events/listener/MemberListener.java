package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.pojo.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Слушатель для обработки событий присоединения новых участников к серверу.
 * Автоматически назначает роли новым участникам согласно конфигурации.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MemberListener extends ListenerAdapter {
    private static final String DEFAULT_ROLE_ID = "empty";
    private static final String LOG_MEMBER_JOIN = "Участник присоединился: {} ({}), сервер: {}";
    private static final String LOG_ROLE_ASSIGNED = "Роль {} назначена участнику {} на сервере {}";
    private static final String ERROR_ROLE_NOT_FOUND = "❌ Роль по умолчанию не найдена: {}";
    private static final String ERROR_ROLE_ASSIGN = "❌ Ошибка при назначении роли: {}";

    private final IJsonHandler jsonHandler;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            handleNewMember(event);
        } catch (Exception e) {
            log.error("Ошибка при обработке нового участника: {}", e.getMessage());
        }
    }

    /**
     * Обрабатывает присоединение нового участника
     */
    private void handleNewMember(GuildMemberJoinEvent event) {
        log.info(LOG_MEMBER_JOIN, 
            event.getMember().getUser().getName(),
            event.getMember().getId(),
            event.getGuild().getName()
        );

        Root config = jsonHandler.read();
        String defaultRoleId = config.getRoles().isEmpty() ? DEFAULT_ROLE_ID : config.getRoles().get(0).getRoleId();

        if (isValidRoleId(defaultRoleId)) {
            assignDefaultRole(event, defaultRoleId);
        }
    }

    /**
     * Проверяет валидность ID роли
     */
    private boolean isValidRoleId(String roleId) {
        return roleId != null && !roleId.equals(DEFAULT_ROLE_ID);
    }

    /**
     * Назначает роль по умолчанию новому участнику
     */
    private void assignDefaultRole(GuildMemberJoinEvent event, String roleId) {
        Role defaultRole = event.getGuild().getRoleById(roleId);
        
        if (defaultRole != null) {
            event.getGuild()
                .addRoleToMember(event.getMember(), defaultRole)
                .queue(
                    success -> logRoleAssigned(event, defaultRole),
                    error -> log.error(ERROR_ROLE_ASSIGN, error.getMessage())
                );
        } else {
            log.error(ERROR_ROLE_NOT_FOUND, roleId);
        }
    }

    /**
     * Логирует успешное назначение роли
     */
    private void logRoleAssigned(GuildMemberJoinEvent event, Role role) {
        log.info(LOG_ROLE_ASSIGNED,
            role.getName(),
            event.getMember().getUser().getName(),
            event.getGuild().getName()
        );
    }
} 