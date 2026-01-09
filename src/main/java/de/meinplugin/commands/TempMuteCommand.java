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

public class TempMuteCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public TempMuteCommand(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.tempmute")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /tempmute <player> <duration> [reason]");
            sender.sendMessage(MessageUtils.PREFIX + "§7Example: /tempmute Player 1h Spam");
            sender.sendMessage(MessageUtils.PREFIX + "§7Duration units: s(seconds), m(minutes), h(hours), d(days), w(weeks), M(months), y(years)");
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
        
        long duration = MessageUtils.parseDuration(args[1]);
        if (duration <= 0) {
            sender.sendMessage(MessageUtils.PREFIX + "§cInvalid duration! Use format like: 1h, 30m, 1d");
            return true;
        }
        
        String reason = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "No reason specified";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        long endTime = System.currentTimeMillis() + duration;
        String durationStr = MessageUtils.formatDuration(duration);
        
        // If player has played before, use UUID-based mute
        if (targetUUID != null) {
            if (plugin.getPunishmentManager().isMuted(targetUUID)) {
                sender.sendMessage(MessageUtils.PREFIX + "§cThis player is already muted!");
                return true;
            }
            
            Punishment punishment = new Punishment(
                targetUUID,
                targetName,
                PunishmentType.TEMPMUTE,
                reason,
                punisherName,
                System.currentTimeMillis(),
                endTime
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
                target.sendMessage("§7  Duration: §f" + durationStr);
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
                PunishmentType.TEMPMUTE,
                reason,
                punisherName,
                System.currentTimeMillis(),
                endTime
            );
            
            plugin.getPunishmentManager().addNameMute(punishment);
        }
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("TEMPMUTE", targetName, punisherName, reason, durationStr);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully muted §f" + targetName + " §afor §f" + durationStr + "§a!");
        
        return true;
    }
}
