package ru.discordj.bot.utility;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MessageCollector {
    private final Predicate<Message> filter;
    private final Consumer<Message> action;
    private final ScheduledFuture<?> timeout;

    public MessageCollector(MessageChannel channel, Predicate<Message> filter, Consumer<Message> action, int seconds) {
        this.filter = filter;
        this.action = action;
        this.timeout = channel.getJDA().getGatewayPool().scheduleAtFixedRate(
            this::stop,
            seconds,
            seconds,
            TimeUnit.SECONDS
        );
    }

    public void handleMessage(Message message) {
        if (filter.test(message)) {
            action.accept(message);
            stop();
        }
    }

    private void stop() {
        if (!timeout.isDone()) {
            timeout.cancel(false);
        }
    }

    public static MessageCollector create(MessageChannel channel, User user, Consumer<Message> action, int seconds) {
        return new MessageCollector(
            channel,
            message -> message.getAuthor().equals(user),
            action,
            seconds
        );
    }
} 