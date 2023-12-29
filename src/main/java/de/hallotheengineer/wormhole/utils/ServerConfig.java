package de.hallotheengineer.wormhole.utils;


import de.hallotheengineer.wormhole.Wormhole;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ServerConfig {

    public static final String BOT_TOKEN = "discordBotToken";
    public static final String CHANNEL_ID = "botChannelID";
    public static final String WEBHOOK_URL = "webhookUrl";
    public static final String WEBHOOK_NAME = "webhookName";
    public static final String WEBHOOK_AVATAR = "webhookAvatar";
    public static final String MESSAGE_CLICK_BASE_URL = "messageClickBaseUrl";
    public static final String MESSAGE_ICON_BASE_URL = "messageIconBaseUrl";
    public String botToken = "TOKEN HERE";
    public String channelID = "CHANNEL ID HERE";
    public String webhookUrl = "WEBHOOK URL HERE";
    public String webhookName = "Minecraft";
    public String webhookAvatar = "https://cdn.discordapp.com/avatars/643945264868098049/c6a249645d46209f337279cd2ca998c7.webp?size=96";
    public String messageClickBaseUrl = "https://namemc.com/profile/";
    public String messageIconBaseUrl = "https://minotar.net/avatar/";


    public void writeTo(Properties properties) {
        properties.setProperty(BOT_TOKEN, botToken);
        properties.setProperty(CHANNEL_ID, channelID);
        properties.setProperty(WEBHOOK_URL, webhookUrl);
        properties.setProperty(WEBHOOK_NAME, webhookName);
        properties.setProperty(WEBHOOK_AVATAR, webhookAvatar);
        properties.setProperty(MESSAGE_CLICK_BASE_URL, messageClickBaseUrl);
        properties.setProperty(MESSAGE_ICON_BASE_URL, messageIconBaseUrl);
    }

    public void readFrom(Properties properties) {
        this.botToken = properties.getProperty(BOT_TOKEN);
        this.channelID = properties.getProperty(CHANNEL_ID);
        this.webhookUrl = properties.getProperty(WEBHOOK_URL);
        this.webhookName = properties.getProperty(WEBHOOK_NAME);
        this.webhookAvatar = properties.getProperty(WEBHOOK_AVATAR);
        this.messageClickBaseUrl = properties.getProperty(MESSAGE_CLICK_BASE_URL);
        this.messageIconBaseUrl = properties.getProperty(MESSAGE_ICON_BASE_URL);
    }

    public void save() {
        Properties properties = new Properties();
        writeTo(properties);
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(Wormhole.MOD_ID+".properties");
        if(!Files.exists(configPath)) {
            try {
                Files.createFile(configPath);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
        try {
            properties.store(Files.newOutputStream(configPath), "Configuration file for Wormhole");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Properties properties = new Properties();
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(Wormhole.MOD_ID+".properties");
        if(!Files.exists(configPath)) {
            try {
                Files.createFile(configPath);
                save();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            properties.load(Files.newInputStream(configPath));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        readFrom(properties);
    }
}
