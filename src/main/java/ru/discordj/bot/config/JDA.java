package ru.discordj.bot.config;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.commands.*;
import ru.discordj.bot.events.commands.music.*;
import ru.discordj.bot.events.listener.AddRole;

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

        stringRoleMap.put(EMOJI_ACCESS, ROLE_ACCESS);
        stringRoleMap.put(EMOJI_JAVA, ROLE_JAVA);
    }

    public static void start(String[] args) {
        JDABuilder.createLight(checkToken(args))
                .setActivity(Activity.watching("за твоим поведением"))
                .setEnabledIntents(
                        GUILD_PRESENCES,
                        GUILD_MESSAGES,
                        GUILD_MEMBERS,
                        GUILD_MESSAGE_REACTIONS,
                        GUILD_VOICE_STATES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CLIENT_STATUS, VOICE_STATE)
                .addEventListeners(
                        MANAGER,
                        new AddRole())
                .build();
    }

    private static String checkToken(String[] args) {
            if (args.length >= 1) {
                logger.info("Loading token key form args...");
                return args[0];
            }
            if (TOKEN_FROM_ENV.isEmpty()) {
                logger.info("Loading token key form ENV...");
               return   System.getenv("TOKEN");
            }
            else {
                logger.info("Loading token key form file...");
                return TOKEN_FROM_ENV;
            }

    }
    public static String getRoleToEmoji(String emoji){
            return stringRoleMap.get(emoji);
    }

}

