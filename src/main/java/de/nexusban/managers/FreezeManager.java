package de.nexusban.managers;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FreezeManager {

    // Map of frozen player UUID to admin UUID who froze them
    private final Map<UUID, UUID> frozenPlayers = new ConcurrentHashMap<>();

    /**
     * Freeze a player by an admin
     *
     * @param player The player to freeze
     * @param admin  The admin who is freezing the player
     */
    public void freezePlayer(Player player, UUID admin) {
        frozenPlayers.put(player.getUniqueId(), admin);
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
     * Check if a player is frozen
     *
     * @param uuid The UUID of the player to check
     * @return true if the player is frozen
     */
    public boolean isFrozen(UUID uuid) {
        return frozenPlayers.containsKey(uuid);
    }

    /**
     * Get the admin who froze a player
     *
     * @param player The frozen player
     * @return The UUID of the admin who froze the player, or null if not frozen
     */
    public UUID getAdmin(Player player) {
        return frozenPlayers.get(player.getUniqueId());
    }

    /**
     * Get the admin who froze a player
     *
     * @param uuid The UUID of the frozen player
     * @return The UUID of the admin who froze the player, or null if not frozen
     */
    public UUID getAdmin(UUID uuid) {
        return frozenPlayers.get(uuid);
    }

    /**
     * Get all frozen players
     *
     * @return Set of UUIDs of all frozen players
     */
    public Set<UUID> getFrozenPlayers() {
        return frozenPlayers.keySet();
    }

    /**
     * Clear all frozen players (used on plugin disable)
     */
    public void clearAll() {
        frozenPlayers.clear();
    }
}
