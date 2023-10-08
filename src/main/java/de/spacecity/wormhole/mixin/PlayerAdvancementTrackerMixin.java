package de.spacecity.wormhole.mixin;

import de.spacecity.wormhole.Wormhole;
import de.spacecity.wormhole.utils.DiscordWebhook;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At("RETURN"))
    public void grandCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) throws IOException {
        if (!Wormhole.ENABLED) return;
        if (advancement.value().display().isEmpty()) return;
        if (!Wormhole.USING_BOT) {
            Wormhole.WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject()
                    .setAuthor(owner.getName().getString(), "https://namemc.com/profile/" + owner.getName().getString(),
                            "https://minotar.net/avatar/" + owner.getName().getString())
                    .setDescription(":crown: " + advancement.value().display().get().getTitle().getString())
            );
            Wormhole.WEBHOOK.execute();
        } else {
            Wormhole.CHANNEL.createMessage(
                    EmbedData.builder().author(EmbedAuthorData.builder().name(owner.getName().getString())
                            .url("https://namemc.com/profile/" + owner.getName().getString())
                            .iconUrl("https://minotar.net/avatar/" + owner.getName().getString())
                            .build()).description(":crown: " + advancement.value().display().get().getTitle().toString()).build()
            ).block();
        }
    }
}
