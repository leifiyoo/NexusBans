package de.meinplugin.listeners;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import de.meinplugin.gui.PunishGUI;
import de.meinplugin.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GUIListener implements Listener {
    
    private final MeinPlugin plugin;
    
    // Store pending punishments (staff UUID -> [target, type, duration])
    private final Map<UUID, String[]> pendingPunishments = new HashMap<>();
    
    public GUIListener(MeinPlugin plugin) {
        this.plugin = plugin;
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
        
        switch (type) {
            case BARRIER: // Ban
                pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "ban", "permanent"});
                PunishGUI.openReasonMenu(player, targetName, "ban");
                break;
            case CLOCK: // Temp Ban
                PunishGUI.openBanDurationMenu(player, targetName);
                break;
            case IRON_BOOTS: // Kick
                pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "kick", null});
                PunishGUI.openReasonMenu(player, targetName, "kick");
                break;
            case PAPER: // Mute
                pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "mute", "permanent"});
                PunishGUI.openReasonMenu(player, targetName, "mute");
                break;
            case BOOK: // Temp Mute
                PunishGUI.openMuteDurationMenu(player, targetName);
                break;
            case SUNFLOWER: // Warn
                pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "warn", null});
                PunishGUI.openReasonMenu(player, targetName, "warn");
                break;
            case WRITABLE_BOOK: // History
                player.closeInventory();
                player.performCommand("history " + targetName);
                break;
            case REDSTONE_BLOCK: // Close
                player.closeInventory();
                break;
        }
    }
    
    private void handleBanDuration(Player player, ItemStack clicked, String targetName) {
        if (clicked.getType() == Material.ARROW) {
            PunishGUI.openMainMenu(player, targetName);
            return;
        }
        
        String duration = getDurationFromMaterial(clicked.getType(), "ban");
        if (duration != null) {
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "tempban", duration});
            PunishGUI.openReasonMenu(player, targetName, "ban");
        }
    }
    
    private void handleMuteDuration(Player player, ItemStack clicked, String targetName) {
        if (clicked.getType() == Material.ARROW) {
            PunishGUI.openMainMenu(player, targetName);
            return;
        }
        
        String duration = getDurationFromMaterial(clicked.getType(), "mute");
        if (duration != null) {
            pendingPunishments.put(player.getUniqueId(), new String[]{targetName, "tempmute", duration});
            PunishGUI.openReasonMenu(player, targetName, "mute");
        }
    }
    
    private void handleReasonMenu(Player player, ItemStack clicked, String targetName, String title) {
        if (clicked.getType() == Material.ARROW) {
            // Go back
            if (title.startsWith(PunishGUI.BAN_REASON_TITLE)) {
                String[] pending = pendingPunishments.get(player.getUniqueId());
                if (pending != null && pending[1].equals("tempban")) {
                    PunishGUI.openBanDurationMenu(player, targetName);
                } else {
                    PunishGUI.openMainMenu(player, targetName);
                }
            } else if (title.startsWith(PunishGUI.MUTE_REASON_TITLE)) {
                String[] pending = pendingPunishments.get(player.getUniqueId());
                if (pending != null && pending[1].equals("tempmute")) {
                    PunishGUI.openMuteDurationMenu(player, targetName);
                } else {
                    PunishGUI.openMainMenu(player, targetName);
                }
            } else {
                PunishGUI.openMainMenu(player, targetName);
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
        if (type.equals("ban")) {
            switch (material) {
                case COAL: return "1h";
                case IRON_INGOT: return "1d";
                case GOLD_INGOT: return "3d";
                case DIAMOND: return "7d";
                case EMERALD: return "14d";
                case NETHERITE_INGOT: return "30d";
                case NETHER_STAR: return "permanent";
            }
        } else if (type.equals("mute")) {
            switch (material) {
                case COAL: return "10m";
                case IRON_INGOT: return "30m";
                case GOLD_INGOT: return "1h";
                case DIAMOND: return "6h";
                case EMERALD: return "1d";
                case NETHERITE_INGOT: return "7d";
                case NETHER_STAR: return "permanent";
            }
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
