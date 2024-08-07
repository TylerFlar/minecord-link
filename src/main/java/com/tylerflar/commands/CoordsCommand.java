package com.tylerflar.commands;

import com.tylerflar.MineCordLink;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoordsCommand implements CommandExecutor {
    private final MineCordLink plugin;

    public CoordsCommand(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;
        String coords = player.getLocation().getBlockX() + ", " + player.getLocation().getBlockY() + ", "
                + player.getLocation().getBlockZ();
        String dimension = getDimensionName(player.getWorld().getEnvironment());
        String location = args.length > 0 ? String.join(" ", args) : "My current";

        String message = ChatColor.YELLOW + player.getName() + ChatColor.WHITE + ": " + location + " coordinates are: "
                +
                ChatColor.YELLOW + dimension + " " + ChatColor.BOLD + ChatColor.LIGHT_PURPLE + coords;

        // Send message to all online players
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(message);
        }

        // Send message to Discord
        String discordMessage = location + " coordinates are: **" + dimension + "** `" + coords + "`";

        plugin.getWebhookManager().sendMessage(
                discordMessage,
                player.getName(),
                "https://mc-heads.net/avatar/" + player.getName(),
                null // No embed
        );

        return true;
    }

    private String getDimensionName(World.Environment environment) {
        switch (environment) {
            case NETHER:
                return "Nether";
            case THE_END:
                return "End";
            default:
                return "Overworld";
        }
    }
}