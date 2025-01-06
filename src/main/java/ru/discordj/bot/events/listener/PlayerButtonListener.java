package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.embed.EmbedFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerButtonListener extends ListenerAdapter {
    @Autowired
    private EmbedFactory embedFactory;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        embedFactory.createMusicEmbed()
            .createPlayerMessage(event.getChannel().asTextChannel());
    }
}


