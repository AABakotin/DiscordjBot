package ru.discordj.bot.embed;

import java.util.Map;
import java.awt.Color;
import java.util.Date;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import ru.discordj.bot.config.JdaConfig;

public class ServerStatusEmbed extends BaseEmbed {
    private static final String TEST_CHANNEL = "1300539782874529802";
    
        public void embedServerStatus(Map<String, String> receive) {
            MessageCreateBuilder messageCreateBuilder = new MessageCreateBuilder();
            EmbedBuilder builder = new EmbedBuilder();
    
            builder
                .setTitle("█▓▒░⡷⠂Monitoring Games Servers⠐⢾░▒▓█")
                .setColor(Color.BLUE)
                .setFooter("📩 " + "requested by @" + "author" + " " + new Date());
            receive.forEach((key, value) -> builder.addField(key, value, true));
    
            messageCreateBuilder.setEmbeds(builder.build());
    
            TextChannel textChannelById = JdaConfig.getJda().getTextChannelById(TEST_CHANNEL);

        if (textChannelById != null) {
            MessageCreateAction message = textChannelById.sendMessage(messageCreateBuilder.build());
            String latestMessageId = textChannelById.getLatestMessageId();
            textChannelById.deleteMessageById(latestMessageId)
                .queue(ok -> message.queue(), not -> message.queue());
        }
    }
} 