package de.nexusban.listeners;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.utils.MessageUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Set;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    
    private final NexusBan plugin;
    
    public PlayerJoinListener(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String ip = event.getAddress().getHostAddress();
        
        // Record player IP for alt detection
        plugin.getPunishmentManager().recordPlayerIp(player.getUniqueId(), ip, player.getName());
        
        // Check if IP is banned
        if (plugin.getPunishmentManager().isIpBanned(ip)) {
            Punishment ipBan = plugin.getPunishmentManager().getIpBan(ip);
            kickWithIpBan(event, player, ipBan, ip);
            return;
        }
        
        // Check if player is banned by UUID
        if (plugin.getPunishmentManager().isBanned(player.getUniqueId())) {
            Punishment ban = plugin.getPunishmentManager().getBan(player.getUniqueId());
            kickWithBan(event, player, ban);
            return;
        }
        
        // Check if player is banned by name (for players who were banned before joining)
        if (plugin.getPunishmentManager().isNameBanned(player.getName())) {
            Punishment ban = plugin.getPunishmentManager().getNameBan(player.getName());
            
            // Convert name ban to UUID ban for future checks
            Punishment uuidBan = new Punishment(
                player.getUniqueId(),
                player.getName(),
                ban.getType(),
                ban.getReason(),
                ban.getPunisherName(),
                ban.getStartTime(),
                ban.getEndTime()
            );
            plugin.getPunishmentManager().addBan(uuidBan);
            plugin.getPunishmentManager().removeNameBan(player.getName());
            
            kickWithBan(event, player, ban);
            return;
        }
        
        // Check for alts of banned players and notify staff
        checkForBannedAlts(player, ip);
    }
    
    private void checkForBannedAlts(Player player, String ip) {
        Set<UUID> alts = plugin.getPunishmentManager().getAlts(player.getUniqueId());
        
        for (UUID altUUID : alts) {
            if (plugin.getPunishmentManager().isBanned(altUUID)) {
                String altName = plugin.getPunishmentManager().getPlayerName(altUUID);
                
                // Notify staff
                String alertMessage = MessageUtils.PREFIX + "§c⚠ §eAlt Detection: §f" + player.getName() + 
                    " §7may be an alt of banned player §c" + altName;
                
                plugin.getServer().getOnlinePlayers().stream()
                    .filter(p -> p.hasPermission("nexusban.notify"))
                    .forEach(p -> p.sendMessage(alertMessage));
                
                plugin.getLogger().warning("Alt Detection: " + player.getName() + " shares IP with banned player " + altName);
                break;
            }
        }
    }
    
    private void kickWithIpBan(PlayerLoginEvent event, Player player, Punishment ban, String ip) {
        String duration;
        if (ban.isPermanent()) {
            duration = "Permanent";
        } else {
            duration = MessageUtils.formatDuration(ban.getRemainingTime());
        }
        
        String kickMessage = String.join("\n", 
            "",
            "§c§l✦ YOUR IP HAS BEEN BANNED ✦",
            "",
            "§7Reason: §f" + ban.getReason(),
            "§7Duration: §f" + duration,
            "§7Banned by: §f" + ban.getPunisherName(),
            "",
            "§7Appeal at: §e§nSee /appeal",
            ""
        );
        
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage);
    }
    
    private void kickWithBan(PlayerLoginEvent event, Player player, Punishment ban) {
        String duration;
        if (ban.isPermanent()) {
            duration = "Permanent";
        } else {
            duration = MessageUtils.formatDuration(ban.getRemainingTime());
        }
        
        String kickMessage = String.join("\n", MessageUtils.getBanScreen(
            ban.getReason(),
            ban.getPunisherName(),
            duration,
            player.getUniqueId().toString().substring(0, 8)
        ));
        
        event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage);
    }
}
