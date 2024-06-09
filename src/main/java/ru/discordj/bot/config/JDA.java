package ru.discordj.bot.config;


import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.listener.AddRoleListener;
import ru.discordj.bot.events.listener.PlayerButtonListener;
import ru.discordj.bot.events.slashcommands.*;
import ru.discordj.bot.events.slashcommands.music.*;

import java.util.HashMap;
import java.util.Map;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.CLIENT_STATUS;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;
import static ru.discordj.bot.config.Constant.*;

public class JDA {
    private static final Logger logger = LoggerFactory.getLogger(JDA.class);
    private static final CommandManager MANAGER = new CommandManager();
    private static final Map<String, String> stringRoleMap = new HashMap<>();


    static {

        MANAGER.add(new Ping());
        MANAGER.add(new Rules());
        MANAGER.add(new Info());
        MANAGER.add(new Hello());
        MANAGER.add(new Invite());

        MANAGER.add(new NowPlaying());
        MANAGER.add(new Play());
        MANAGER.add(new Queue());
        MANAGER.add(new Repeat());
        MANAGER.add(new Skip());
        MANAGER.add(new Stop());
        MANAGER.add(new ClearPlayList());

        stringRoleMap.put(EMOJI_ACCESS, ROLE_ACCESS);
        stringRoleMap.put(EMOJI_JAVA, ROLE_JAVA);
    }

    public static void start(String[] args) {
        JDABuilder.createLight(checkToken(args))
                .setEnabledIntents(
                        GUILD_PRESENCES,
                        GUILD_MESSAGES,
                        GUILD_MEMBERS,
                        GUILD_MESSAGE_REACTIONS,
                        GUILD_VOICE_STATES,
                        MESSAGE_CONTENT)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CLIENT_STATUS, VOICE_STATE)
                .addEventListeners(
                        MANAGER,
                        new AddRoleListener(),
                        new PlayerButtonListener())
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
            return TOKEN_FROM_FILE_PROPERTIES;
        }
    }


    public static String getRoleToEmoji(String emoji) {
        return stringRoleMap.get(emoji);
    }

}

