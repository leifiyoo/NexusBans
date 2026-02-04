package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnfreezeCommand implements CommandExecutor {

    private final NexusBan plugin;

    public UnfreezeCommand(NexusBan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.freeze")) {
            sender.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission", "§cYou don't have permission to use this command!")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("§cUsage: /unfreeze <player>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.player-not-found", "§cPlayer not found!")));
            return true;
        }

        if (!plugin.getFreezeManager().isFrozen(target)) {
            sender.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.freeze.not-frozen", "§c{player} is not frozen!")
                    .replace("{player}", target.getName())));
            return true;
        }

        // Unfreeze the player
        plugin.getFreezeManager().unfreezePlayer(target);

        // Send messages
        String unfreezeMessage = MessageUtils.colorize(plugin.getConfig().getString("messages.freeze.unfrozen", "§aYou have been unfrozen!")
                .replace("{admin}", sender.getName()));
        target.sendMessage(unfreezeMessage);

        String staffMessage = MessageUtils.colorize(plugin.getConfig().getString("messages.freeze.staff-unfrozen", "§a{player} has been unfrozen!")
                .replace("{player}", target.getName())
                .replace("{admin}", sender.getName()));
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
