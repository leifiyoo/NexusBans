package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnmuteCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public UnmuteCommand(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.unmute")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /unmute <player>");
            return true;
        }
        
        String targetName = args[0];
        UUID targetUUID = plugin.getPunishmentManager().getUUID(targetName);
        
        boolean wasUUIDMuted = targetUUID != null && plugin.getPunishmentManager().isMuted(targetUUID);
        boolean wasNameMuted = plugin.getPunishmentManager().isNameMuted(targetName);
        
        if (!wasUUIDMuted && !wasNameMuted) {
            sender.sendMessage(MessageUtils.PREFIX + "§cThis player is not muted!");
            return true;
        }
        
        if (wasUUIDMuted) {
            plugin.getPunishmentManager().removeMute(targetUUID);
        }
        if (wasNameMuted) {
            plugin.getPunishmentManager().removeNameMute(targetName);
        }
        
        String staffName = sender instanceof Player ? sender.getName() : "Console";
        
        // Notify target if online
        if (targetUUID != null) {
            Player target = Bukkit.getPlayer(targetUUID);
            if (target != null) {
                target.sendMessage("");
                target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                target.sendMessage("§a§l  ✓ YOU HAVE BEEN UNMUTED");
                target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                target.sendMessage("§7  Your mute has been lifted by §f" + staffName);
                target.sendMessage("§a  You can chat again!");
                target.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                target.sendMessage("");
            }
        }
        
        // Broadcast to staff
        String broadcast = MessageUtils.getStaffBroadcast("UNMUTE", targetName, staffName, "Mute lifted", null);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("nexusban.notify")) {
                p.sendMessage(broadcast);
            }
        }
        
        sender.sendMessage(MessageUtils.PREFIX + "§aSuccessfully unmuted §f" + targetName + "§a!");
        
        return true;
    }
}
