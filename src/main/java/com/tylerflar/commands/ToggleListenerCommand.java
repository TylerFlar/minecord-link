package com.tylerflar.commands;

import com.tylerflar.MineCordLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ToggleListenerCommand implements CommandExecutor, TabCompleter {
    private final MineCordLink plugin;
    private final List<String> listeners = Arrays.asList("chat", "join", "leave", "death", "advancement");

    public ToggleListenerCommand(MineCordLink plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            sender.sendMessage("Usage: /togglelistener <listener> <on|off>");
            return true;
        }

        String listener = args[0].toLowerCase();
        String state = args[1].toLowerCase();

        if (!listeners.contains(listener)) {
            sender.sendMessage("Invalid listener. Available listeners: " + String.join(", ", listeners));
            return true;
        }

        if (!state.equals("on") && !state.equals("off")) {
            sender.sendMessage("Invalid state. Use 'on' or 'off'.");
            return true;
        }

        boolean newState = state.equals("on");
        plugin.toggleListener(listener, newState);
        sender.sendMessage("Listener '" + listener + "' has been turned " + state + ".");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return listeners.stream()
                    .filter(l -> l.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("on", "off").stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return null;
    }
}
