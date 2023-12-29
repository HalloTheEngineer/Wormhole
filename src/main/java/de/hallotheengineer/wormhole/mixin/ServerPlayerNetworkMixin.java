package de.hallotheengineer.wormhole.mixin;

import de.hallotheengineer.wormhole.Wormhole;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayerNetworkMixin {
    @Shadow public ServerPlayerEntity player;
    @Inject(method = "handleDecoratedMessage", at = @At("TAIL"))
    public void handleDecoratedMessage(SignedMessage message, CallbackInfo ci) {
        if (!message.isSenderMissing() && message.hasSignature()) {
            if (Wormhole.isBotActive()) Wormhole.getBot().sendPlayerMessage(player.getName(), message.getContent());
            if (Wormhole.isWebhookActive()) Wormhole.getWebhook().sendPlayerMessage(player.getName(), message.getContent());
        }
    }
}
