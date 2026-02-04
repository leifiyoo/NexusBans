package de.nexusban.managers;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FreezeManager {

    // Special UUID for console-initiated freezes
    private static final UUID CONSOLE_UUID = new UUID(0L, 0L);

    // Map of frozen player UUID to admin UUID who froze them
    private final Map<UUID, UUID> frozenPlayers = new ConcurrentHashMap<>();

    /**
     * Freeze a player by an admin
     *
     * @param player The player to freeze
     * @param admin  The admin who is freezing the player (null for console)
     */
    public void freezePlayer(Player player, UUID admin) {
        // Use special UUID for console to avoid NullPointerException
        frozenPlayers.put(player.getUniqueId(), admin != null ? admin : CONSOLE_UUID);
    }

    /**
     * Unfreeze a player
     *
     * @param player The player to unfreeze
     */
    public void unfreezePlayer(Player player) {
        frozenPlayers.remove(player.getUniqueId());
    }

    /**
     * Check if a player is frozen
     *
     * @param player The player to check
     * @return true if the player is frozen
     */
    public boolean isFrozen(Player player) {
        return frozenPlayers.containsKey(player.getUniqueId());
    }

    /**
     * Clear all frozen players (used on plugin disable)
     */
    public void clearAll() {
        frozenPlayers.clear();
    }
}
