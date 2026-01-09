package de.nexusban.managers;

import de.nexusban.NexusBan;
import de.nexusban.data.Punishment;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class HistoryManager {
    
    private final NexusBan plugin;
    private final Map<UUID, List<Punishment>> playerHistory;
    private final Map<String, List<Punishment>> nameHistory; // For players without UUID
    private final File historyFile;
    private FileConfiguration historyConfig;
    
    public HistoryManager(NexusBan plugin) {
        this.plugin = plugin;
        this.playerHistory = new ConcurrentHashMap<>();
        this.nameHistory = new ConcurrentHashMap<>();
        this.historyFile = new File(plugin.getDataFolder(), "history.yml");
        loadHistory();
    }
    
    private void loadHistory() {
        if (!historyFile.exists()) {
            try {
                historyFile.getParentFile().mkdirs();
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        historyConfig = YamlConfiguration.loadConfiguration(historyFile);
        
        // Load UUID-based history
        if (historyConfig.contains("history")) {
            for (String uuidStr : historyConfig.getConfigurationSection("history").getKeys(false)) {
                UUID uuid = UUID.fromString(uuidStr);
                List<Punishment> punishments = new ArrayList<>();
                
                for (String idStr : historyConfig.getConfigurationSection("history." + uuidStr).getKeys(false)) {
                    String path = "history." + uuidStr + "." + idStr;
                    
                    Punishment punishment = new Punishment(
                        uuid,
                        historyConfig.getString(path + ".playerName"),
                        Punishment.PunishmentType.valueOf(historyConfig.getString(path + ".type")),
                        historyConfig.getString(path + ".reason"),
                        historyConfig.getString(path + ".punisher"),
                        historyConfig.getLong(path + ".startTime"),
                        historyConfig.getLong(path + ".endTime")
                    );
                    
                    punishments.add(punishment);
                }
                
                playerHistory.put(uuid, punishments);
            }
        }
        
        // Load name-based history
        if (historyConfig.contains("namehistory")) {
            for (String name : historyConfig.getConfigurationSection("namehistory").getKeys(false)) {
                List<Punishment> punishments = new ArrayList<>();
                
                for (String idStr : historyConfig.getConfigurationSection("namehistory." + name).getKeys(false)) {
                    String path = "namehistory." + name + "." + idStr;
                    
                    Punishment punishment = new Punishment(
                        null,
                        historyConfig.getString(path + ".playerName"),
                        Punishment.PunishmentType.valueOf(historyConfig.getString(path + ".type")),
                        historyConfig.getString(path + ".reason"),
                        historyConfig.getString(path + ".punisher"),
                        historyConfig.getLong(path + ".startTime"),
                        historyConfig.getLong(path + ".endTime")
                    );
                    
                    punishments.add(punishment);
                }
                
                nameHistory.put(name.toLowerCase(), punishments);
            }
        }
    }
    
    public void saveAll() {
        for (Map.Entry<UUID, List<Punishment>> entry : playerHistory.entrySet()) {
            int id = 0;
            for (Punishment p : entry.getValue()) {
                String path = "history." + entry.getKey().toString() + "." + id;
                historyConfig.set(path + ".playerName", p.getPlayerName());
                historyConfig.set(path + ".type", p.getType().name());
                historyConfig.set(path + ".reason", p.getReason());
                historyConfig.set(path + ".punisher", p.getPunisherName());
                historyConfig.set(path + ".startTime", p.getStartTime());
                historyConfig.set(path + ".endTime", p.getEndTime());
                id++;
            }
        }
        
        try {
            historyConfig.save(historyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void addHistory(Punishment punishment) {
        UUID uuid = punishment.getPlayerUUID();
        
        // If UUID is null, use name-based history
        if (uuid == null) {
            String name = punishment.getPlayerName().toLowerCase();
            nameHistory.computeIfAbsent(name, k -> new ArrayList<>()).add(punishment);
            
            int id = nameHistory.get(name).size() - 1;
            String path = "namehistory." + name + "." + id;
            historyConfig.set(path + ".playerName", punishment.getPlayerName());
            historyConfig.set(path + ".type", punishment.getType().name());
            historyConfig.set(path + ".reason", punishment.getReason());
            historyConfig.set(path + ".punisher", punishment.getPunisherName());
            historyConfig.set(path + ".startTime", punishment.getStartTime());
            historyConfig.set(path + ".endTime", punishment.getEndTime());
        } else {
            playerHistory.computeIfAbsent(uuid, k -> new ArrayList<>()).add(punishment);
            
            int id = playerHistory.get(uuid).size() - 1;
            String path = "history." + uuid.toString() + "." + id;
            historyConfig.set(path + ".playerName", punishment.getPlayerName());
            historyConfig.set(path + ".type", punishment.getType().name());
            historyConfig.set(path + ".reason", punishment.getReason());
            historyConfig.set(path + ".punisher", punishment.getPunisherName());
            historyConfig.set(path + ".startTime", punishment.getStartTime());
            historyConfig.set(path + ".endTime", punishment.getEndTime());
        }
        
        try {
            historyConfig.save(historyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<Punishment> getHistory(UUID uuid) {
        return playerHistory.getOrDefault(uuid, new ArrayList<>());
    }
    
    public List<Punishment> getHistoryByName(String name) {
        return nameHistory.getOrDefault(name.toLowerCase(), new ArrayList<>());
    }
    
    public List<Punishment> getAllHistory(UUID uuid, String name) {
        List<Punishment> all = new ArrayList<>();
        if (uuid != null) {
            all.addAll(getHistory(uuid));
        }
        all.addAll(getHistoryByName(name));
        return all;
    }

    public int getWarningCount(UUID uuid) {
        return (int) getHistory(uuid).stream()
            .filter(p -> p.getType() == Punishment.PunishmentType.WARN)
            .count();
    }
    
    public int getTotalPunishments(UUID uuid) {
        return getHistory(uuid).size();
    }
    
    public long getLastPunishmentTime(UUID uuid) {
        List<Punishment> history = getHistory(uuid);
        if (history.isEmpty()) {
            return 0;
        }
        return history.stream()
            .mapToLong(Punishment::getStartTime)
            .max()
            .orElse(0);
    }
    
    public void clearHistory(UUID uuid) {
        playerHistory.remove(uuid);
        historyConfig.set("history." + uuid.toString(), null);
        try {
            historyConfig.save(historyFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
