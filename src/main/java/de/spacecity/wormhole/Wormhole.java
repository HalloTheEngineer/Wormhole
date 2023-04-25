package de.spacecity.wormhole;

import de.spacecity.wormhole.utils.DiscordWebhook;
import de.spacecity.wormhole.utils.ServerStartedEvent;
import de.spacecity.wormhole.utils.ServerStoppingEvent;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.discordjson.json.EmbedData;
import discord4j.discordjson.json.EmbedImageData;
import discord4j.rest.entity.RestChannel;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;

public class Wormhole implements ModInitializer {
    public static boolean ENABLED = false;
    public static boolean USING_BOT = true;
    public static Logger LOGGER = LoggerFactory.getLogger("Wormhole");
    public static MinecraftServer SERVER;

    public static DiscordWebhook WEBHOOK;
    public static DiscordClient BOT;
    public static RestChannel CHANNEL;
    public static String WEBHOOK_URL;
    public static String BOT_TOKEN;
    public static Mono<Void> login;
    public static String CHANNEL_ID;
    public static YamlFile config = new YamlFile(FabricLoader.getInstance().getConfigDir().toString() + "/wormhole.yml");

    public static Thread DISCORD_BOT = new Thread(() -> {
        BOT = DiscordClient.create(BOT_TOKEN);
        CHANNEL = BOT.getChannelById(Snowflake.of(CHANNEL_ID));
        login = BOT.withGateway((GatewayDiscordClient gateway) -> {
            Mono<Void> loggedIn = gateway.on(ReadyEvent.class, event ->
                            Mono.fromRunnable(() -> {
                                final User user = event.getSelf();
                                LOGGER.info("Bot successfully logged in as " +
                                        user.getUsername() + "#" + user.getDiscriminator());
                            }))
                    .then();

            Mono<Void> createMessage = gateway.on(MessageCreateEvent.class, event -> {
                Message message = event.getMessage();
                if (message.getAuthor().get().isBot()) return Mono.empty();
                if (!(SERVER == null)) SERVER.getPlayerManager().broadcast(Text.of(message.getAuthor().get().getUsername() + "#"
                        + message.getAuthor().get().getDiscriminator() + " ▶ " + message.getContent()), false);
                return Mono.empty();
            }).then();

            return loggedIn.and(createMessage);
        });
        login.block();
    }, "DiscordBot");


    @Override
    public void onInitialize() {
        LOGGER.info("Initializing of mod Wormhole started");
        loadConfig();
        if (!botStartup()) {
            USING_BOT = false;
            LOGGER.info("Trying webhook url...");
            if (!webhookStartup()) {
                LOGGER.error("Invalid credentials given, deactivating mod.");
            } else {
                ENABLED = true;
                LOGGER.info("Valid webhook url found, sending welcome message... | D2M Connection disabled");
            }
        } else {
            ENABLED = true;
            LOGGER.info("Valid bot-token found, D2M Connection enabled");
        }
        ServerLifecycleEvents.SERVER_STARTED.register(new ServerStartedEvent());
        ServerLifecycleEvents.SERVER_STOPPING.register(new ServerStoppingEvent());
        if (ENABLED) LOGGER.info("Mod has been initialized.");
    }


    private boolean botStartup() {
        BOT_TOKEN = config.getString("botToken");
        CHANNEL_ID = config.getString("channelID");
        if (BOT_TOKEN == null || BOT_TOKEN.isEmpty()) {
            LOGGER.warn("No bot token given...");
            return false;
        }
        if (CHANNEL_ID == null || CHANNEL_ID.isEmpty()) {
            LOGGER.warn("No channel id given...");
            return false;
        }
        DISCORD_BOT.start();
        return true;
    }
    private static boolean webhookStartup() {
        WEBHOOK_URL = config.getString("webhookUrl");
        if (WEBHOOK_URL == null || WEBHOOK_URL.isEmpty()) return false;
        WEBHOOK = new DiscordWebhook(WEBHOOK_URL);
        try {
            WEBHOOK.addEmbed(new DiscordWebhook.EmbedObject().setTitle("Server started at " + Time.valueOf(LocalTime.now())));
            WEBHOOK.execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
    private static void loadConfig() {
        try {
            if (!config.exists()) {
                config.createNewFile();
                LOGGER.info("Config File has been created: " + config.getFilePath() + "\n");
                LOGGER.warn("Now fill in your credentials and restart the server \n");
            } else {
                LOGGER.warn(config.getFilePath() + " already exists, loading configurations...\n");
            }
            config.load();
        } catch (final Exception e) {
            e.printStackTrace();
        }
        config.addDefault("webhookUrl", "");
        config.addDefault("botToken", "");
        config.addDefault("channelID", "");
        config.setComment("webhookUrl", "Discord to Minecraft communication not possible");
        config.setComment("botToken", "Discord to Minecraft communication possible");
        config.setComment("channelID", "Only needed when using a discord bot");
        try {
            config.save();
        } catch (final IOException e) {
            e.printStackTrace();
        }

    }

}
