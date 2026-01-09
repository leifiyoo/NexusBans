package de.nexusban.utils;

import org.bukkit.Material;

/**
 * Utility class for Material compatibility across Minecraft versions.
 * Handles the Material enum changes between 1.12 and 1.13+
 */
public class MaterialCompat {
    
    private static final boolean IS_LEGACY;
    
    static {
        // Check if we're on 1.13+ by trying to access a 1.13+ material
        boolean legacy = false;
        try {
            Material.valueOf("BLACK_STAINED_GLASS_PANE");
        } catch (IllegalArgumentException e) {
            legacy = true;
        }
        IS_LEGACY = legacy;
    }
    
    public static boolean isLegacy() {
        return IS_LEGACY;
    }
    
    /**
     * Get black stained glass pane (works on all versions)
     */
    @SuppressWarnings("deprecation")
    public static org.bukkit.inventory.ItemStack getBlackGlass() {
        if (IS_LEGACY) {
            return new org.bukkit.inventory.ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 15);
        }
        return new org.bukkit.inventory.ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    }
    
    /**
     * Get red stained glass pane
     */
    @SuppressWarnings("deprecation")
    public static org.bukkit.inventory.ItemStack getRedGlass() {
        if (IS_LEGACY) {
            return new org.bukkit.inventory.ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 14);
        }
        return new org.bukkit.inventory.ItemStack(Material.RED_STAINED_GLASS_PANE);
    }
    
    /**
     * Get gray stained glass pane
     */
    @SuppressWarnings("deprecation")
    public static org.bukkit.inventory.ItemStack getGrayGlass() {
        if (IS_LEGACY) {
            return new org.bukkit.inventory.ItemStack(Material.valueOf("STAINED_GLASS_PANE"), 1, (short) 7);
        }
        return new org.bukkit.inventory.ItemStack(Material.GRAY_STAINED_GLASS_PANE);
    }
    
    /**
     * Get player head material
     */
    public static Material getPlayerHead() {
        if (IS_LEGACY) {
            return Material.valueOf("SKULL_ITEM");
        }
        return Material.PLAYER_HEAD;
    }
    
    /**
     * Get player head item with correct data value for legacy
     */
    @SuppressWarnings("deprecation")
    public static org.bukkit.inventory.ItemStack getPlayerHeadItem() {
        if (IS_LEGACY) {
            return new org.bukkit.inventory.ItemStack(Material.valueOf("SKULL_ITEM"), 1, (short) 3);
        }
        return new org.bukkit.inventory.ItemStack(Material.PLAYER_HEAD);
    }
    
    /**
     * Get clock material (CLOCK in 1.13+, WATCH in 1.12)
     */
    public static Material getClock() {
        if (IS_LEGACY) {
            return Material.valueOf("WATCH");
        }
        return Material.CLOCK;
    }
    
    /**
     * Get writable book material
     */
    public static Material getWritableBook() {
        if (IS_LEGACY) {
            return Material.valueOf("BOOK_AND_QUILL");
        }
        return Material.WRITABLE_BOOK;
    }
    
    /**
     * Get sunflower material (SUNFLOWER in 1.13+, DOUBLE_PLANT in 1.12)
     */
    @SuppressWarnings("deprecation")
    public static org.bukkit.inventory.ItemStack getSunflower() {
        if (IS_LEGACY) {
            return new org.bukkit.inventory.ItemStack(Material.valueOf("DOUBLE_PLANT"), 1, (short) 0);
        }
        return new org.bukkit.inventory.ItemStack(Material.SUNFLOWER);
    }
    
    /**
     * Safe material getter - returns STONE if material doesn't exist
     */
    public static Material getMaterial(String modernName, String legacyName) {
        try {
            if (IS_LEGACY && legacyName != null) {
                return Material.valueOf(legacyName);
            }
            return Material.valueOf(modernName);
        } catch (IllegalArgumentException e) {
            return Material.STONE;
        }
    }
}
