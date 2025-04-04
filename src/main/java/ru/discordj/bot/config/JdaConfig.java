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
import ru.discordj.bot.events.listener.configurator.Configurator;
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
        MANAGER.add(new RadioSlashCommand());
        MANAGER.add(new UpdateCommandsSlashCommand());
        MANAGER.add(new RadioReloadSlashCommand());
        MANAGER.add(new RadioAddSlashCommand());
        MANAGER.add(new RadioRemoveSlashCommand());
        MANAGER.add(new RadioListSlashCommand());

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
                        new Configurator(),
                        new MusicControlsListener(),
                        new MemberListener(),
                        new ReadyListener(),
                        new VoiceChannelListener()
                )
                .build();
    }

    private static String checkToken(String[] args) {
        if (args.length >= 1) {
            logger.info("Loading token key form args...");
            return args[0];
        } else if (System.getenv().containsKey("TOKEN")) {
            logger.info("Loading token key form system environment...");
            return System.getenv("TOKEN");
        } else {
            logger.info("Loading token key form properties file...");
            return jsonHandler.read().getToken();
        }
    }

    public static JDA getJda() {
        return jda;
    }

}

