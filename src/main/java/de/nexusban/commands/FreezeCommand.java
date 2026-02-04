package de.nexusban.commands;

import de.nexusban.NexusBan;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FreezeCommand implements CommandExecutor {

    private final NexusBan plugin;

    public FreezeCommand(NexusBan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.freeze")) {
            sender.sendMessage(plugin.getConfig().getString("messages.no-permission", "§cYou don't have permission to use this command!"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§cUsage: /freeze <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(plugin.getConfig().getString("messages.player-not-found", "§cPlayer not found!"));
            return true;
        }

        if (target.hasPermission("nexusban.freeze.bypass")) {
            sender.sendMessage("§cYou cannot freeze this player!");
            return true;
        }

        if (plugin.getFreezeManager().isFrozen(target)) {
            sender.sendMessage(plugin.getConfig().getString("messages.freeze.already-frozen", "§c{player} is already frozen!")
                    .replace("{player}", target.getName()));
            return true;
        }

        // Freeze the player
        UUID adminUUID = sender instanceof Player ? ((Player) sender).getUniqueId() : null;
        plugin.getFreezeManager().freezePlayer(target, adminUUID);

        // Send messages
        String freezeMessage = plugin.getConfig().getString("messages.freeze.frozen", "§cYou have been frozen! You can only chat with staff members.")
                .replace("{admin}", sender.getName());
        target.sendMessage(freezeMessage);

        String staffMessage = plugin.getConfig().getString("messages.freeze.staff-frozen", "§a{player} has been frozen!")
                .replace("{player}", target.getName())
                .replace("{admin}", sender.getName());
        sender.sendMessage(staffMessage);

        // Notify other staff members
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("nexusban.freeze") && !onlinePlayer.equals(sender) && !onlinePlayer.equals(target)) {
                onlinePlayer.sendMessage(staffMessage);
            }
        }

        return true;
    }
}
