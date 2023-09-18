package ru.discordj.bot;


import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.commands.Ban;
import ru.discordj.bot.events.commands.Info;
import ru.discordj.bot.events.commands.Ping;
import ru.discordj.bot.events.commands.RulesInfo;

import java.util.Collections;

public class Main extends ListenerAdapter {


    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    public static void main(@NotNull String[] args) {
        CommandManager manager = new CommandManager();
        manager.add(new Ping());
        manager.add(new Ban());
        manager.add(new RulesInfo());
        manager.add(new Info());

        JDABuilder.createLight(args[0], Collections.emptyList())
                .setActivity(Activity.playing("Testing MZF"))

                .setEnabledIntents(
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.CLIENT_STATUS)
                .addEventListeners(manager)
                .build();


    }
}
