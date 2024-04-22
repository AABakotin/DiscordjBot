package ru.discordj.bot.events.slashcommands.music;

import dev.arbjerg.lavalink.client.FunctionalLoadResultHandler;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.Link;
import dev.arbjerg.lavalink.client.player.Track;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.discordj.bot.config.JDA;
import ru.discordj.bot.config.LavaLink;
import ru.discordj.bot.events.ICommand;
import ru.discordj.bot.events.lavaplayer.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Play implements ICommand {
    private static final Logger logger = LoggerFactory.getLogger(Play.class);

    @Override
    public String getName() {
        return "play";
    }

    @Override
    public String getDescription() {
        return "Will play a song";
    }

    @Override
    public List<OptionData> getOptions() {
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.STRING, "name", "Name of the song to play", true));
        return options;
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        final Guild guild = event.getGuild();

        // We are already connected, go ahead and play
        if (guild.getSelfMember().getVoiceState().inAudioChannel()) {
            event.deferReply(false).queue();
        } else {
            // Connect to VC first
            joinHelper(event);
        }

        String identifier = event.getOption("name").getAsString();
        final long guildId = guild.getIdLong();
        final Link link = JDA.getLavalink().getClient().getOrCreateLink(guildId);



        if (!isUrl(identifier)) {
            identifier = "ytsearch:" + identifier;
        }

        link.loadItem(identifier).subscribe(new FunctionalLoadResultHandler(
                // Track loaded
                (trackLoad) -> {
                    final Track track = trackLoad.getTrack();

                    // Inner class at the end of this file
                    final var userData = new MyUserData(event.getUser().getIdLong());

                    track.setUserData(userData);

                    // there are a few ways of updating the player! Just pick whatever you prefer
                    if (new Random().nextBoolean()) {
                        link.getPlayer()
                                .flatMap(
                                        (p) -> p.setTrack(track).setVolume(35)
                                )
                                .subscribe((player) -> {
                                    final Track playingTrack = player.getTrack();
                                    final var trackTitle = playingTrack.getInfo().getTitle();
                                    final MyUserData customData = playingTrack.getUserData(MyUserData.class);

                                    event.getHook().sendMessage("Now playing: " + trackTitle + "\nRequested by: <@" + customData.requester() + '>').queue();
                                });
                    } else {
                        link.createOrUpdatePlayer()
                                .setTrack(track)
                                .setVolume(35)
                                .subscribe((player) -> {
                                    final Track playingTrack = player.getTrack();
                                    final var trackTitle = playingTrack.getInfo().getTitle();
                                    final MyUserData customData = playingTrack.getUserData(MyUserData.class);

                                    event.getHook().sendMessage("Now playing: " + trackTitle + "\nRequested by: <@" + customData.requester() + '>').queue();
                                });
                    }
                },
                null, // playlist loaded
                // search result loaded
                (search) -> {
                    final List<Track> tracks = search.getTracks();

                    if (tracks.isEmpty()) {
                        event.getHook().sendMessage("No tracks found!").queue();
                        return;
                    }

                    final Track firstTrack = tracks.get(0);

                    // This is a different way of updating the player! Choose your preference!
                    // This method will also create a player if there is not one in the server yet
                    link.updatePlayer((update) -> update.setTrack(firstTrack).setVolume(35))
                            .subscribe((ignored) -> {
                                event.getHook().sendMessage("Now playing: " + firstTrack.getInfo().getTitle()).queue();
                            });
                },
                null, // no matches
                null // load failed
        ));
    }

    private void joinHelper(SlashCommandInteractionEvent event) {
        final Member member = event.getMember();
        final GuildVoiceState memberVoiceState = member.getVoiceState();

        if (memberVoiceState.inAudioChannel()) {
            event.getJDA().getDirectAudioController().connect(memberVoiceState.getChannel());
        }

        event.reply("Joining your channel!").queue();
    }

    record MyUserData(long requester) {}


    private boolean isUrl(String link) {
        try {
            new URI(link);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
//        final Member member = event.getMember();
//        final GuildVoiceState memberVoiceState = member.getVoiceState();
//        final GuildVoiceState voiceState = event.getGuild().getSelfMember().getVoiceState();
//        final AudioManager audioManager = event.getGuild().getAudioManager();
//
//        if (!memberVoiceState.inAudioChannel()) {
//            event.reply("You need to be in a voice channel").setEphemeral(true).queue();
//            return;
//        }
//        if (!voiceState.inAudioChannel()) {
//            audioManager.openAudioConnection(memberVoiceState.getChannel());
//        } else if (voiceState.getChannel() != memberVoiceState.getChannel()) {
//            event.reply("You need to be in the same channel as me").setEphemeral(true).queue();
//            return;
//        }
//        String link = event.getOption("name").getAsString();
//
//        if (!isUrl(link)) {
//            link = "ytsearch:" + link;
//        }
//
//        event.reply("Adding to queue:").queue();
////        PlayerManager.get().play(event.getChannel().asTextChannel(), link);
//
//        event.getJDA().getDirectAudioController().connect(event.getChannel());
//
//        logger.info("Playing music for " + member.getUser().getName());
//    }

