package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.embed.EmbedFactory;
import ru.discordj.bot.lavaplayer.PlayerManager;

public class PlayerButtonListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getButton().getId();
        if (buttonId == null) return;
        
        var guild = event.getGuild();
        if (guild == null) return;
        
        var musicManager = PlayerManager.getInstance().getGuildMusicManager(guild);
        var trackScheduler = musicManager.getTrackScheduler();
        
        switch (buttonId) {
            case "play_pause":
                trackScheduler.togglePause();
                break;
            case "stop":
                trackScheduler.stop();
                guild.getAudioManager().closeAudioConnection();
                break;
            case "repeat":
                trackScheduler.toggleRepeat();
                break;
            case "skip":
                trackScheduler.skip();
                break;
        }
        
        // Обновляем сообщение с плеером
        EmbedFactory.getInstance().createMusicEmbed()
            .updatePlayerMessage(event.getChannel().asTextChannel(), 
                trackScheduler.getPlayerMessageId());
            
        // Подтверждаем взаимодействие
        event.deferEdit().queue();
    }
}


