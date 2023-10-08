package de.spacecity.wormhole.mixin;

import de.spacecity.wormhole.Wormhole;
import de.spacecity.wormhole.utils.DiscordWebhook;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
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

import java.io.IOException;

@Mixin(DamageTracker.class)
public class DamageTrackerMixin {
    @Shadow @Final private LivingEntity entity;
    @Inject(method = "getDeathMessage", at = @At("RETURN"))
    public void getDeathMessage(CallbackInfoReturnable<Text> cir) throws IOException {
        if (!Wormhole.ENABLED) return;
        if (!(entity instanceof PlayerEntity)) return;
        if (!Wormhole.USING_BOT) {
            Wormhole.WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject()
                    .setAuthor(cir.getReturnValue().getString(), "https://namemc.com/profile/" + entity.getName().getString(),
                            "https://minotar.net/avatar/" + entity.getName().getString())
            );
            Wormhole.WEBHOOK.execute();
        } else {
            Wormhole.CHANNEL.createMessage(
                    EmbedData.builder().author(EmbedAuthorData.builder().name(cir.getReturnValue().getString())
                            .url("https://namemc.com/profile/" + entity.getName().getString())
                            .iconUrl("https://minotar.net/avatar/" + entity.getName().getString())
                            .build()).description(cir.getReturnValue().getString()).build()
            ).block();
        }
    }
}
