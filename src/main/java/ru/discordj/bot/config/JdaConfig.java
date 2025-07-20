package ru.discordj.bot.config;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.utility.IJsonHandler;
import ru.discordj.bot.utility.JsonParse;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.listener.AddRoleListener;
import ru.discordj.bot.events.listener.PlayerButtonListener;
import ru.discordj.bot.events.listener.VoiceChannelListener;
import ru.discordj.bot.events.slashcommands.*;
import ru.discordj.bot.events.listener.MusicControlsListener;
import ru.discordj.bot.events.listener.MemberListener;
import ru.discordj.bot.events.listener.ReadyListener;


import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.CLIENT_STATUS;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;

public class JdaConfig {
    private static final Logger logger = LoggerFactory.getLogger(JdaConfig.class);
    private static final CommandManager MANAGER = new CommandManager();
    public static IJsonHandler jsonHandler = JsonParse.getInstance();
    private static JDA jda;


    static {

        MANAGER.add(new PingSlashcommand());
        MANAGER.add(new RulesSlashcommand());
        MANAGER.add(new InfoSlashcommand());
        MANAGER.add(new HelloSlashcommand());
        MANAGER.add(new InviteSlashcommand());
        MANAGER.add(new PlayMusicSlashCommand());
        MANAGER.add(new UpdateCommandsSlashCommand());
        MANAGER.add(new GuildConfigSlashCommand());

    }

    private JdaConfig (){
        throw new IllegalStateException("Configuration class");
    }

    public static void start(String[] args) {
        jda = JDABuilder.createDefault(checkToken(args))
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
                        new AddRoleListener(),
                        new PlayerButtonListener(),
                        new MusicControlsListener(),
                        new MemberListener(),
                        new ReadyListener(),
                        new VoiceChannelListener()
                )
                .build();
    }

    private static String checkToken(String[] args) {
        if (args.length >= 1) {
            logger.info("Loading token key from args...");
            return args[0];
        } else if (System.getenv().containsKey("DISCORD_TOKEN")) {
            logger.info("Loading token key from system environment (DISCORD_TOKEN)...");
            return System.getenv("DISCORD_TOKEN");
        } else {
            logger.error("No token provided in args or environment variables! Please provide a token.");
            throw new IllegalArgumentException("Discord bot token is required. Provide it as first argument or set DISCORD_TOKEN environment variable.");
        }
    }

    public static JDA getJda() {
        return jda;
    }
    
    /**
     * Инициализирует компоненты бота после полной загрузки JDA
     */
    public static void initializeComponents() {
        // Инициализация мониторинга для всех гильдий
        MonitoringManager.getInstance().initForAllGuilds(jda.getGuilds());
        
        logger.info("Компоненты бота инициализированы");
    }

}

