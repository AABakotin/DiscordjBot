package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.config.embed.EmbedCreation;
import ru.discordj.bot.events.lavaplayer.PlayerManager;

public class ButtonListener extends ListenerAdapter {


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        PlayerManager.get().getGuildMusicManager(event.getGuild()).getTrackScheduler().removeTrack(event.getButton().getId());
        event.getChannel().deleteMessageById(event.getMessageId()).queue();
        EmbedCreation.playListEmbed(event.getChannel().asTextChannel());
    }

}
