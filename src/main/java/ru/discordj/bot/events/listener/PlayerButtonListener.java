package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.embed.EmbedFactory;

public class PlayerButtonListener extends ListenerAdapter {


    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        EmbedFactory.getInstance().createMusicEmbed()
            .createPlayerMessage(event.getChannel().asTextChannel());
    }
}


