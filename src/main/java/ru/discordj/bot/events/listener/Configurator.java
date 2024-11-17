package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static ru.discordj.bot.config.Constant.ADMIN_CHANNEL;

public class Configurator extends ListenerAdapter {


    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (event.getMessage().getChannel().getId().equals(ADMIN_CHANNEL)) {
            event.getChannel().sendMessage("Привет, тут можно настроить Мониторинг севрверов").queue();

        }
    }

}
