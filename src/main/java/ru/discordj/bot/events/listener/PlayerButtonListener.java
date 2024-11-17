package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.embed.EmbedCreation;
import ru.discordj.bot.lavaplayer.PlayerManager;

public class PlayerButtonListener extends ListenerAdapter {


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
            PlayerManager.getInstance().getGuildMusicManager(event.getGuild()).getTrackScheduler().removeTrack(event.getButton().getId());
            event.getChannel().deleteMessageById(event.getMessageId()).queue();
            EmbedCreation.get().playListEmbed(event.getChannel().asTextChannel());
        }
    }


