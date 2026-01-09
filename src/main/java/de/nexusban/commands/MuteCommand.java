package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.data.Punishment.PunishmentType;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MuteCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public MuteCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.mute")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /mute <player> [reason]");
            return true;
        }
        
        String targetName = args[0];
        
        // Prevent muting yourself
        if (sender instanceof Player && sender.getName().equalsIgnoreCase(targetName)) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou cannot mute yourself!");
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
        
        // If player has played before, use UUID-based mute
        if (targetUUID != null) {
            if (plugin.getPunishmentManager().isMuted(targetUUID)) {
                sender.sendMessage(MessageUtils.PREFIX + "§cThis player is already muted!");
                return true;
            }
            
            Punishment punishment = new Punishment(
                targetUUID,
                targetName,
                PunishmentType.MUTE,
                reason,
                punisherName,
                System.currentTimeMillis(),
                -1 // Permanent
            );
            
            plugin.getPunishmentManager().addMute(punishment);
            
            // Notify target if online
            Player target = Bukkit.getPlayer(targetUUID);
            if (target != null) {
                target.sendMessage("");
                target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                target.sendMessage("§c§l  🔇 YOU HAVE BEEN MUTED");
                target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                target.sendMessage("§7  Reason: §f" + reason);
                target.sendMessage("§7  Duration: §4Permanent");
                target.sendMessage("§7  Muted by: §f" + punisherName);
                target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                target.sendMessage("");
            }
        } else {
            // Player never joined - use name-based mute
            if (plugin.getPunishmentManager().isNameMuted(targetName)) {
                sender.sendMessage(MessageUtils.PREFIX + "§cThis player is already muted!");
                return true;
            }
            
            Punishment punishment = new Punishment(
                null,
                targetName,
                PunishmentType.MUTE,
                reason,
                punisherName,
                System.currentTimeMillis(),
                -1
            );
            
            plugin.getPunishmentManager().addNameMute(punishment);
        }
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("MUTE", targetName, punisherName, reason, "Permanent");
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully permanently muted §f" + targetName + "§a!");
        
        return true;
    }
}
