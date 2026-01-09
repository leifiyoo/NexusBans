package de.nexusban.managers;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import de.nexusban.data.Punishment.PunishmentType;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.OfflinePlayer;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PunishmentManager {
    
    private final NexusBan plugin;
    private final Map<UUID, Punishment> activeBans;
    private final Map<UUID, Punishment> activeMutes;
    private final Map<String, Punishment> nameBans;
    private final Map<String, Punishment> nameMutes;
    
    // IP Ban System
    private final Map<String, Punishment> ipBans;
    
    // Alt Detection
    private final Map<String, Set<UUID>> ipToPlayers;
    private final Map<UUID, Set<String>> playerToIps;
    
    private final File punishmentsFile;
    private final File playerDataFile;
    private FileConfiguration punishmentsConfig;
    private FileConfiguration playerDataConfig;
    
    public PunishmentManager(NexusBan plugin) {
        this.plugin = plugin;
        this.activeBans = new ConcurrentHashMap<>();
        this.activeMutes = new ConcurrentHashMap<>();
        this.nameBans = new ConcurrentHashMap<>();
        this.nameMutes = new ConcurrentHashMap<>();
        this.ipBans = new ConcurrentHashMap<>();
        this.ipToPlayers = new ConcurrentHashMap<>();
        this.playerToIps = new ConcurrentHashMap<>();
        
        this.punishmentsFile = new File(plugin.getDataFolder(), "punishments.yml");
        this.playerDataFile = new File(plugin.getDataFolder(), "playerdata.yml");
        
        loadPunishments();
        loadPlayerData();
        startCleanupTask();
    }
    
    // ==================== LOADING ====================
    
    private void loadPunishments() {
        if (!punishmentsFile.exists()) {
            try {
                punishmentsFile.getParentFile().mkdirs();
                punishmentsFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        punishmentsConfig = YamlConfiguration.loadConfiguration(punishmentsFile);
        
        // Load UUID-based bans
        ConfigurationSection bansSection = punishmentsConfig.getConfigurationSection("bans");
        if (bansSection != null) {
            for (String uuidStr : bansSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    String path = "bans." + uuidStr;
                    Punishment punishment = new Punishment(
                        uuid,
                        punishmentsConfig.getString(path + ".playerName"),
                        PunishmentType.valueOf(punishmentsConfig.getString(path + ".type")),
                        punishmentsConfig.getString(path + ".reason"),
                        punishmentsConfig.getString(path + ".punisher"),
                        punishmentsConfig.getLong(path + ".startTime"),
                        punishmentsConfig.getLong(path + ".endTime")
                    );
                    if (!punishment.isExpired()) {
                        activeBans.put(uuid, punishment);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load ban for " + uuidStr);
                }
            }
        }
        
        // Load UUID-based mutes
        ConfigurationSection mutesSection = punishmentsConfig.getConfigurationSection("mutes");
        if (mutesSection != null) {
            for (String uuidStr : mutesSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    String path = "mutes." + uuidStr;
                    Punishment punishment = new Punishment(
                        uuid,
                        punishmentsConfig.getString(path + ".playerName"),
                        PunishmentType.valueOf(punishmentsConfig.getString(path + ".type")),
                        punishmentsConfig.getString(path + ".reason"),
                        punishmentsConfig.getString(path + ".punisher"),
                        punishmentsConfig.getLong(path + ".startTime"),
                        punishmentsConfig.getLong(path + ".endTime")
                    );
                    if (!punishment.isExpired()) {
                        activeMutes.put(uuid, punishment);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load mute for " + uuidStr);
                }
            }
        }
        
        // Load name-based bans
        ConfigurationSection nameBansSection = punishmentsConfig.getConfigurationSection("namebans");
        if (nameBansSection != null) {
            for (String name : nameBansSection.getKeys(false)) {
                try {
                    String path = "namebans." + name;
                    Punishment punishment = new Punishment(
                        null,
                        punishmentsConfig.getString(path + ".playerName"),
                        PunishmentType.valueOf(punishmentsConfig.getString(path + ".type")),
                        punishmentsConfig.getString(path + ".reason"),
                        punishmentsConfig.getString(path + ".punisher"),
                        punishmentsConfig.getLong(path + ".startTime"),
                        punishmentsConfig.getLong(path + ".endTime")
                    );
                    if (!punishment.isExpired()) {
                        nameBans.put(name.toLowerCase(), punishment);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load name ban for " + name);
                }
            }
        }
        
        // Load name-based mutes
        ConfigurationSection nameMutesSection = punishmentsConfig.getConfigurationSection("namemutes");
        if (nameMutesSection != null) {
            for (String name : nameMutesSection.getKeys(false)) {
                try {
                    String path = "namemutes." + name;
                    Punishment punishment = new Punishment(
                        null,
                        punishmentsConfig.getString(path + ".playerName"),
                        PunishmentType.valueOf(punishmentsConfig.getString(path + ".type")),
                        punishmentsConfig.getString(path + ".reason"),
                        punishmentsConfig.getString(path + ".punisher"),
                        punishmentsConfig.getLong(path + ".startTime"),
                        punishmentsConfig.getLong(path + ".endTime")
                    );
                    if (!punishment.isExpired()) {
                        nameMutes.put(name.toLowerCase(), punishment);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load name mute for " + name);
                }
            }
        }
        
        // Load IP bans
        ConfigurationSection ipBansSection = punishmentsConfig.getConfigurationSection("ipbans");
        if (ipBansSection != null) {
            for (String ipKey : ipBansSection.getKeys(false)) {
                try {
                    String path = "ipbans." + ipKey;
                    String ip = punishmentsConfig.getString(path + ".ip", ipKey.replace("_", "."));
                    Punishment punishment = new Punishment(
                        null,
                        punishmentsConfig.getString(path + ".playerName"),
                        PunishmentType.IPBAN,
                        punishmentsConfig.getString(path + ".reason"),
                        punishmentsConfig.getString(path + ".punisher"),
                        punishmentsConfig.getLong(path + ".startTime"),
                        punishmentsConfig.getLong(path + ".endTime")
                    );
                    if (!punishment.isExpired()) {
                        ipBans.put(ip, punishment);
                    }
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load IP ban for " + ipKey);
                }
            }
        }
        
        plugin.getLogger().info("Loaded " + activeBans.size() + " bans, " + activeMutes.size() + " mutes, " + ipBans.size() + " IP bans.");
    }
    
    private void loadPlayerData() {
        if (!playerDataFile.exists()) {
            try {
                playerDataFile.getParentFile().mkdirs();
                playerDataFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        playerDataConfig = YamlConfiguration.loadConfiguration(playerDataFile);
        
        // Load IP to player mappings
        ConfigurationSection ipsSection = playerDataConfig.getConfigurationSection("ips");
        if (ipsSection != null) {
            for (String ipKey : ipsSection.getKeys(false)) {
                String ip = ipKey.replace("_", ".");
                List<String> uuidStrings = playerDataConfig.getStringList("ips." + ipKey);
                Set<UUID> uuids = ConcurrentHashMap.newKeySet();
                for (String uuidStr : uuidStrings) {
                    try {
                        uuids.add(UUID.fromString(uuidStr));
                    } catch (Exception ignored) {}
                }
                if (!uuids.isEmpty()) {
                    ipToPlayers.put(ip, uuids);
                }
            }
        }
        
        // Load player to IP mappings
        ConfigurationSection playersSection = playerDataConfig.getConfigurationSection("players");
        if (playersSection != null) {
            for (String uuidStr : playersSection.getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(uuidStr);
                    List<String> ips = playerDataConfig.getStringList("players." + uuidStr + ".ips");
                    Set<String> ipSet = ConcurrentHashMap.newKeySet();
                    ipSet.addAll(ips);
                    playerToIps.put(uuid, ipSet);
                } catch (Exception ignored) {}
            }
        }
    }
    
    // ==================== ASYNC SAVING ====================
    
    private void saveAsync(Runnable saveTask) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (punishmentsConfig) {
                saveTask.run();
                try {
                    punishmentsConfig.save(punishmentsFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public void saveAll() {
        for (Map.Entry<UUID, Punishment> entry : activeBans.entrySet()) {
            savePunishmentSync("bans", entry.getValue());
        }
        for (Map.Entry<UUID, Punishment> entry : activeMutes.entrySet()) {
            savePunishmentSync("mutes", entry.getValue());
        }
        try {
            punishmentsConfig.save(punishmentsFile);
            playerDataConfig.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void savePunishmentSync(String category, Punishment punishment) {
        if (punishment.getPlayerUUID() == null) return;
        String path = category + "." + punishment.getPlayerUUID().toString();
        punishmentsConfig.set(path + ".playerName", punishment.getPlayerName());
        punishmentsConfig.set(path + ".type", punishment.getType().name());
        punishmentsConfig.set(path + ".reason", punishment.getReason());
        punishmentsConfig.set(path + ".punisher", punishment.getPunisherName());
        punishmentsConfig.set(path + ".startTime", punishment.getStartTime());
        punishmentsConfig.set(path + ".endTime", punishment.getEndTime());
    }
    
    // ==================== BAN METHODS ====================
    
    public void addBan(Punishment punishment) {
        if (punishment.getPlayerUUID() == null) return;
        activeBans.put(punishment.getPlayerUUID(), punishment);
        saveAsync(() -> savePunishmentSync("bans", punishment));
        plugin.getHistoryManager().addHistory(punishment);
    }
    
    public void removeBan(UUID uuid) {
        activeBans.remove(uuid);
        saveAsync(() -> punishmentsConfig.set("bans." + uuid.toString(), null));
    }
    
    public boolean isBanned(UUID uuid) {
        Punishment ban = activeBans.get(uuid);
        if (ban == null) return false;
        if (ban.isExpired()) {
            removeBan(uuid);
            return false;
        }
        return true;
    }
    
    public Punishment getBan(UUID uuid) {
        return activeBans.get(uuid);
    }
    
    public Collection<Punishment> getAllBans() {
        return activeBans.values();
    }
    
    // ==================== MUTE METHODS ====================
    
    public void addMute(Punishment punishment) {
        if (punishment.getPlayerUUID() == null) return;
        activeMutes.put(punishment.getPlayerUUID(), punishment);
        saveAsync(() -> savePunishmentSync("mutes", punishment));
        plugin.getHistoryManager().addHistory(punishment);
    }
    
    public void removeMute(UUID uuid) {
        activeMutes.remove(uuid);
        saveAsync(() -> punishmentsConfig.set("mutes." + uuid.toString(), null));
    }
    
    public boolean isMuted(UUID uuid) {
        Punishment mute = activeMutes.get(uuid);
        if (mute == null) return false;
        if (mute.isExpired()) {
            removeMute(uuid);
            return false;
        }
        return true;
    }
    
    public Punishment getMute(UUID uuid) {
        return activeMutes.get(uuid);
    }
    
    public Collection<Punishment> getAllMutes() {
        return activeMutes.values();
    }
    
    // ==================== NAME-BASED BANS ====================
    
    public void addNameBan(Punishment punishment) {
        nameBans.put(punishment.getPlayerName().toLowerCase(), punishment);
        saveAsync(() -> {
            String path = "namebans." + punishment.getPlayerName().toLowerCase();
            punishmentsConfig.set(path + ".playerName", punishment.getPlayerName());
            punishmentsConfig.set(path + ".type", punishment.getType().name());
            punishmentsConfig.set(path + ".reason", punishment.getReason());
            punishmentsConfig.set(path + ".punisher", punishment.getPunisherName());
            punishmentsConfig.set(path + ".startTime", punishment.getStartTime());
            punishmentsConfig.set(path + ".endTime", punishment.getEndTime());
        });
        plugin.getHistoryManager().addHistory(punishment);
    }
    
    public void removeNameBan(String name) {
        nameBans.remove(name.toLowerCase());
        saveAsync(() -> punishmentsConfig.set("namebans." + name.toLowerCase(), null));
    }
    
    public boolean isNameBanned(String name) {
        Punishment ban = nameBans.get(name.toLowerCase());
        if (ban == null) return false;
        if (ban.isExpired()) {
            removeNameBan(name);
            return false;
        }
        return true;
    }
    
    public Punishment getNameBan(String name) {
        return nameBans.get(name.toLowerCase());
    }
    
    // ==================== NAME-BASED MUTES ====================
    
    public void addNameMute(Punishment punishment) {
        nameMutes.put(punishment.getPlayerName().toLowerCase(), punishment);
        saveAsync(() -> {
            String path = "namemutes." + punishment.getPlayerName().toLowerCase();
            punishmentsConfig.set(path + ".playerName", punishment.getPlayerName());
            punishmentsConfig.set(path + ".type", punishment.getType().name());
            punishmentsConfig.set(path + ".reason", punishment.getReason());
            punishmentsConfig.set(path + ".punisher", punishment.getPunisherName());
            punishmentsConfig.set(path + ".startTime", punishment.getStartTime());
            punishmentsConfig.set(path + ".endTime", punishment.getEndTime());
        });
        plugin.getHistoryManager().addHistory(punishment);
    }
    
    public void removeNameMute(String name) {
        nameMutes.remove(name.toLowerCase());
        saveAsync(() -> punishmentsConfig.set("namemutes." + name.toLowerCase(), null));
    }
    
    public boolean isNameMuted(String name) {
        Punishment mute = nameMutes.get(name.toLowerCase());
        if (mute == null) return false;
        if (mute.isExpired()) {
            removeNameMute(name);
            return false;
        }
        return true;
    }
    
    public Punishment getNameMute(String name) {
        return nameMutes.get(name.toLowerCase());
    }
    
    public Collection<Punishment> getAllNameBans() {
        return nameBans.values();
    }
    
    // ==================== IP BAN SYSTEM ====================
    
    public void addIpBan(String ip, Punishment punishment) {
        ipBans.put(ip, punishment);
        saveAsync(() -> {
            String path = "ipbans." + ip.replace(".", "_");
            punishmentsConfig.set(path + ".playerName", punishment.getPlayerName());
            punishmentsConfig.set(path + ".type", punishment.getType().name());
            punishmentsConfig.set(path + ".reason", punishment.getReason());
            punishmentsConfig.set(path + ".punisher", punishment.getPunisherName());
            punishmentsConfig.set(path + ".startTime", punishment.getStartTime());
            punishmentsConfig.set(path + ".endTime", punishment.getEndTime());
            punishmentsConfig.set(path + ".ip", ip);
        });
        plugin.getHistoryManager().addHistory(punishment);
    }
    
    public void removeIpBan(String ip) {
        ipBans.remove(ip);
        saveAsync(() -> punishmentsConfig.set("ipbans." + ip.replace(".", "_"), null));
    }
    
    public boolean isIpBanned(String ip) {
        Punishment ban = ipBans.get(ip);
        if (ban == null) return false;
        if (ban.isExpired()) {
            removeIpBan(ip);
            return false;
        }
        return true;
    }
    
    public Punishment getIpBan(String ip) {
        return ipBans.get(ip);
    }
    
    public Collection<Punishment> getAllIpBans() {
        return ipBans.values();
    }
    
    public Map<String, Punishment> getIpBansMap() {
        return new HashMap<>(ipBans);
    }
    
    // ==================== ALT DETECTION ====================
    
    public void recordPlayerIp(UUID uuid, String ip, String playerName) {
        playerToIps.computeIfAbsent(uuid, k -> ConcurrentHashMap.newKeySet()).add(ip);
        ipToPlayers.computeIfAbsent(ip, k -> ConcurrentHashMap.newKeySet()).add(uuid);
        
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            synchronized (playerDataConfig) {
                playerDataConfig.set("players." + uuid.toString() + ".name", playerName);
                playerDataConfig.set("players." + uuid.toString() + ".ips", new ArrayList<>(playerToIps.get(uuid)));
                playerDataConfig.set("players." + uuid.toString() + ".lastIp", ip);
                playerDataConfig.set("players." + uuid.toString() + ".lastSeen", System.currentTimeMillis());
                
                List<String> uuidStrings = new ArrayList<>();
                for (UUID u : ipToPlayers.get(ip)) {
                    uuidStrings.add(u.toString());
                }
                playerDataConfig.set("ips." + ip.replace(".", "_"), uuidStrings);
                
                try {
                    playerDataConfig.save(playerDataFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    public Set<UUID> getPlayersOnIp(String ip) {
        return ipToPlayers.getOrDefault(ip, Collections.emptySet());
    }
    
    public Set<String> getPlayerIps(UUID uuid) {
        return playerToIps.getOrDefault(uuid, Collections.emptySet());
    }
    
    public Set<UUID> getAlts(UUID uuid) {
        Set<UUID> alts = new HashSet<>();
        Set<String> ips = getPlayerIps(uuid);
        for (String ip : ips) {
            alts.addAll(getPlayersOnIp(ip));
        }
        alts.remove(uuid);
        return alts;
    }
    
    public String getPlayerName(UUID uuid) {
        return playerDataConfig.getString("players." + uuid.toString() + ".name", "Unknown");
    }
    
    public String getLastIp(UUID uuid) {
        return playerDataConfig.getString("players." + uuid.toString() + ".lastIp", null);
    }
    
    // ==================== STAFF PROTECTION ====================
    
    public boolean canPunish(CommandSender punisher, UUID targetUUID) {
        if (punisher.hasPermission("nexusban.bypass.protection")) return true;
        if (!(punisher instanceof Player)) return true;
        
        OfflinePlayer target = plugin.getServer().getOfflinePlayer(targetUUID);
        if (target.isOnline() && target.getPlayer().hasPermission("nexusban.exempt")) {
            return false;
        }
        if (punisher.hasPermission("nexusban.admin")) {
            return !(target.isOnline() && target.getPlayer().hasPermission("nexusban.admin"));
        }
        if (punisher.hasPermission("nexusban.moderator")) {
            if (target.isOnline()) {
                return !target.getPlayer().hasPermission("nexusban.admin") && 
                       !target.getPlayer().hasPermission("nexusban.moderator");
            }
        }
        return true;
    }
    
    public boolean canPunish(CommandSender punisher, String targetName) {
        Player online = plugin.getServer().getPlayerExact(targetName);
        if (online != null) {
            return canPunish(punisher, online.getUniqueId());
        }
        return true;
    }
    
    // ==================== UTILITY ====================
    private void startCleanupTask() {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            activeBans.entrySet().removeIf(entry -> entry.getValue().isExpired());
            activeMutes.entrySet().removeIf(entry -> entry.getValue().isExpired());
            nameBans.entrySet().removeIf(entry -> entry.getValue().isExpired());
            nameMutes.entrySet().removeIf(entry -> entry.getValue().isExpired());
            ipBans.entrySet().removeIf(entry -> entry.getValue().isExpired());
        }, 20L * 60, 20L * 60);
    }
    
    public UUID getUUID(String playerName) {
        Player online = plugin.getServer().getPlayerExact(playerName);
        if (online != null) return online.getUniqueId();
        
        OfflinePlayer offline = plugin.getServer().getOfflinePlayer(playerName);
        if (offline.hasPlayedBefore()) return offline.getUniqueId();
        
        return null;
    }
}
