package com.tylerflar.listeners;

import com.tylerflar.MineCordLink;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.awt.Color;
import java.time.Instant;

public class PlayerDeathListener implements Listener {
    private final MineCordLink plugin;

    public PlayerDeathListener(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        String playerName = event.getEntity().getName();
        String deathMessage = event.getDeathMessage();
        String avatarUrl = "https://mc-heads.net/avatar/" + playerName;
        
        WebhookEmbed embed = new WebhookEmbedBuilder()
            .setColor(Color.RED.getRGB())
            .setAuthor(new WebhookEmbed.EmbedAuthor(playerName, avatarUrl, null))
            .setDescription(deathMessage != null ? deathMessage : playerName + " died")
            .setTimestamp(Instant.now())
            .build();
        
        plugin.getWebhookManager().sendMessage(
            null,  // No content, using embed instead
            "Server",
            null,  // No avatar URL for webhook
            embed
        );
    }
}