package ru.discordj.bot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
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
import org.springframework.context.ApplicationContext;

import static net.dv8tion.jda.api.utils.cache.CacheFlag.CLIENT_STATUS;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JdaConfig {
    private static JDA jda;
    private final CommandManager commandManager = new CommandManager();
    private List<Object> listeners = new ArrayList<>();

    @Value("${discord.token}")
    private String token;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public JDA jda() {
        initializeListeners();
        
        log.info("Initializing JDA with token: {}...", token.substring(0, 10));
        
        jda = JDABuilder.createDefault(token)
                .setEnabledIntents(getRequiredIntents())
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CLIENT_STATUS, VOICE_STATE, CacheFlag.EMOJI)
                .build();

        listeners.stream()
            .filter(listener -> listener instanceof net.dv8tion.jda.api.hooks.EventListener)
            .forEach(jda::addEventListener);
        
        if (commandManager instanceof net.dv8tion.jda.api.hooks.EventListener) {
            jda.addEventListener(commandManager);
        }

        return jda;
    }

    private void initializeListeners() {
        List<ICommand> commands = applicationContext.getBeansOfType(ICommand.class).values().stream().toList();
        this.listeners = Arrays.asList(
            applicationContext.getBean(ReadyListener.class),
            applicationContext.getBean(MemberListener.class),
            applicationContext.getBean(AddRoleListener.class),
            applicationContext.getBean(MusicControlHandler.class),
            applicationContext.getBean(Configurator.class)
        );
        commands.forEach(commandManager::add);
        log.info("Registered {} slash commands", commands.size());
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
            GatewayIntent.GUILD_EMOJIS_AND_STICKERS,
            GatewayIntent.SCHEDULED_EVENTS,
            GatewayIntent.MESSAGE_CONTENT
        );
    }
}

