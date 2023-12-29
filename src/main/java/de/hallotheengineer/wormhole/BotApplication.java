package de.hallotheengineer.wormhole;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalTime;

import static de.hallotheengineer.wormhole.Wormhole.LOGGER;
import static de.hallotheengineer.wormhole.Wormhole.CONFIG;
public class BotApplication extends ListenerAdapter {
    private final JDA jda;
    private final MinecraftServer server;
    private TextChannel messageChannel;
    private boolean isActive;

    public BotApplication(String token, MinecraftServer server) {
        this.jda = JDABuilder.createLight(token, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(this)
                .setActivity(Activity.customStatus("SMP Networking Bot by @hallotheengineer"))
                .build();
        this.server = server;
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        startup();
    }
    public void startup() {
        this.messageChannel = jda.getTextChannelById(Wormhole.CONFIG.channelID);
        if (this.messageChannel == null) {
            LOGGER.warn("The provided channel id is invalid or could not be found by the bot! Shutting down...");
            this.isActive = false;
        } else {
            sendServerStarting();
            this.isActive = true;
        }
    }
    private void sendServerStarting() {
        if (isActive) return;
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(" :green_circle: Server started at " + Time.valueOf(LocalTime.now()));
        builder.setColor(Color.GREEN);
        messageChannel.sendMessageEmbeds(builder.build()).delay(Duration.ofSeconds(2)).queue();
    }

    private void sendServerStopping() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(" :red_circle:  Server stopped at " + Time.valueOf(LocalTime.now()));
        builder.setColor(Color.RED);
        messageChannel.sendMessageEmbeds(builder.build()).queue();
    }

    public void sendAdvancement(Text advancementDisplay) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(advancementDisplay.getString());
        builder.setColor(Color.YELLOW);
        this.messageChannel.sendMessageEmbeds(builder.build()).queue();
    }
    public void sendPlayerConnect(Text name) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.GREEN);
        builder.setAuthor(name.getLiteralString() + " joined the game", CONFIG.messageClickBaseUrl + name.getLiteralString(),
                CONFIG.messageIconBaseUrl + name.getLiteralString());
        this.messageChannel.sendMessageEmbeds(builder.build()).queue();
    }
    public void sendPlayerDisconnect(Text name) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(Color.RED);
        builder.setAuthor(name.getLiteralString() + " left the game", CONFIG.messageClickBaseUrl + name.getLiteralString(),
                CONFIG.messageIconBaseUrl + name.getLiteralString());
        this.messageChannel.sendMessageEmbeds(builder.build()).queue();
    }
    public void sendPlayerDeath(Text deathMessage) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(deathMessage.getString());
        builder.setColor(Color.BLACK);
        this.messageChannel.sendMessageEmbeds(builder.build()).queue();
    }
    public void sendPlayerMessage(Text name, Text message) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(message.getLiteralString());
        builder.setColor(Color.CYAN);
        builder.setAuthor(name.getLiteralString(), CONFIG.messageClickBaseUrl + name.getLiteralString(),
                CONFIG.messageIconBaseUrl + name.getLiteralString());
        this.messageChannel.sendMessageEmbeds(builder.build()).queue();
    }
    public void shutdown() {
        sendServerStopping();
        this.jda.shutdown();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.TEXT) && event.getGuildChannel().getId().equals(messageChannel.getId()) && !event.isWebhookMessage() && !event.getAuthor().isBot()) {
            server.getPlayerManager().broadcast(Text.literal("[Discord]").formatted(Formatting.BLUE).append(applyAttachmentsToText(event.getMessage(), event.getMessage().getContentStripped())), false);
        }
    }
    private Text applyAttachmentsToText(Message message, String content) {
        MutableText text = Text.empty();
        text.append(Text.literal(" "+message.getAuthor().getGlobalName()+" ▶ ").formatted(Formatting.GRAY));
        message.getAttachments().forEach(attachment -> text.append(Text.literal("[💾] ").formatted(Formatting.WHITE).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, attachment.getUrl())).withInsertion(attachment.getFileName()))));
        text.append(Text.literal(content).formatted(Formatting.GRAY));
        return text;
    }

    public boolean isActive() {
        return this.isActive;
    }
}
