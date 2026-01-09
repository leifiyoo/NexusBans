package de.meinplugin.commands;

import de.meinplugin.MeinPlugin;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

public class AltsCommand implements CommandExecutor {
    
    private final MeinPlugin plugin;
    
    public AltsCommand(MeinPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("nexusban.alts")) {
            sender.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(MessageUtils.PREFIX + "§cUsage: /alts <player>");
            return true;
        }
        
        String targetName = args[0];
        UUID targetUUID = null;
        
        // Try online player first
        Player onlinePlayer = plugin.getServer().getPlayerExact(targetName);
        if (onlinePlayer != null) {
            targetUUID = onlinePlayer.getUniqueId();
            targetName = onlinePlayer.getName();
        } else {
            // Try offline player
            targetUUID = plugin.getPunishmentManager().getUUID(targetName);
        }
        
        if (targetUUID == null) {
            sender.sendMessage(MessageUtils.PREFIX + "§cPlayer not found: §f" + targetName);
            return true;
        }
        
        Set<UUID> alts = plugin.getPunishmentManager().getAlts(targetUUID);
        Set<String> ips = plugin.getPunishmentManager().getPlayerIps(targetUUID);
        
        sender.sendMessage("");
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("§b§l  🔍 ALT DETECTION - " + targetName);
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Show IPs
        sender.sendMessage("§7  Known IPs: §f" + (ips.isEmpty() ? "None" : ips.size()));
        if (!ips.isEmpty() && sender.hasPermission("nexusban.alts.showip")) {
            for (String ip : ips) {
                sender.sendMessage("§8    - §7" + ip);
            }
        }
        
        // Show alts
        sender.sendMessage("");
        sender.sendMessage("§7  Potential Alt Accounts: §f" + (alts.isEmpty() ? "None" : alts.size()));
        
        if (!alts.isEmpty()) {
            for (UUID altUUID : alts) {
                String altName = plugin.getPunishmentManager().getPlayerName(altUUID);
                boolean isBanned = plugin.getPunishmentManager().isBanned(altUUID);
                boolean isMuted = plugin.getPunishmentManager().isMuted(altUUID);
                
                StringBuilder status = new StringBuilder();
                if (isBanned) status.append("§c[BANNED] ");
                if (isMuted) status.append("§6[MUTED] ");
                
                OfflinePlayer altPlayer = plugin.getServer().getOfflinePlayer(altUUID);
                String onlineStatus = altPlayer.isOnline() ? "§a[ONLINE]" : "§7[OFFLINE]";
                
                sender.sendMessage("§8    - §f" + altName + " " + status + onlineStatus);
            }
        }
        
        sender.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        sender.sendMessage("");
        
        return true;
    }
}
