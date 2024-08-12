package com.tylerflar.commands;

import com.tylerflar.MineCordLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LinkDiscordCommand implements CommandExecutor {
    private final MineCordLink plugin;
    private final Map<String, UUID> linkingCodes = new HashMap<>();

    public LinkDiscordCommand(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Generate a new code
            String linkCode = generateLinkCode();
            linkingCodes.put(linkCode, player.getUniqueId());

            player.sendMessage("Your link code is: " + linkCode);
            player.sendMessage("Use this code with the /linkminecraft command in Discord to link your accounts.");

            // Remove the code after 5 minutes
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> linkingCodes.remove(linkCode), 20 * 60 * 5);
        } else {
            // Try to link with provided code
            String code = args[0];
            String discordId = plugin.getLinkingCodes().remove(code);

            if (discordId != null) {
                plugin.getLinkedAccounts().put(discordId, player.getUniqueId());
                plugin.saveLinkedAccounts();
                player.sendMessage("Your Minecraft account has been successfully linked to your Discord account!");
            } else {
                player.sendMessage("Invalid or expired link code. Please generate a new code in Discord.");
            }
        }

        return true;
    }

    private String generateLinkCode() {
        return UUID.randomUUID().toString().substring(0, 6);
    }

    public Map<String, UUID> getLinkingCodes() {
        return linkingCodes;
    }
}