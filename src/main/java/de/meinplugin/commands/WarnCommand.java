package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import de.meinplugin.data.Punishment.PunishmentType;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WarnCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public WarnCommand(MeinPlugin plugin) {
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
        
        if (target == null) {
            sender.sendMessage(MessageUtils.PREFIX + "§cPlayer §f" + targetName + " §cis not online!");
            return true;
        }
        
        // Staff protection check
        if (!plugin.getPunishmentManager().canPunish(sender, target.getUniqueId())) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou cannot punish this player!");
            return true;
        }
        
        String reason = args.length > 1 ? String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length)) : "No reason specified";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        // Add warning to history
        Punishment punishment = new Punishment(
            target.getUniqueId(),
            target.getName(),
            PunishmentType.WARN,
            reason,
            punisherName,
            System.currentTimeMillis(),
            System.currentTimeMillis()
        );
        plugin.getHistoryManager().addHistory(punishment);
        
        int warnCount = plugin.getHistoryManager().getWarningCount(target.getUniqueId());
        
        // Notify target with fancy warning
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
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("WARNING #" + warnCount, targetName, punisherName, reason, null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully warned §f" + targetName + "§a! (Warning #" + warnCount + ")");
        
        // Auto-actions based on warning count
        if (warnCount >= 5) {
            sender.sendMessage(MessageUtils.PREFIX + "§e" + targetName + " has reached " + warnCount + " warnings! Consider a ban.");
        }
        
        return true;
    }
}
