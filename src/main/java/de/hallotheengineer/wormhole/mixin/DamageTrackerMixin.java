package de.hallotheengineer.wormhole.mixin;

import de.hallotheengineer.wormhole.Wormhole;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Shadow @Final private LivingEntity entity;
    @Inject(method = "getDeathMessage", at = @At("RETURN"))
    public void getDeathMessage(CallbackInfoReturnable<Text> cir) {
        if (entity instanceof PlayerEntity) {
            if (Wormhole.isBotActive()) Wormhole.getBot().sendPlayerDeath(cir.getReturnValue());
            if (Wormhole.isWebhookActive()) Wormhole.getWebhook().sendPlayerDeath(cir.getReturnValue());
        }
    }
}
