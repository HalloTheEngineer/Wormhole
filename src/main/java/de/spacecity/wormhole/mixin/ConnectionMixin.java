package de.spacecity.wormhole.mixin;

import de.spacecity.wormhole.Wormhole;
import de.spacecity.wormhole.utils.DiscordWebhook;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(PlayerManager.class)
public class ConnectionMixin {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) throws IOException {
        if (!Wormhole.ENABLED) return;
        if (!Wormhole.USING_BOT) {
            Wormhole.WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject()
                    .setAuthor(player.getName().getString() + " joined the game.", "https://namemc.com/profile/" + player.getName().getString(),
                            "https://minotar.net/avatar/" + player.getName().getString())
            );
            Wormhole.WEBHOOK.execute();
        } else {
            Wormhole.CHANNEL.createMessage(
                    EmbedData.builder().author(EmbedAuthorData.builder().name(player.getName()
                                    .getString() + " joined the game.").url("https://namemc.com/profile/" + player.getName().getString())
                            .iconUrl("https://minotar.net/avatar/" + player.getName().getString()
                            ).build()).build()
            ).block();
        }
    }
    @Inject(method = "remove", at = @At("TAIL"))
    public void remove(ServerPlayerEntity player, CallbackInfo ci) throws IOException {
        if (!Wormhole.ENABLED) return;
        if (!Wormhole.USING_BOT) {
            Wormhole.WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject()
                    .setAuthor(player.getName().getString() + " left the game.", "https://namemc.com/profile/" + player.getName().getString(),
                            "https://minotar.net/avatar/" + player.getName().getString())
            );
            Wormhole.WEBHOOK.execute();
        } else {
            Wormhole.CHANNEL.createMessage(
                    EmbedData.builder().author(EmbedAuthorData.builder().name(player.getName()
                                    .getString() + " left the game.").url("https://namemc.com/profile/" + player.getName().getString())
                            .iconUrl("https://minotar.net/avatar/" + player.getName().getString()
                            ).build()).build()
            ).block();
        }
    }
}
