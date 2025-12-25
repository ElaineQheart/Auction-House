package me.elaineqheart.auctionHouse.data.persistentStorage.yml;

import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerPreferencesManager extends ConfigManager{

    public static boolean hasAnnouncementsEnabled(UUID uuid) {
        return playerPreferences.get().getBoolean("players." + uuid.toString() + ".announcements", true);
    }
    public static void setAnnouncementsEnabled(UUID uuid, boolean enabled) {
        playerPreferences.get().set("players." + uuid.toString() + ".announcements", enabled);
        playerPreferences.save();
    }
    public static boolean toggleAnnouncements(Player player) {
        boolean current = hasAnnouncementsEnabled(player.getUniqueId());
        setAnnouncementsEnabled(player.getUniqueId(), !current);
        return !current;
    }

}
