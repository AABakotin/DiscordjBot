package ru.discordj.bot.events.listener;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import ru.discordj.bot.lavaplayer.PlayerManager;
import ru.discordj.bot.lavaplayer.TrackScheduler;
import ru.discordj.bot.lavaplayer.TrackSelectionData;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.discordj.bot.embed.EmbedFactory;

@Component
public class MusicControlsListener extends ListenerAdapter {
    @Autowired
    private EmbedFactory embedFactory;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonId = event.getComponentId();
        
        if (buttonId.startsWith("select:")) {
            handleTrackSelection(event);
            return;
        }
        
        TrackScheduler scheduler = PlayerManager.getInstance()
            .getGuildMusicManager(event.getGuild())
            .getTrackScheduler();
            
        if (buttonId.startsWith("delete:")) {
            int index = Integer.parseInt(buttonId.split(":")[1]) - 1;
            List<AudioTrack> tracks = new ArrayList<>(scheduler.getQueue());
            if (index >= 0 && index < tracks.size()) {
                scheduler.removeTrack(tracks.get(index).getInfo().title);
            }
            event.deferEdit().queue();
            return;
        } else {
            switch (buttonId) {
                case "play_pause":
                    scheduler.togglePause();
                    break;
                
                case "stop":
                    scheduler.stop();
                    event.getGuild().getAudioManager().closeAudioConnection();
                    break;
                
                case "repeat":
                    scheduler.toggleRepeat();
                    break;
                
                case "skip":
                    scheduler.skip();
                    break;
            }
        }
        
        // Обновляем сообщение после взаимодействия
        event.deferEdit().queue(response -> 
            embedFactory.createMusicEmbed()
                .updatePlayerMessage(event.getChannel().asTextChannel(), event.getMessage().getId())
        );
    }

    private void handleTrackSelection(ButtonInteractionEvent event) {
        TrackScheduler scheduler = PlayerManager.getInstance()
            .getGuildMusicManager(event.getGuild())
            .getTrackScheduler();
        
        TrackSelectionData selectionData = scheduler.getTrackSelectionData();
        if (selectionData != null && event.getMessage().getId().equals(selectionData.getMessageId())) {
            int index = Integer.parseInt(event.getComponentId().split(":")[1]);
            AudioTrack selectedTrack = selectionData.getTracks().get(index).makeClone();
            
            scheduler.queue(selectedTrack);
            event.getMessage().delete().queue();
            scheduler.setTrackSelectionData(null);
        }
        
        event.deferEdit().queue();
    }
} 