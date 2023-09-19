package ru.discordj.bot;


import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import ru.discordj.bot.events.CommandManager;
import ru.discordj.bot.events.commands.*;
import ru.discordj.bot.events.listener.AddRole;


public class Main extends ListenerAdapter {
   private static final CommandManager manager = new CommandManager();

    static {
        manager.add(new Ping());
        manager.add(new Ban());
        manager.add(new RulesInfo());
        manager.add(new Info());
        manager.add(new Hello());
    }
    public static void main(String[] args) {

        JDABuilder.createLight(args[0])
                .setActivity(Activity.playing("Testing MZF"))
                .setEnabledIntents(
                        GatewayIntent.GUILD_PRESENCES,
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL)
                .enableCache(CacheFlag.CLIENT_STATUS)
                .addEventListeners(manager,new AddRole())
                .build();
    }
}
