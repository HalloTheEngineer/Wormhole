package de.spacecity.wormhole.mixin;

import de.spacecity.wormhole.Wormhole;
import de.spacecity.wormhole.utils.DiscordWebhook;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;

@Mixin(ServerPlayNetworkHandler.class)
public class ChatMixin {
    @Shadow public ServerPlayerEntity player;

    @Inject(method = "handleDecoratedMessage", at = @At("RETURN"))
    public void onChatMessage(SignedMessage message, CallbackInfo ci) throws IOException {
        if (!Wormhole.ENABLED) return;
        if (!Wormhole.USING_BOT) {
            Wormhole.WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject().setDescription(message.getContent().getString())
                    .setAuthor(player.getName().getString(), "https://namemc.com/profile/" + player.getName().getString(),
                            "https://minotar.net/cube/" + player.getName().getString())
            );
            Wormhole.WEBHOOK.execute();
        } else {
            Wormhole.CHANNEL.createMessage(
                    EmbedData.builder().author(EmbedAuthorData.builder().name(player.getName()
                    .getString()).url("https://namemc.com/profile/" + player.getName().getString())
                    .iconUrl("https://minotar.net/cube/" + player.getName().getString()
                    ).build()).description(message.getContent().getString()).build()
            ).block();
        }

    }
}
