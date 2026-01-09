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

public class TempBanCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public TempBanCommand(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.tempban")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /tempban <player> <duration> [reason]");
            sender.sendMessage(MessageUtils.PREFIX + "§7Example: /tempban Player 7d Hacking");
            sender.sendMessage(MessageUtils.PREFIX + "§7Duration units: s(seconds), m(minutes), h(hours), d(days), w(weeks), M(months), y(years)");
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
        
        long duration = MessageUtils.parseDuration(args[1]);
        if (duration <= 0) {
            sender.sendMessage(MessageUtils.PREFIX + "§cInvalid duration! Use format like: 1d, 2h, 30m, 1w");
            return true;
        }
        
        String reason = args.length > 2 ? String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length)) : "No reason specified";
        String punisherName = sender instanceof Player ? sender.getName() : "Console";
        
        long endTime = System.currentTimeMillis() + duration;
        String durationStr = MessageUtils.formatDuration(duration);
        
        // If player has played before, use UUID-based ban
        if (targetUUID != null) {
            if (plugin.getPunishmentManager().isBanned(targetUUID)) {
                sender.sendMessage(MessageUtils.PREFIX + "§cThis player is already banned!");
                return true;
            }
            
            Punishment punishment = new Punishment(
                targetUUID,
                targetName,
                PunishmentType.TEMPBAN,
                reason,
                punisherName,
                System.currentTimeMillis(),
                endTime
            );
            
            plugin.getPunishmentManager().addBan(punishment);
            
            // Kick if online
            Player target = Bukkit.getPlayer(targetUUID);
            if (target != null) {
                target.kickPlayer(String.join("\n", MessageUtils.getBanScreen(reason, punisherName, durationStr, targetUUID.toString().substring(0, 8))));
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
                PunishmentType.TEMPBAN,
                reason,
                punisherName,
                System.currentTimeMillis(),
                endTime
            );
            
            plugin.getPunishmentManager().addNameBan(punishment);
        }
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("TEMP BAN", targetName, punisherName, reason, durationStr);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully banned §f" + targetName + "§a for §f" + durationStr + "§a!");
        
        return true;
    }
}

