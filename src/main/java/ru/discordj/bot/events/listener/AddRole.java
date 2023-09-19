package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.entities.RoleImpl;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddRole extends ListenerAdapter {

    private final Map<String, String> stringRoleMap = new HashMap<>();
    private final SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final  Date date = new Date();
    private final Long guestChannel = 848236644838408223L;


    {
        stringRoleMap.put("\uD83D\uDC4D", "888903807466086410"); // ThumbsUp / AccessRole
        stringRoleMap.put("☕","960975510647762995");
    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {

        User member = event.getMember().getUser();
        Guild guild = event.getGuild();
        GuildChannel guestGuildChannel = event.getGuild().getGuildChannelById(guestChannel);

        if (guestGuildChannel == event.getGuildChannel()) {
            String emoji = event.getEmoji().getName();
            if (stringRoleMap.containsKey(emoji)) {
                guild.addRoleToMember(member, event.getGuild().getRoleById(stringRoleMap.get(emoji))).queue();
             EmbedBuilder builder = new EmbedBuilder()
                     .setColor(Color.BLUE)
                     .setTitle("\"*TSD server*\"")
                     .addField("Добро пожаловать!", event.getUser().getName(), true)
                     .setImage(event.getGuild().getIconUrl())
                     .addField("Правила:", " 1. Не матерится (чуть можно)\n2. Не орать \n3. Уважать других\n4. Для отписки отжать палец вверх\n5. Для получения основной роли, напиши админу", false)
                     .setFooter("Send " + formatter.format(date), event.getGuild().getIconUrl());
                event.getUser().openPrivateChannel().complete().sendMessageEmbeds(builder.build()).queue();
            }
        }
    }
}
