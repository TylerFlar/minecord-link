package com.tylerflar.discord;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.Color;

public class WebhookManager {
    private WebhookClient client;
    private String botAvatarUrl;
    private String botUsername;

    public WebhookManager(JavaPlugin plugin, String botAvatarUrl, String botUsername) {
        this.botAvatarUrl = botAvatarUrl;
        this.botUsername = botUsername;
        String webhookUrl = plugin.getConfig().getString("discord.webhook_url");
        if (webhookUrl == null || webhookUrl.isEmpty()) {
            plugin.getLogger().severe("Webhook URL is not set in the config!");
            this.client = null;
        } else {
            this.client = WebhookClient.withUrl(webhookUrl);
        }
    }

    public void sendMessage(String content, String username, String avatarUrl, WebhookEmbed embed) {
        if (client == null)
            return;

        WebhookMessageBuilder builder = new WebhookMessageBuilder();
        if (content != null && !content.isEmpty()) {
            builder.setContent(content);
        }
        if (username != null && !username.isEmpty()) {
            builder.setUsername(username);
        } else {
            builder.setUsername(botUsername);
        }
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            builder.setAvatarUrl(avatarUrl);
        } else {
            builder.setAvatarUrl(botAvatarUrl);
        }
        if (embed != null) {
            builder.addEmbeds(embed);
        }

        client.send(builder.build());
    }

    public WebhookEmbed createEmbed(String title, String description, Color color) {
        return new WebhookEmbedBuilder()
                .setColor(color.getRGB())
                .setDescription(description)
                .setTitle(new WebhookEmbed.EmbedTitle(title, null))
                .build();
    }

    public void updateWebhookUrl(String newWebhookUrl) {
        if (client != null) {
            client.close();
        }
        this.client = WebhookClient.withUrl(newWebhookUrl);
    }

    public void shutdown() {
        if (client != null) {
            client.close();
        }
    }
}