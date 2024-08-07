package com.tylerflar.listeners;

import com.tylerflar.MineCordLink;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {
    private final MineCordLink plugin;

    public ChatListener(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        String playerName = event.getPlayer().getName();
        String message = ChatColor.stripColor(event.getMessage());
        
        String avatarUrl = "https://mc-heads.net/avatar/" + event.getPlayer().getName();
        
        plugin.getWebhookManager().sendMessage(
            message,
            playerName,
            avatarUrl,
            null  // No embed
        );
    }
}