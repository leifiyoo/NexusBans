package de.nexusban.listeners;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.gui.PunishGUI;
import de.nexusban.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {
    
    private final NexusBan plugin;
    
    // Store pending punishments (staff UUID -> [target, type, duration, targetUUID])
    private final Map<UUID, String[]> pendingPunishments = new HashMap<>();
    
    public GUIListener(NexusBan plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Helper method to get UUID from player name (cached, no API calls)
     */
    private UUID getTargetUUID(String targetName) {
        Player online = Bukkit.getPlayerExact(targetName);
        if (online != null) return online.getUniqueId();
        
        @SuppressWarnings("deprecation")
        OfflinePlayer offline = Bukkit.getOfflinePlayer(targetName);
        if (offline.hasPlayedBefore()) return offline.getUniqueId();
        
        return null;
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();
        
        // Check if it's our GUI
        if (!title.startsWith("§8«")) return;
        
        event.setCancelled(true);
        
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;
        if (clicked.getType().name().contains("STAINED_GLASS_PANE")) return;
        
        String targetName = extractTargetName(title);
        if (targetName == null) return;
        
        // Main Menu
        if (title.startsWith(PunishGUI.MAIN_MENU_TITLE)) {
            handleMainMenu(player, clicked, targetName);
        }
        // Ban Duration Menu
        else if (title.startsWith(PunishGUI.BAN_DURATION_TITLE)) {
            handleBanDuration(player, clicked, targetName);
        }
        // Mute Duration Menu
        else if (title.startsWith(PunishGUI.MUTE_DURATION_TITLE)) {
            handleMuteDuration(player, clicked, targetName);
        }
        // Reason Menus
        else if (title.startsWith(PunishGUI.BAN_REASON_TITLE) || 
                 title.startsWith(PunishGUI.MUTE_REASON_TITLE) ||
                 title.startsWith(PunishGUI.KICK_REASON_TITLE) ||
                 title.startsWith(PunishGUI.WARN_REASON_TITLE)) {
            handleReasonMenu(player, clicked, targetName, title);
        }
    }
    
    private void handleMainMenu(Player player, ItemStack clicked, String targetName) {
        Material type = clicked.getType();
        String typeName = type.name();
        UUID targetUUID = getTargetUUID(targetName);
        String uuidStr = targetUUID != null ? targetUUID.toString() : "";
        
        // Handle version-specific materials
        if (type == Material.BARRIER) { // Ban
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "ban", "permanent", uuidStr});
            PunishGUI.openReasonMenu(player, targetName, "ban");
        } else if (typeName.equals("CLOCK") || typeName.equals("WATCH")) { // Temp Ban
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "tempban", null, uuidStr});
            PunishGUI.openBanDurationMenu(player, targetName);
        } else if (type == Material.IRON_BOOTS) { // Kick
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "kick", null, uuidStr});
            PunishGUI.openReasonMenu(player, targetName, "kick");
        } else if (type == Material.PAPER) { // Mute
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "mute", "permanent", uuidStr});
            PunishGUI.openReasonMenu(player, targetName, "mute");
        } else if (type == Material.BOOK) { // Temp Mute
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "tempmute", null, uuidStr});
            PunishGUI.openMuteDurationMenu(player, targetName);
        } else if (type == Material.BLAZE_POWDER) { // Warn
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "warn", null, uuidStr});
            PunishGUI.openReasonMenu(player, targetName, "warn");
        } else if (typeName.equals("WRITABLE_BOOK") || typeName.equals("BOOK_AND_QUILL")) { // History
            player.closeInventory();
            player.performCommand("history " + targetName);
        } else if (type == Material.REDSTONE_BLOCK) { // Close
            player.closeInventory();
        }
    }
    
    private void handleBanDuration(Player player, ItemStack clicked, String targetName) {
        if (clicked.getType() == Material.ARROW) {
            String[] pending = pendingPunishments.get(player.getUniqueId());
            UUID targetUUID = null;
            
            // Try to get UUID from pending data first
            if (pending != null && pending.length > 3 && pending[3] != null && !pending[3].isEmpty()) {
                try {
                    targetUUID = UUID.fromString(pending[3]);
                } catch (IllegalArgumentException ignored) {}
            }
            
            // Fallback to local lookup
            if (targetUUID == null) {
                targetUUID = getTargetUUID(targetName);
            }
            
            if (targetUUID != null) {
                PunishGUI.openMainMenu(player, targetName, targetUUID);
            } else {
                // For players who never joined, we need to create a fake UUID
                // Just close and tell them to use /punish again
                player.closeInventory();
                player.sendMessage(MessageUtils.PREFIX + "§7Use §f/punish " + targetName + " §7to start over.");
            }
            return;
        }
        
        String duration = getDurationFromMaterial(clicked.getType(), "ban");
        if (duration != null) {
            String[] pending = pendingPunishments.get(player.getUniqueId());
            String uuidStr = (pending != null && pending.length > 3) ? pending[3] : "";
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "tempban", duration, uuidStr});
            PunishGUI.openReasonMenu(player, targetName, "ban");
        }
    }
    
    private void handleMuteDuration(Player player, ItemStack clicked, String targetName) {
        if (clicked.getType() == Material.ARROW) {
            String[] pending = pendingPunishments.get(player.getUniqueId());
            UUID targetUUID = null;
            
            // Try to get UUID from pending data first
            if (pending != null && pending.length > 3 && pending[3] != null && !pending[3].isEmpty()) {
                try {
                    targetUUID = UUID.fromString(pending[3]);
                } catch (IllegalArgumentException ignored) {}
            }
            
            // Fallback to local lookup
            if (targetUUID == null) {
                targetUUID = getTargetUUID(targetName);
            }
            
            if (targetUUID != null) {
                PunishGUI.openMainMenu(player, targetName, targetUUID);
            } else {
                player.closeInventory();
                player.sendMessage(MessageUtils.PREFIX + "§7Use §f/punish " + targetName + " §7to start over.");
            }
            return;
        }
        
        String duration = getDurationFromMaterial(clicked.getType(), "mute");
        if (duration != null) {
            String[] pending = pendingPunishments.get(player.getUniqueId());
            String uuidStr = (pending != null && pending.length > 3) ? pending[3] : "";
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "tempmute", duration, uuidStr});
            PunishGUI.openReasonMenu(player, targetName, "mute");
        }
    }
    
    private void handleReasonMenu(Player player, ItemStack clicked, String targetName, String title) {
        if (clicked.getType() == Material.ARROW) {
            // Go back
            String[] pending = pendingPunishments.get(player.getUniqueId());
            UUID targetUUID = null;
            
            // Try to get UUID from pending data first
            if (pending != null && pending.length > 3 && pending[3] != null && !pending[3].isEmpty()) {
                try {
                    targetUUID = UUID.fromString(pending[3]);
                } catch (IllegalArgumentException ignored) {}
            }
            
            // Fallback to local lookup
            if (targetUUID == null) {
                targetUUID = getTargetUUID(targetName);
            }
            
            if (title.startsWith(PunishGUI.BAN_REASON_TITLE)) {
                if (pending != null && pending[1].equals("tempban")) {
                    PunishGUI.openBanDurationMenu(player, targetName);
                } else if (targetUUID != null) {
                    PunishGUI.openMainMenu(player, targetName, targetUUID);
                } else {
                    player.closeInventory();
                    player.sendMessage(MessageUtils.PREFIX + "§7Use §f/punish " + targetName + " §7to start over.");
                }
            } else if (title.startsWith(PunishGUI.MUTE_REASON_TITLE)) {
                if (pending != null && pending[1].equals("tempmute")) {
                    PunishGUI.openMuteDurationMenu(player, targetName);
                } else if (targetUUID != null) {
                    PunishGUI.openMainMenu(player, targetName, targetUUID);
                } else {
                    player.closeInventory();
                    player.sendMessage(MessageUtils.PREFIX + "§7Use §f/punish " + targetName + " §7to start over.");
                }
            } else if (targetUUID != null) {
                PunishGUI.openMainMenu(player, targetName, targetUUID);
            } else {
                player.closeInventory();
                player.sendMessage(MessageUtils.PREFIX + "§7Use §f/punish " + targetName + " §7to start over.");
            }
            return;
        }
        
        String reason = getReasonFromItem(clicked);
        if (reason == null) return;
        
        String[] pending = pendingPunishments.remove(player.getUniqueId());
        if (pending == null) return;
        
        player.closeInventory();
        executePunishment(player, pending[0], pending[1], pending[2], reason);
    }
    
    private void executePunishment(Player staff, String targetName, String type, String duration, String reason) {
        switch (type) {
            case "ban":
                staff.performCommand("ban " + targetName + " " + reason);
                break;
            case "tempban":
                staff.performCommand("tempban " + targetName + " " + duration + " " + reason);
                break;
            case "mute":
                staff.performCommand("mute " + targetName + " " + reason);
                break;
            case "tempmute":
                staff.performCommand("tempmute " + targetName + " " + duration + " " + reason);
                break;
            case "kick":
                staff.performCommand("kick " + targetName + " " + reason);
                break;
            case "warn":
                staff.performCommand("warn " + targetName + " " + reason);
                break;
        }
    }
    
    private String getDurationFromMaterial(Material material, String type) {
        String matName = material.name();
        if (type.equals("ban")) {
            if (matName.equals("COAL")) return "1h";
            if (matName.equals("IRON_INGOT")) return "1d";
            if (matName.equals("GOLD_INGOT")) return "3d";
            if (matName.equals("DIAMOND")) return "7d";
            if (matName.equals("EMERALD")) return "14d";
            if (matName.equals("OBSIDIAN")) return "30d";
            if (matName.equals("NETHER_STAR")) return "permanent";
        } else if (type.equals("mute")) {
            if (matName.equals("COAL")) return "10m";
            if (matName.equals("IRON_INGOT")) return "30m";
            if (matName.equals("GOLD_INGOT")) return "1h";
            if (matName.equals("DIAMOND")) return "6h";
            if (matName.equals("EMERALD")) return "1d";
            if (matName.equals("OBSIDIAN")) return "7d";
            if (matName.equals("NETHER_STAR")) return "permanent";
        }
        return null;
    }
    
    private String getReasonFromItem(ItemStack item) {
        if (item.getItemMeta() == null || item.getItemMeta().getLore() == null) return null;
        
        for (String line : item.getItemMeta().getLore()) {
            if (line.startsWith("§7Reason: §f")) {
                return line.replace("§7Reason: §f", "");
            }
        }
        return null;
    }
    
    private String extractTargetName(String title) {
        // Find the §7 which precedes the player name
        int index = title.lastIndexOf("§7");
        if (index != -1 && index + 2 < title.length()) {
            return title.substring(index + 2);
        }
        return null;
    }
}
