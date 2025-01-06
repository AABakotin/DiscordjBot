package ru.discordj.bot.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.listener.AddRoleListener;
import ru.discordj.bot.events.listener.PlayerButtonListener;
import ru.discordj.bot.events.listener.configurator.Configurator;
import ru.discordj.bot.events.slashcommands.*;
import ru.discordj.bot.events.listener.MusicControlsListener;
import ru.discordj.bot.events.listener.MemberListener;
import ru.discordj.bot.events.listener.ReadyListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.CLIENT_STATUS;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;

@Configuration
public class JdaConfig {
    private static final Logger logger = LoggerFactory.getLogger(JdaConfig.class);
    private static final CommandManager MANAGER = new CommandManager();
    private static JDA jda;

    @Value("${discord.token}")
    private String token;

    @Autowired
    private ReadyListener readyListener;
    
    @Autowired
    private MemberListener memberListener;

    @Autowired
    private PlayMusicSlashCommand playMusicCommand;

    @Autowired
    private AddRoleListener addRoleListener;
    
    @Autowired
    private PlayerButtonListener playerButtonListener;
    
    @Autowired
    private Configurator configurator;
    
    @Autowired
    private MusicControlsListener musicControlsListener;

    @Autowired
    private PingSlashcommand pingCommand;
    
    @Autowired
    private RulesSlashcommand rulesCommand;
    
    @Autowired
    private InfoSlashcommand infoCommand;
    
    @Autowired
    private HelloSlashcommand helloCommand;
    
    @Autowired
    private InviteSlashcommand inviteCommand;
    
    @Autowired
    private UpdateCommandsSlashCommand updateCommand;

    @PostConstruct
    public void init() {
        MANAGER.add(pingCommand);
        MANAGER.add(rulesCommand);
        MANAGER.add(infoCommand);
        MANAGER.add(helloCommand);
        MANAGER.add(inviteCommand);
        MANAGER.add(playMusicCommand);
        MANAGER.add(updateCommand);
    }

    @Bean
    public JDA jda() {
        logger.info("Initializing JDA with token: {}...", token.substring(0, 10));
        
        return JDABuilder.createDefault(token)
                .setEnabledIntents(
                        GUILD_PRESENCES,
                        GUILD_MESSAGES,
                        GUILD_MEMBERS,
                        GUILD_MESSAGE_REACTIONS,
                        GUILD_MESSAGES,
                        GUILD_EXPRESSIONS,
                        SCHEDULED_EVENTS,
                        GUILD_VOICE_STATES,
                        MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CLIENT_STATUS, VOICE_STATE)
                .addEventListeners(
                        MANAGER,
                        addRoleListener,
                        playerButtonListener,
                        configurator,
                        musicControlsListener,
                        memberListener,
                        readyListener
                )
                .build();
    }

    public static JDA getJda() {
        return jda;
    }

}

