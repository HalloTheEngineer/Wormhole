package de.hallotheengineer.wormhole.mixin;

import de.hallotheengineer.wormhole.Wormhole;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "method_53637", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"))
    public void grandCriterion(AdvancementEntry entry, AdvancementDisplay display, CallbackInfo ci) {
        if (Wormhole.isBotActive()) Wormhole.getBot().sendAdvancement(display.getFrame().getChatAnnouncementText(entry, owner));
        if (Wormhole.isWebhookActive()) Wormhole.getWebhook().sendAdvancement(display.getFrame().getChatAnnouncementText(entry, owner));
    }
}
