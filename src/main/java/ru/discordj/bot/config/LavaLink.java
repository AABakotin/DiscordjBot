package ru.discordj.bot.config;

import dev.arbjerg.lavalink.client.Helpers;
import dev.arbjerg.lavalink.client.LavalinkClient;
import dev.arbjerg.lavalink.client.LavalinkNode;
import dev.arbjerg.lavalink.client.NodeOptions;
import dev.arbjerg.lavalink.client.event.EmittedEvent;
import dev.arbjerg.lavalink.client.loadbalancing.RegionGroup;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import dev.arbjerg.lavalink.client.event.TrackStartEvent;
import dev.arbjerg.lavalink.client.event.StatsEvent;

import java.net.URI;
import java.util.List;

public class LavaLink extends ListenerAdapter {

    private LavalinkClient client;

    public LavaLink(String token) {
        this.client = new LavalinkClient(
                Helpers.getUserIdFromToken(token)
        );
        this.registerLavalinkListeners();
        this.registerLavalinkNodes();
    }

    private void registerLavalinkNodes() {
        List.of(
            /*client.addNode(
                "Testnode",
                URI.create("ws://localhost:2333"),
                "youshallnotpass",
                RegionGroup.EUROPE
            ),*/

                client.addNode(new NodeOptions.Builder().setName("Lavalink")
                        .setServerUri(URI.create("ws://localhost:2333"))
                        .setPassword("297813")
                        .setRegionFilter(RegionGroup.EUROPE)
                        .setHttpTimeout(5000L)
                        .build()
                )
        ).forEach((node) -> {
            node.on(TrackStartEvent.class).subscribe((event) -> {
                final LavalinkNode node1 = event.getNode();

                System.out.printf(
                        "%s: track started: %s%n",
                        node1.getName(),
                        event.getTrack().getInfo()
                );
            });
        });
    }

    private void registerLavalinkListeners() {
        this.client.on(dev.arbjerg.lavalink.client.event.ReadyEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            System.out.printf(
                    "Node '%s' is ready, session id is '%s'!%n",
                    node.getName(),
                    event.getSessionId()
            );
        });

        this.client.on(StatsEvent.class).subscribe((event) -> {
            final LavalinkNode node = event.getNode();

            System.out.printf(
                    "Node '%s' has stats, current players: %d/%d%n",
                    node.getName(),
                    event.getPlayingPlayers(),
                    event.getPlayers()
            );
        });

        this.client.on(EmittedEvent.class).subscribe((event) -> {
            if (event instanceof TrackStartEvent) {
                System.out.println("Is a track start event!");
            }

            final var node = event.getNode();

            System.out.printf(
                    "Node '%s' emitted event: %s%n",
                    node.getName(),
                    event
            );
        });
    }

    public LavalinkClient getClient() {
        return client;
    }
}
