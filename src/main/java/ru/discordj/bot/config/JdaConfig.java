package ru.discordj.bot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.events.listener.AddRoleListener;
import ru.discordj.bot.events.listener.MusicControlHandler;
import ru.discordj.bot.events.listener.configurator.Configurator;
import ru.discordj.bot.events.listener.MemberListener;
import ru.discordj.bot.events.listener.ReadyListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collection;

import static net.dv8tion.jda.api.utils.cache.CacheFlag.CLIENT_STATUS;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JdaConfig {
    private static JDA jda;
    private final CommandManager commandManager;
    private final List<ICommand> commands;
    private final List<Object> listeners;

    @Value("${discord.token}")
    private String token;

    @Autowired
    public JdaConfig(
            ReadyListener readyListener,
            MemberListener memberListener,
            AddRoleListener addRoleListener,
            MusicControlHandler musicControlHandler,
            Configurator configurator,
            List<ICommand> commands 
    ) {
        this.commandManager = new CommandManager();
        this.commands = commands;
        this.listeners = Arrays.asList(
            readyListener,
            memberListener,
            addRoleListener,
            musicControlHandler,
            configurator
        );
    }

    @PostConstruct
    public void init() {
        commands.forEach(commandManager::add);
        log.info("Registered {} slash commands", commands.size());
    }

    @Bean
    public JDA jda() {
        log.info("Initializing JDA with token: {}...", token.substring(0, 10));
        
        jda = JDABuilder.createDefault(token)
                .setEnabledIntents(getRequiredIntents())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CLIENT_STATUS, VOICE_STATE)
                .addEventListeners(getEventListeners())
                .build();
        return jda;
    }

    public static JDA getJda() {
        return jda;
    }

    private Collection<GatewayIntent> getRequiredIntents() {
        return Arrays.asList(
            GatewayIntent.GUILD_PRESENCES,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGE_REACTIONS,
            GatewayIntent.GUILD_VOICE_STATES,
            GatewayIntent.MESSAGE_CONTENT
        );
    }

    private Object[] getEventListeners() {
        List<Object> allListeners = new ArrayList<>(listeners);
        allListeners.add(commandManager);
        return allListeners.toArray();
    }
}

