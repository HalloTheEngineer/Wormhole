package de.hallotheengineer.wormhole.mixin;

import de.hallotheengineer.wormhole.Wormhole;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;sendPacket(Lnet/minecraft/network/packet/Packet;)V", ordinal = 5))
    public void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, ConnectedClientData clientData, CallbackInfo ci) {
        if (Wormhole.isBotActive()) Wormhole.getBot().sendPlayerConnect(player.getName());
        if (Wormhole.isWebhookActive()) Wormhole.getWebhook().sendPlayerConnect(player.getName());
    }
    @Inject(method = "remove", at = @At("HEAD"))
    public void remove(ServerPlayerEntity player, CallbackInfo ci) {
        if (Wormhole.isBotActive()) Wormhole.getBot().sendPlayerDisconnect(player.getName());
        if (Wormhole.isWebhookActive()) Wormhole.getWebhook().sendPlayerDisconnect(player.getName());
    }
}
