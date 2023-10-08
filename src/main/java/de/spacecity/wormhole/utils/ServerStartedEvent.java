package de.spacecity.wormhole.utils;

import de.spacecity.wormhole.Wormhole;
import discord4j.discordjson.json.EmbedData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;

public class ServerStartedEvent implements ServerLifecycleEvents.ServerStarted {
    @Override
    public void onServerStarted(MinecraftServer server) {
        Wormhole.SERVER = server;
        if (!Wormhole.ENABLED) return;
        if (Wormhole.USING_BOT)
            Wormhole.CHANNEL.createMessage(
                        EmbedData.builder().title(" 🟢 Server started at " + Time.valueOf(LocalTime.now())).build())
                    .block();
        else {
            try {
                Wormhole.WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Server started at " + Time.valueOf(LocalTime.now())));
                Wormhole.WEBHOOK.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
