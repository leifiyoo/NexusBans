package de.nexusban.commands;

import de.nexusban.NexusBan;
import de.nexusban.gui.PunishGUI;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PunishGUICommand implements CommandExecutor {
    
    private final NexusBan plugin;
    
    public PunishGUICommand(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(MessageUtils.PREFIX + "§cThis command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("nexusban.gui")) {
            player.sendMessage(MessageUtils.PREFIX + "§cYou don't have permission to use this command!");
            return true;
        }
        
        if (args.length < 1) {
            player.sendMessage(MessageUtils.PREFIX + "§cUsage: /punish <player>");
            return true;
        }
        
        String targetName = args[0];
        
        // Prevent punishing yourself
        if (player.getName().equalsIgnoreCase(targetName)) {
            player.sendMessage(MessageUtils.PREFIX + "§cYou cannot punish yourself!");
            return true;
        }
        
        // Check if player is online first (fast)
        Player onlineTarget = Bukkit.getPlayerExact(targetName);
        if (onlineTarget != null) {
            // Player is online, open GUI immediately
            PunishGUI.openMainMenu(player, targetName, onlineTarget.getUniqueId());
            return true;
        }
        
        // Check if player has played before (from cache, also fast)
        @SuppressWarnings("deprecation")
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
        if (offlinePlayer.hasPlayedBefore()) {
            // Player has played before, use cached UUID
            PunishGUI.openMainMenu(player, targetName, offlinePlayer.getUniqueId());
            return true;
        }
        
        // Player never joined - fetch UUID async from Mojang API
        player.sendMessage(MessageUtils.PREFIX + "§7Loading player data for §e" + targetName + "§7...");
        
        // Run async to not block server
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                // This calls Mojang API (can take time, but we're async now)
                @SuppressWarnings("deprecation")
                OfflinePlayer mojangPlayer = Bukkit.getOfflinePlayer(targetName);
                UUID uuid = mojangPlayer.getUniqueId();
                
                // Check if UUID is valid (not offline-mode generated)
                if (uuid != null) {
                    // Switch back to main thread to open GUI
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (player.isOnline()) {
                            PunishGUI.openMainMenu(player, targetName, uuid);
                        }
                    });
                } else {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        if (player.isOnline()) {
                            player.sendMessage(MessageUtils.PREFIX + "§cPlayer §e" + targetName + " §cnot found!");
                        }
                    });
                }
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    if (player.isOnline()) {
                        player.sendMessage(MessageUtils.PREFIX + "§cError loading player: §e" + e.getMessage());
                    }
                });
            }
        });
        
        return true;
    }
}

