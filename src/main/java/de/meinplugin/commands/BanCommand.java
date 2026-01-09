package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import de.meinplugin.data.Punishment.PunishmentType;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BanCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public BanCommand(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.ban")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /ban <player> [reason]");
            return true;
        }
        
        String targetName = args[0];
        
        // Prevent banning yourself
        if (sender instanceof Player && sender.getName().equalsIgnoreCase(targetName)) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou cannot ban yourself!");
            return true;
        }
        
        // Staff protection check
        if (!plugin.getPunishmentManager().canPunish(sender, targetName)) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou cannot punish this player!");
            return true;
        }
        
        UUID targetUUID = plugin.getPunishmentManager().getUUID(targetName);
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason specified";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        // If player has played before, use UUID-based ban
        if (targetUUID != null) {
            if (plugin.getPunishmentManager().isBanned(targetUUID)) {
                sender.sendMessage(MessageUtils.PREFIX + "§cThis player is already banned!");
                return true;
            }
            
            Punishment punishment = new Punishment(
                targetUUID,
                targetName,
                PunishmentType.BAN,
                reason,
                punisherName,
                System.currentTimeMillis(),
                -1 // Permanent
            );
            
            plugin.getPunishmentManager().addBan(punishment);
            
            // Kick if online
            Player target = Bukkit.getPlayer(targetUUID);
            if (target != null) {
                target.kickPlayer(String.join("\n", MessageUtils.getBanScreen(reason, punisherName, "Permanent", targetUUID.toString().substring(0, 8))));
            }
        } else {
            // Player never joined - use name-based ban
            if (plugin.getPunishmentManager().isNameBanned(targetName)) {
                sender.sendMessage(MessageUtils.PREFIX + "§cThis player is already banned!");
                return true;
            }
            
            Punishment punishment = new Punishment(
                null,
                targetName,
                PunishmentType.BAN,
                reason,
                punisherName,
                System.currentTimeMillis(),
                -1 // Permanent
            );
            
            plugin.getPunishmentManager().addNameBan(punishment);
        }
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("PERMANENT BAN", targetName, punisherName, reason, null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully banned §f" + targetName + "§a permanently!");
        
        return true;
    }
}

