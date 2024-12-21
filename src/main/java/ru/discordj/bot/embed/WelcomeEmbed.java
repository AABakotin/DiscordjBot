package ru.discordj.bot.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import ru.discordj.bot.utility.pojo.RulesMessage;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WelcomeEmbed extends BaseEmbed {

    public MessageEmbed embedWelcomeGuild(String avatarUrl, String username) {
        RulesMessage rules = jsonHandler.readRules();
        
        EmbedBuilder embed = new EmbedBuilder()
            .setColor(Color.decode("#2f3136"))
            .setThumbnail(avatarUrl);

        if (rules.getTitle() != null) {
            embed.setTitle(rules.getTitle());
        }
        
        if (rules.getWelcomeField() != null) {
            embed.addField("", rules.getWelcomeField(), false);
        }
        
        if (rules.getRulesField() != null) {
            embed.addField("", rules.getRulesField(), false);
        }
        
        if (rules.getFooter() != null) {
            String footer = rules.getFooter()
                .replace("{author}", username)
                .replace("{date}", new SimpleDateFormat("dd.MM.yyyy").format(new Date()));
            embed.setFooter(footer);
        }

        return embed.build();
    }

    public MessageEmbed embedLeaveGuild(String avatarUrl, String username) {
        String inviteLink = jsonHandler.read().getInviteLink();
        if (inviteLink == null || inviteLink.equals("empty")) {
            inviteLink = "https://discord.gg/invite";
        }

        return new EmbedBuilder()
            .setColor(Color.decode("#2f3136"))
            .setThumbnail(avatarUrl)
            .setTitle("До свидания!")
            .setDescription(String.format(
                "Пользователь **%s** покинул сервер 👋\n\n" +
                "Будем рады видеть вас снова!\n" +
                "Ссылка для возвращения: %s", 
                username, inviteLink))
            .build();
    }
}