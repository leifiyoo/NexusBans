package de.meinplugin.gui;

import de.meinplugin.MeinPlugin;
import de.meinplugin.data.Punishment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PunishGUI {
    
    public static final String MAIN_MENU_TITLE = "§8« §c§lPUNISH §8» §7";
    public static final String BAN_DURATION_TITLE = "§8« §4§lBAN TIME §8» §7";
    public static final String MUTE_DURATION_TITLE = "§8« §6§lMUTE TIME §8» §7";
    public static final String BAN_REASON_TITLE = "§8« §4§lBAN REASON §8» §7";
    public static final String MUTE_REASON_TITLE = "§8« §6§lMUTE REASON §8» §7";
    public static final String KICK_REASON_TITLE = "§8« §c§lKICK REASON §8» §7";
    public static final String WARN_REASON_TITLE = "§8« §e§lWARN REASON §8» §7";
    
    public static void openMainMenu(Player staff, String targetName) {
        Inventory inv = Bukkit.createInventory(null, 54, MAIN_MENU_TITLE + targetName);
        
        // Fill borders with black glass
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        ItemStack redGlass = createItem(Material.RED_STAINED_GLASS_PANE, " ", null);
        
        // Top and bottom borders
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, blackGlass);
            inv.setItem(45 + i, blackGlass);
        }
        // Side borders
        for (int i = 1; i < 5; i++) {
            inv.setItem(i * 9, blackGlass);
            inv.setItem(i * 9 + 8, blackGlass);
        }
        
        // Fill middle with gray glass
        ItemStack grayGlass = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", null);
        for (int i = 10; i < 44; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, grayGlass);
            }
        }
        
        // Decorative red corners
        inv.setItem(0, redGlass);
        inv.setItem(8, redGlass);
        inv.setItem(45, redGlass);
        inv.setItem(53, redGlass);
        
        // Player head in center top
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(targetName));
        skullMeta.setDisplayName("§c§l⚔ §f§l" + targetName + " §c§l⚔");
        
        // Get player info
        UUID targetUUID = MeinPlugin.getInstance().getPunishmentManager().getUUID(targetName);
        int warnings = 0;
        String banStatus = "§a✔ Not Banned";
        String muteStatus = "§a✔ Not Muted";
        String onlineStatus = Bukkit.getPlayer(targetName) != null ? "§a● Online" : "§c○ Offline";
        
        if (targetUUID != null) {
            warnings = MeinPlugin.getInstance().getHistoryManager().getWarningCount(targetUUID);
            if (MeinPlugin.getInstance().getPunishmentManager().isBanned(targetUUID)) {
                Punishment ban = MeinPlugin.getInstance().getPunishmentManager().getBan(targetUUID);
                banStatus = ban.isPermanent() ? "§4✘ Permanent Ban" : "§c✘ Temp Banned";
            }
            if (MeinPlugin.getInstance().getPunishmentManager().isMuted(targetUUID)) {
                Punishment mute = MeinPlugin.getInstance().getPunishmentManager().getMute(targetUUID);
                muteStatus = mute.isPermanent() ? "§6✘ Permanent Mute" : "§e✘ Temp Muted";
            }
        }
        
        skullMeta.setLore(Arrays.asList(
            "§8§m─────────────────────",
            "",
            "  §7Status: " + onlineStatus,
            "  §7Warnings: §e" + warnings,
            "",
            "  " + banStatus,
            "  " + muteStatus,
            "",
            "§8§m─────────────────────",
            "",
            "§7Select a punishment below"
        ));
        skull.setItemMeta(skullMeta);
        inv.setItem(4, skull);
        
        // ═══════════════════════════════════════
        // PUNISHMENT BUTTONS - More spread out layout
        // ═══════════════════════════════════════
        
        // Row 2: Ban options
        inv.setItem(19, createItem(Material.BARRIER, "§4§l☠ PERMANENT BAN", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Permanently remove this",
            "§7player from the server.",
            "",
            "§c⚠ This is irreversible!",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to ban"
        )));
        
        inv.setItem(20, createItem(Material.CLOCK, "§c§l⏰ TEMP BAN", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Temporarily ban player",
            "§7for a specific duration.",
            "",
            "§7Available times:",
            "§8 » §f1h, 1d, 3d, 7d, 14d, 30d",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to select time"
        )));
        
        // Row 2: Middle - Kick
        inv.setItem(22, createItem(Material.IRON_BOOTS, "§6§l👢 KICK", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Kick player from server.",
            "§7They can rejoin after.",
            "",
            "§a✔ Non-permanent action",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to kick"
        )));
        
        // Row 2: Mute options
        inv.setItem(24, createItem(Material.PAPER, "§6§l🔇 PERMANENT MUTE", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Permanently mute player.",
            "§7They cannot chat anymore.",
            "",
            "§c⚠ Permanent action!",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to mute"
        )));
        
        inv.setItem(25, createItem(Material.BOOK, "§e§l⏰ TEMP MUTE", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Temporarily mute player",
            "§7for a specific duration.",
            "",
            "§7Available times:",
            "§8 » §f10m, 30m, 1h, 6h, 1d, 7d",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to select time"
        )));
        
        // Row 3: Warn in center
        inv.setItem(31, createItem(Material.SUNFLOWER, "§e§l⚠ WARN", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Issue a warning to player.",
            "",
            "§7Current warnings: §e" + warnings,
            "",
            "§7After §c5 warnings§7:",
            "§8 » §fConsider stronger action",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to warn"
        )));
        
        // Row 4: Utility buttons
        inv.setItem(39, createItem(Material.WRITABLE_BOOK, "§b§l📋 VIEW HISTORY", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7View full punishment",
            "§7history of this player.",
            "",
            "§7Shows: Bans, Mutes,",
            "§7Kicks, Warnings",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to view"
        )));
        
        inv.setItem(41, createItem(Material.REDSTONE_BLOCK, "§c§l✘ CLOSE", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Close this menu.",
            "",
            "§8§m─────────────────────",
            "§e➤ Left-Click to close"
        )));
        
        // Plugin info
        inv.setItem(49, createItem(Material.NETHER_STAR, "§b§lNexus§3§lBan", Arrays.asList(
            "§8§m─────────────────────",
            "",
            "§7Created by §e" + MeinPlugin.AUTHOR,
            "§7Version: §f" + MeinPlugin.getInstance().getDescription().getVersion(),
            "",
            "§8§m─────────────────────"
        )));
        
        staff.openInventory(inv);
    }
    
    public static void openBanDurationMenu(Player staff, String targetName) {
        Inventory inv = Bukkit.createInventory(null, 36, BAN_DURATION_TITLE + targetName);
        
        // Fill with dark red glass
        ItemStack filler = createItem(Material.RED_STAINED_GLASS_PANE, " ", null);
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        
        for (int i = 0; i < 36; i++) {
            inv.setItem(i, filler);
        }
        // Borders
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, blackGlass);
            inv.setItem(27 + i, blackGlass);
        }
        
        // Duration options with better icons
        inv.setItem(10, createDurationItem(Material.COAL, "§7§l1 HOUR", "1h", "§8Short ban for minor issues"));
        inv.setItem(11, createDurationItem(Material.IRON_INGOT, "§f§l1 DAY", "1d", "§8Standard temporary ban"));
        inv.setItem(12, createDurationItem(Material.GOLD_INGOT, "§6§l3 DAYS", "3d", "§8Extended temporary ban"));
        inv.setItem(14, createDurationItem(Material.DIAMOND, "§b§l7 DAYS", "7d", "§8Week-long ban"));
        inv.setItem(15, createDurationItem(Material.EMERALD, "§a§l14 DAYS", "14d", "§8Two week ban"));
        inv.setItem(16, createDurationItem(Material.NETHERITE_INGOT, "§8§l30 DAYS", "30d", "§8Month-long ban"));
        
        // Permanent in center
        inv.setItem(22, createItem(Material.NETHER_STAR, "§4§l☠ PERMANENT", Arrays.asList(
            "§8§m─────────────────",
            "",
            "§7Duration: §4Forever",
            "",
            "§c⚠ Cannot be undone!",
            "",
            "§8§m─────────────────",
            "§e➤ Click to select"
        )));
        
        // Back button
        inv.setItem(31, createItem(Material.ARROW, "§c§l← BACK", Arrays.asList("", "§7Return to main menu")));
        
        staff.openInventory(inv);
    }
    
    public static void openMuteDurationMenu(Player staff, String targetName) {
        Inventory inv = Bukkit.createInventory(null, 36, MUTE_DURATION_TITLE + targetName);
        
        // Fill with orange glass
        ItemStack filler = createItem(Material.ORANGE_STAINED_GLASS_PANE, " ", null);
        ItemStack blackGlass = createItem(Material.BLACK_STAINED_GLASS_PANE, " ", null);
        
        for (int i = 0; i < 36; i++) {
            inv.setItem(i, filler);
        }
        // Borders
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, blackGlass);
            inv.setItem(27 + i, blackGlass);
        }
        
        // Duration options
        inv.setItem(10, createDurationItem(Material.COAL, "§7§l10 MINUTES", "10m", "§8Quick cooldown"));
        inv.setItem(11, createDurationItem(Material.IRON_INGOT, "§f§l30 MINUTES", "30m", "§8Short mute"));
        inv.setItem(12, createDurationItem(Material.GOLD_INGOT, "§6§l1 HOUR", "1h", "§8Standard mute"));
        inv.setItem(14, createDurationItem(Material.DIAMOND, "§b§l6 HOURS", "6h", "§8Extended mute"));
        inv.setItem(15, createDurationItem(Material.EMERALD, "§a§l1 DAY", "1d", "§8Day-long mute"));
        inv.setItem(16, createDurationItem(Material.NETHERITE_INGOT, "§8§l7 DAYS", "7d", "§8Week-long mute"));
        
        // Permanent in center
        inv.setItem(22, createItem(Material.NETHER_STAR, "§6§l🔇 PERMANENT", Arrays.asList(
            "§8§m─────────────────",
            "",
            "§7Duration: §6Forever",
            "",
            "§e⚠ Permanent silence!",
            "",
            "§8§m─────────────────",
            "§e➤ Click to select"
        )));
        
        // Back button
        inv.setItem(31, createItem(Material.ARROW, "§c§l← BACK", Arrays.asList("", "§7Return to main menu")));
        
        staff.openInventory(inv);
    }
    
    public static void openReasonMenu(Player staff, String targetName, String type) {
        String title;
        Material fillerMaterial;
        Material borderMaterial = Material.BLACK_STAINED_GLASS_PANE;
        
        switch (type) {
            case "ban":
                title = BAN_REASON_TITLE + targetName;
                fillerMaterial = Material.RED_STAINED_GLASS_PANE;
                break;
            case "mute":
                title = MUTE_REASON_TITLE + targetName;
                fillerMaterial = Material.ORANGE_STAINED_GLASS_PANE;
                break;
            case "kick":
                title = KICK_REASON_TITLE + targetName;
                fillerMaterial = Material.YELLOW_STAINED_GLASS_PANE;
                break;
            case "warn":
                title = WARN_REASON_TITLE + targetName;
                fillerMaterial = Material.LIME_STAINED_GLASS_PANE;
                break;
            default:
                return;
        }
        
        Inventory inv = Bukkit.createInventory(null, 45, title);
        
        // Fill
        ItemStack filler = createItem(fillerMaterial, " ", null);
        ItemStack border = createItem(borderMaterial, " ", null);
        
        for (int i = 0; i < 45; i++) {
            inv.setItem(i, filler);
        }
        // Borders
        for (int i = 0; i < 9; i++) {
            inv.setItem(i, border);
            inv.setItem(36 + i, border);
        }
        
        // Reason options based on type
        if (type.equals("ban")) {
            inv.setItem(10, createReasonItem(Material.DIAMOND_SWORD, "§c§lHACKING", "Hacking / Cheating", "§7Using unfair advantages"));
            inv.setItem(11, createReasonItem(Material.TNT, "§c§lGRIEFING", "Griefing", "§7Destroying others' builds"));
            inv.setItem(12, createReasonItem(Material.REPEATER, "§c§lEXPLOITING", "Bug Exploiting", "§7Abusing game bugs"));
            inv.setItem(14, createReasonItem(Material.SKELETON_SKULL, "§c§lTOXICITY", "Toxic Behavior", "§7Extreme toxic behavior"));
            inv.setItem(15, createReasonItem(Material.NAME_TAG, "§c§lADVERTISING", "Advertising", "§7Promoting other servers"));
            inv.setItem(16, createReasonItem(Material.PLAYER_HEAD, "§c§lBAN EVASION", "Ban Evasion", "§7Alt account of banned player"));
            inv.setItem(22, createReasonItem(Material.COMMAND_BLOCK, "§c§lOTHER", "Other", "§7Custom reason"));
        } else if (type.equals("mute")) {
            inv.setItem(10, createReasonItem(Material.PAPER, "§6§lSPAMMING", "Chat Spam", "§7Flooding the chat"));
            inv.setItem(11, createReasonItem(Material.SKELETON_SKULL, "§6§lTOXICITY", "Toxic Behavior", "§7Being toxic in chat"));
            inv.setItem(12, createReasonItem(Material.NAME_TAG, "§6§lADVERTISING", "Advertising", "§7Promoting other servers"));
            inv.setItem(14, createReasonItem(Material.BOOK, "§6§lLANGUAGE", "Inappropriate Language", "§7Using bad language"));
            inv.setItem(15, createReasonItem(Material.IRON_SWORD, "§6§lHARASSMENT", "Harassment", "§7Harassing other players"));
            inv.setItem(16, createReasonItem(Material.COMMAND_BLOCK, "§6§lOTHER", "Other", "§7Custom reason"));
        } else if (type.equals("kick")) {
            inv.setItem(11, createReasonItem(Material.HOPPER, "§e§lAFK", "Being AFK", "§7Away from keyboard"));
            inv.setItem(12, createReasonItem(Material.PAPER, "§e§lSPAMMING", "Chat Spam", "§7Flooding the chat"));
            inv.setItem(14, createReasonItem(Material.SKELETON_SKULL, "§e§lBEHAVIOR", "Inappropriate Behavior", "§7Breaking rules"));
            inv.setItem(15, createReasonItem(Material.COMMAND_BLOCK, "§e§lOTHER", "Other", "§7Custom reason"));
        } else if (type.equals("warn")) {
            inv.setItem(10, createReasonItem(Material.PAPER, "§e§lSPAMMING", "Chat Spam", "§7Flooding the chat"));
            inv.setItem(11, createReasonItem(Material.SKELETON_SKULL, "§e§lTOXICITY", "Toxic Behavior", "§7Being toxic"));
            inv.setItem(12, createReasonItem(Material.BOOK, "§e§lLANGUAGE", "Inappropriate Language", "§7Using bad language"));
            inv.setItem(14, createReasonItem(Material.IRON_SWORD, "§e§lBEHAVIOR", "Inappropriate Behavior", "§7Breaking rules"));
            inv.setItem(15, createReasonItem(Material.BARRIER, "§e§lRULES", "Breaking Rules", "§7General rule violation"));
            inv.setItem(16, createReasonItem(Material.COMMAND_BLOCK, "§e§lOTHER", "Other", "§7Custom reason"));
        }
        
        // Back button
        inv.setItem(40, createItem(Material.ARROW, "§c§l← BACK", Arrays.asList("", "§7Return to previous menu")));
        
        staff.openInventory(inv);
    }
    
    private static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        if (lore != null) {
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
    
    private static ItemStack createDurationItem(Material material, String name, String duration, String description) {
        return createItem(material, name, Arrays.asList(
            "§8§m─────────────────",
            "",
            "§7Duration: §f" + duration,
            description,
            "",
            "§8§m─────────────────",
            "§e➤ Click to select"
        ));
    }
    
    private static ItemStack createReasonItem(Material material, String name, String reason, String description) {
        return createItem(material, name, Arrays.asList(
            "§8§m─────────────────",
            "",
            "§7Reason: §f" + reason,
            description,
            "",
            "§8§m─────────────────",
            "§e➤ Click to apply"
        ));
    }
}
