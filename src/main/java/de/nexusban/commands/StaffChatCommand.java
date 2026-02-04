package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand implements CommandExecutor {

    private final NexusBan plugin;

    public StaffChatCommand(NexusBan plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.staffchat")) {
            sender.sendMessage(MessageUtils.colorize(plugin.getConfig().getString("messages.no-permission", "§cYou don't have permission to use this command!")));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(MessageUtils.colorize("§cUsage: /staffchat <message>"));
            return true;
        }

        // Build message
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }

        String senderName = sender instanceof Player ? sender.getName() : "Console";
        String formattedMessage = MessageUtils.colorize(plugin.getConfig().getString("messages.staffchat-format", "§7[§cStaff§7] §e{sender}§7: §f{message}")
                .replace("{sender}", senderName)
                .replace("{message}", message.toString().trim()));

        // Send to all staff members
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasPermission("nexusban.staffchat")) {
                onlinePlayer.sendMessage(formattedMessage);
            }
        }

        // Send to console
        Bukkit.getConsoleSender().sendMessage(formattedMessage);

        return true;
    }
}
