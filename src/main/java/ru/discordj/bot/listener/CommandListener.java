package ru.discordj.bot.listener;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;
import ru.discordj.bot.service.MusicService;
import ru.discordj.bot.service.RoleService;

@Component
public class CommandListener extends ListenerAdapter {
    
    private final MusicService musicService;
    private final RoleService roleService;
    
    public CommandListener(MusicService musicService, RoleService roleService) {
        this.musicService = musicService;
        this.roleService = roleService;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        
        String message = event.getMessage().getContentRaw();
        String[] args = message.split("\\s+");
        
        switch (args[0].toLowerCase()) {
            case "!play":
                if (args.length > 1) {
                    musicService.play(
                        event.getGuild().getId(),
                        event.getChannel().getId(),
                        args[1]
                    );
                } else {
                    event.getChannel().sendMessage("Использование: !play <URL>").queue();
                }
                break;
                
            case "!pause":
                musicService.pause(event.getGuild().getId());
                event.getChannel().sendMessage("⏸ Воспроизведение приостановлено").queue();
                break;
                
            case "!resume":
                musicService.resume(event.getGuild().getId());
                event.getChannel().sendMessage("▶ Воспроизведение возобновлено").queue();
                break;
                
            case "!skip":
                musicService.skip(event.getGuild().getId());
                event.getChannel().sendMessage("⏭ Трек пропущен").queue();
                break;
                
            case "!stop":
                musicService.stop(event.getGuild().getId());
                event.getChannel().sendMessage("⏹ Воспроизведение остановлено").queue();
                break;
                
            case "!np":
            case "!playing":
                String trackInfo = musicService.getCurrentTrackInfo(event.getGuild().getId());
                event.getChannel().sendMessage(trackInfo).queue();
                break;
        }
    }
} 