package de.hallotheengineer.wormhole;

import com.mojang.authlib.GameProfile;
import de.hallotheengineer.wormhole.utils.ServerConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.FakePlayer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Wormhole implements ModInitializer, ServerLifecycleEvents.ServerStarting, ServerLifecycleEvents.ServerStopping, ServerPlayConnectionEvents.Join {
    public static final String MOD_ID = "wormhole";
    public static Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    public static final ServerConfig CONFIG = new ServerConfig();
    private static BotApplication bot;
    private static WebhookApplication webhook;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing...");

        ServerLifecycleEvents.SERVER_STARTING.register(this);
        ServerLifecycleEvents.SERVER_STOPPING.register(this);
        ServerPlayConnectionEvents.JOIN.register(this);
    }

    @Override
    public void onServerStarting(MinecraftServer server) {
        CONFIG.load();
        if (!configIsValid(server)) LOGGER.warn("Wormhole Config not valid! Adjust it to establish connection!");
    }

    private boolean configIsValid(MinecraftServer server) {
        if (CONFIG.botToken.length() == 72 && CONFIG.channelID.length() == 19) {
            Thread thread = new Thread(() -> {
                bot = new BotApplication(CONFIG.botToken, server);
                bot.startup();
                LOGGER.info("Starting dc-mc connection using your discord bot!");
            }, "Wormhole");
            thread.start();
            return true;
        } else if (CONFIG.webhookUrl.length() == 121) {
            Thread thread = new Thread(() -> {
                webhook = new WebhookApplication(CONFIG.webhookUrl, CONFIG.webhookAvatar, CONFIG.webhookName);
                LOGGER.info("Starting dc-mc connection using your webhook!");
            }, "Wormhole");
            thread.start();
            return true;
        } else return false;
    }

    @Override
    public void onServerStopping(MinecraftServer server) {
        if (isBotActive()) bot.shutdown();
        if (isWebhookActive()) webhook.shutdown();
    }
    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        Executors.newScheduledThreadPool(1).schedule(() -> server.executeSync(() -> sender.sendPacket(PlayerListS2CPacket.entryFromPlayer(List.of(FakePlayer.get(server.getOverworld(), new GameProfile(UUID.fromString("7d5ac49a-5304-4b21-863d-e25dd4e0b853"), "DisConnect")))))), 1, TimeUnit.SECONDS);
    }
    public static boolean isBotActive() {
        return bot != null && bot.isActive();
    }
    public static boolean isWebhookActive() {
        return webhook != null;
    }

    public static BotApplication getBot() {
        return bot;
    }

    public static WebhookApplication getWebhook() {
        return webhook;
    }
}
