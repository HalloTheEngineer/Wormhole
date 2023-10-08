package de.spacecity.wormhole.utils;

import de.spacecity.wormhole.Wormhole;
import discord4j.discordjson.json.EmbedData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;

public class ServerStoppingEvent implements ServerLifecycleEvents.ServerStopping {
    @Override
    public void onServerStopping(MinecraftServer server) {
        if (!Wormhole.USING_BOT) {
            try {
                Wormhole.WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Server stopped at " + Time.valueOf(LocalTime.now())));
                Wormhole.WEBHOOK.execute();
            } catch (IOException e) {
                Wormhole.WEBHOOK.clear();
                throw new RuntimeException(e);
            }
        } else {
            Wormhole.CHANNEL.createMessage(
                    EmbedData.builder()
                            .title(" 🔴 Server stopped at " + Time.valueOf(LocalTime.now())).build()).block();
        }
        Wormhole.DISCORD_BOT.interrupt();
    }
}
