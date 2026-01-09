package de.nexusban.data;

import java.util.UUID;

public class Punishment {
    
    public enum PunishmentType {
        BAN, TEMPBAN, MUTE, TEMPMUTE, KICK, WARN, IPBAN
    }
    
    private final UUID playerUUID;
    private final String playerName;
    private final PunishmentType type;
    private final String reason;
    private final String punisherName;
    private final long startTime;
    private final long endTime; // -1 for permanent
    private boolean active;
    
    public Punishment(UUID playerUUID, String playerName, PunishmentType type, 
                      String reason, String punisherName, long startTime, long endTime) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.type = type;
        this.reason = reason;
        this.punisherName = punisherName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.active = true;
    }
    
    public UUID getPlayerUUID() {
        return playerUUID;
    }
    
    public String getPlayerName() {
        return playerName;
    }
    
    public PunishmentType getType() {
        return type;
    }
    
    public String getReason() {
        return reason;
    }
    
    public String getPunisherName() {
        return punisherName;
    }
    
    public long getStartTime() {
        return startTime;
    }
    
    public long getEndTime() {
        return endTime;
    }
    
    public boolean isPermanent() {
        return endTime == -1;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isExpired() {
        if (isPermanent()) return false;
        return System.currentTimeMillis() > endTime;
    }
    
    public long getRemainingTime() {
        if (isPermanent()) return -1;
        return Math.max(0, endTime - System.currentTimeMillis());
    }
}
