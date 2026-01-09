package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnbanCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public UnbanCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.unban")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /unban <player>");
            return true;
        }
        
        String targetName = args[0];
        UUID targetUUID = plugin.getPunishmentManager().getUUID(targetName);
        
        boolean wasUUIDBanned = targetUUID != null && plugin.getPunishmentManager().isBanned(targetUUID);
        boolean wasNameBanned = plugin.getPunishmentManager().isNameBanned(targetName);
        
        if (!wasUUIDBanned && !wasNameBanned) {
            sender.sendMessage(MessageUtils.PREFIX + "§cThis player is not banned!");
            return true;
        }
        
        if (wasUUIDBanned) {
            plugin.getPunishmentManager().removeBan(targetUUID);
        }
        if (wasNameBanned) {
            plugin.getPunishmentManager().removeNameBan(targetName);
        }
        
        String staffName = sender instanceof Player ? sender.getName() : "Console";
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("UNBAN", targetName, staffName, "Ban lifted", null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully unbanned §f" + targetName + "§a!");
        
        return true;
    }
}
