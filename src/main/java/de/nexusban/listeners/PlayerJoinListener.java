package de.nexusban.listeners;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.List;
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
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Show pending offline warnings first
        showPendingWarnings(player);

        // Check if rejoin warning is enabled
        if (!plugin.getConfig().getBoolean("rejoin-warning.enabled", true)) {
            return;
        }

        // Check if player has punishment history
        int totalPunishments = plugin.getHistoryManager().getTotalPunishments(player.getUniqueId());
        if (totalPunishments == 0) {
            return;
        }

        // Check time limit for showing warning
        int showForHours = plugin.getConfig().getInt("rejoin-warning.show-for-hours", 168);
        if (showForHours > 0) {
            long lastPunishment = plugin.getHistoryManager().getLastPunishmentTime(player.getUniqueId());
            long hoursSince = (System.currentTimeMillis() - lastPunishment) / (1000 * 60 * 60);
            if (hoursSince > showForHours) {
                return;
            }
        }

        // Send warning message with delay so player can see it
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (player.isOnline()) {
                List<String> messages = plugin.getConfig().getStringList("rejoin-warning.message");
                for (String msg : messages) {
                    player.sendMessage(MessageUtils.colorize(msg));
                }
            }
        }, 40L); // 2 seconds delay
    }

    private void showPendingWarnings(Player player) {
        // Get all warnings from history
        List<Punishment> allHistory = plugin.getHistoryManager().getAllHistory(player.getUniqueId(), player.getName());

        // Find warnings from the last 7 days that player hasn't seen yet
        long sevenDaysAgo = System.currentTimeMillis() - (7 * 24 * 60 * 60 * 1000);
        long lastSeen = player.getLastPlayed(); // When player was last online

        List<Punishment> pendingWarnings = allHistory.stream()
            .filter(p -> p.getType() == Punishment.PunishmentType.WARN)
            .filter(p -> p.getStartTime() > lastSeen) // Warnings received while offline
            .filter(p -> p.getStartTime() > sevenDaysAgo) // Within last 7 days
            .toList();

        if (pendingWarnings.isEmpty()) {
            return;
        }

        // Show warnings with a delay
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;

            int warnCount = plugin.getHistoryManager().getWarningCount(player.getUniqueId());

            for (Punishment warning : pendingWarnings) {
                player.sendMessage("");
                player.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§e§l  ⚠ NEW WARNING RECEIVED WHILE OFFLINE");
                player.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("§7  Reason: §f" + warning.getReason());
                player.sendMessage("§7  Warned by: §f" + warning.getPunisherName());
                player.sendMessage("§7  Date: §f" + MessageUtils.formatDate(warning.getStartTime()));
                player.sendMessage("");
                player.sendMessage("§c  Please follow the server rules!");
                player.sendMessage("§7  Total warnings: §e" + warnCount);
                player.sendMessage("§8§l§m━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                player.sendMessage("");
            }
        }, 60L); // 3 seconds delay
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
        
        String kickMessage = String.join("\n", MessageUtils.getIpBanScreen(
            ban.getReason(),
            ban.getPunisherName(),
            duration
        ));
        
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
