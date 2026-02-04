package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.data.Punishment.PunishmentType;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WarnCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public WarnCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.warn")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /warn <player> [reason]");
            return true;
        }
        
        String targetName = args[0];
        Player target = Bukkit.getPlayer(targetName);
        UUID targetUUID = null;
        boolean isOnline = target != null;

        // Get UUID (works for online and offline players)
        if (isOnline) {
            targetUUID = target.getUniqueId();
            // Staff protection check for online players
            if (!plugin.getPunishmentManager().canPunish(sender, targetUUID)) {
                sender.sendMessage(MessageUtils.PREFIX + "§cYou cannot punish this player!");
                return true;
            }
        } else {
            // Try to get UUID for offline player
            targetUUID = plugin.getPunishmentManager().getUUID(targetName);
        }

        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason specified";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";

        // Add warning to history
        Punishment punishment = new Punishment(
            targetUUID,
            targetName,
            PunishmentType.WARN,
            reason,
            punisherName,
            System.currentTimeMillis(),
            System.currentTimeMillis()
        );
        plugin.getHistoryManager().addHistory(punishment);

        int warnCount;
        if (targetUUID != null) {
            warnCount = plugin.getHistoryManager().getWarningCount(targetUUID);
        } else {
            // For never-joined players, count name-based warnings
            warnCount = (int) plugin.getHistoryManager().getHistoryByName(targetName).stream()
                .filter(p -> p.getType() == PunishmentType.WARN)
                .count();
        }

        // Notify target if online
        if (isOnline) {
            target.sendMessage("");
            target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            target.sendMessage("§e§l  ⚠ WARNING #" + warnCount);
            target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            target.sendMessage("§7  Reason: §f" + reason);
            target.sendMessage("§7  Warned by: §f" + punisherName);
            target.sendMessage("");
            target.sendMessage("§c  Please follow the server rules!");
            target.sendMessage("§7  Total warnings: §e" + warnCount);
            target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            target.sendMessage("");

            // Play warning sound
            target.playSound(target.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 0.5f);
        }
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("WARNING #" + warnCount, targetName, punisherName, reason, null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        String statusMsg = isOnline ? "" : " §7(offline - will see warning on next join)";
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully warned §f" + targetName + "§a! (Warning #" + warnCount + ")" + statusMsg);
        
        // Auto-actions based on warning count
        if (warnCount >= 5) {
            sender.sendMessage(MessageUtils.PREFIX + "§e" + targetName + " has reached " + warnCount + " warnings! Consider a ban.");
        }
        
        return true;
    }
}
