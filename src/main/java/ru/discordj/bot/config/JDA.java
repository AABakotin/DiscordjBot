package ru.discordj.bot.config;

import ch.qos.logback.core.subst.Token;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.commands.*;
import ru.discordj.bot.events.listener.AddRole;

import static ru.discordj.bot.config.Constant.*;

public class JDA {
    private static final CommandManager MANAGER = new CommandManager();

    static {
        MANAGER.add(new Ping());
        MANAGER.add(new Ban());
        MANAGER.add(new RulesInfo());
        MANAGER.add(new Info());
        MANAGER.add(new Hello());
    }

    public static void start() {
        JDABuilder.createLight(System.getenv("TOKEN"))
                .setActivity(Activity.watching("за твоим поведением"))
                .setEnabledIntents(
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.CLIENT_STATUS)
                .addEventListeners(
                        MANAGER,
                        new AddRole())
                .build();
    }
}

