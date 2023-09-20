package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.Config;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AddRole extends ListenerAdapter {

    private static final Config config = new Config();
    private static final Logger logger = LoggerFactory.getLogger(AddRole.class);
    private final Map<String, String> stringRoleMap = new HashMap<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final Date date = new Date();

    {
        stringRoleMap.put(config.getEmojiAccess(), config.getRoleAccess());
        stringRoleMap.put(config.getEmojiJava(), config.getRoleJavaDevelopment());
    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(config.getGuestChannel());
        if (guestGuildChannel == event.getGuildChannel()) {
            String emoji = event.getEmoji().getName();
            if (stringRoleMap.containsKey(emoji)) {
                event.getGuild().addRoleToMember(event.getMember().getUser(), event.getGuild().getRoleById(stringRoleMap.get(emoji))).queue();

                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setTitle("\"*TSD server*\"")
                        .addField("Добро пожаловать!", event.getUser().getName(), true)
                        .setImage(event.getGuild().getIconUrl())
                        .addField("Правила:", " 1. Не матерится (чуть можно)\n2. Не орать \n3. Уважать других\n4. Для отписки отжать палец вверх\n5. Для получения основной роли, напиши админу", false)
                        .setFooter("Send " + formatter.format(date), event.getGuild().getIconUrl());
                event.getUser().openPrivateChannel().complete().sendMessageEmbeds(builder.build()).queue();
                logger.info("User " + event.getUser().getName() + " subscribe " + emoji);
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent event) {
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(config.getGuestChannel());
        if (guestGuildChannel == event.getGuildChannel()) {
            String emoji = event.getEmoji().getName();
            if (stringRoleMap.containsKey(emoji)) {
                event.getGuild()
                        .removeRoleFromMember(event.getMember().getUser(), event.getGuild().getRoleById(stringRoleMap.get(emoji)))
                        .queue();

                EmbedBuilder builder = new EmbedBuilder()
                        .setColor(Color.BLUE)
                        .setTitle("\"*TSD server*\"")
                        .addField("Прощай мой друг", event.getUser().getName(), true)
                        .setImage(event.getGuild().getIconUrl())
                        .addField("Ждем тебя снова!", "", false)
                        .setFooter("Send " + this.formatter.format(this.date), event.getGuild().getIconUrl());
                event.getUser().openPrivateChannel().complete().sendMessageEmbeds(builder.build()).queue();
                logger.info("User " + event.getUser().getName() + " unsubscribe " + emoji);
            }
        }
    }
}
