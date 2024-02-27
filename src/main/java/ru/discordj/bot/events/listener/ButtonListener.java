package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.embed.IEmbed;
import ru.discordj.bot.embed.createEmbed.EmbedForm;
import ru.discordj.bot.events.lavaplayer.PlayerManager;

import java.util.Objects;

public class ButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        IEmbed iEmbed = new EmbedForm();
        event.getChannel().deleteMessageById(event.getChannel().getLatestMessageId()).queue(
               e -> event.reply(iEmbed.playListEmbed(event.getChannel().asTextChannel())).queue());
        PlayerManager.get().getGuildMusicManager(Objects.requireNonNull(event.getGuild())).getTrackScheduler().removeTrack(event);

    }
}


