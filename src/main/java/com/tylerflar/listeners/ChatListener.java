package com.tylerflar.listeners;

import com.tylerflar.MineCordLink;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final MineCordLink plugin;
    private boolean crossChatEnabled;

    public ChatListener(MineCordLink plugin) {
        this.plugin = plugin;
        this.crossChatEnabled = plugin.getConfig().getBoolean("crosschat_enabled", true);
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!crossChatEnabled) {
            return;
        }

        String playerName = event.getPlayer().getName();
        String message = ChatColor.stripColor(event.getMessage());

        String avatarUrl = "https://mc-heads.net/avatar/" + event.getPlayer().getName();

        plugin.getWebhookManager().sendMessage(
                message,
                playerName,
                avatarUrl,
                null // No embed
        );
    }

    public void setCrossChatEnabled(boolean enabled) {
        this.crossChatEnabled = enabled;
    }
}