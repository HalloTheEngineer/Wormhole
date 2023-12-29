package de.hallotheengineer.wormhole;

import de.hallotheengineer.wormhole.utils.DiscordWebhook;
import net.minecraft.text.Text;

import java.awt.*;
import java.io.IOException;
import java.sql.Time;
import java.time.LocalTime;

import static de.hallotheengineer.wormhole.Wormhole.LOGGER;
import static de.hallotheengineer.wormhole.Wormhole.CONFIG;

public class WebhookApplication {
    private final String webhookUrl;
    private final String webhookAvatar;
    private final String webhookName;
    public WebhookApplication(String webhookUrl, String webhookAvatar, String webhookName) {
        this.webhookUrl = webhookUrl;
        this.webhookAvatar = webhookAvatar;
        this.webhookName = webhookName;

        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl, webhookAvatar, webhookName);
            webhook.addEmbed(
                    new DiscordWebhook.EmbedObject().setTitle(" :green_circle:  Server started at " + Time.valueOf(LocalTime.now())).setColor(Color.GREEN));
            webhook.execute();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    public void shutdown() {
        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl, webhookAvatar, webhookName);
            webhook.addEmbed(new DiscordWebhook.EmbedObject().setTitle(" :red_circle:  Server stopped at " + Time.valueOf(LocalTime.now())).setColor(Color.RED));
            webhook.execute();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    public void sendPlayerConnect(Text name) {
        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl, webhookAvatar, webhookName);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(Color.GREEN)
                    .setAuthor(name.getLiteralString() + " joined the game", CONFIG.messageClickBaseUrl + name.getLiteralString(),
                            CONFIG.messageIconBaseUrl + name.getLiteralString()));
            webhook.execute();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    public void sendPlayerDisconnect(Text name) {
        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl, webhookAvatar, webhookName);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(Color.RED)
                    .setAuthor(name.getLiteralString() + " left the game", CONFIG.messageClickBaseUrl + name.getLiteralString(),
                            CONFIG.messageIconBaseUrl + name.getLiteralString()));
            webhook.execute();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    public void sendPlayerDeath(Text deathMessage) {
        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl, webhookAvatar, webhookName);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(Color.BLACK)
                    .setDescription(deathMessage.getLiteralString()));
            webhook.execute();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    public void sendAdvancement(Text advancementDisplay) {
        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl, webhookAvatar, webhookName);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(Color.YELLOW)
                    .setTitle(advancementDisplay.getString()));
            webhook.execute();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }

    public void sendPlayerMessage(Text name, Text content) {
        try {
            DiscordWebhook webhook = new DiscordWebhook(webhookUrl, webhookAvatar, webhookName);
            webhook.addEmbed(new DiscordWebhook.EmbedObject()
                    .setColor(Color.CYAN)
                    .setTitle(content.getLiteralString())
                    .setAuthor(name.getLiteralString(), CONFIG.messageClickBaseUrl + name.getLiteralString(),
                            CONFIG.messageIconBaseUrl + name.getLiteralString()));
            webhook.execute();
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
    }
}
