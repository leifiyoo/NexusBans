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

public class KickCommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public KickCommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.kick")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /kick <player> [reason]");
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
        
        // Add to history
        Punishment punishment = new Punishment(
            target.getUniqueId(),
            target.getName(),
            PunishmentType.KICK,
            reason,
            punisherName,
            System.currentTimeMillis(),
            System.currentTimeMillis()
        );
        plugin.getHistoryManager().addHistory(punishment);
        
        // Kick player
        target.kickPlayer(String.join("\n", MessageUtils.getKickScreen(reason, punisherName)));
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("KICK", targetName, punisherName, reason, null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully kicked §f" + targetName + "§a!");
        
        return true;
    }
}

