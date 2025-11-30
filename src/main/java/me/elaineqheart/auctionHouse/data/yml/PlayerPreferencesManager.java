package me.elaineqheart.auctionHouse.data.yml;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerPreferencesManager {

    private static File file;
    private static FileConfiguration config;

    public static void setup() {
        file = new File(AuctionHouse.getPlugin().getDataFolder(), "playerPreferences.yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                AuctionHouse.getPlugin().getLogger().severe("Could not create playerPreferences.yml");
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            AuctionHouse.getPlugin().getLogger().severe("Could not save playerPreferences.yml");
        }
    }

    public static void reload() {
        config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Check if a player has auction announcements enabled
     * @param player The player to check
     * @return true if announcements are enabled (default is true)
     */
    public static boolean hasAnnouncementsEnabled(Player player) {
        return hasAnnouncementsEnabled(player.getUniqueId());
    }

    /**
     * Check if a player has auction announcements enabled by UUID
     * @param uuid The UUID of the player
     * @return true if announcements are enabled (default is true)
     */
    public static boolean hasAnnouncementsEnabled(UUID uuid) {
        return config.getBoolean("players." + uuid.toString() + ".announcements", true);
    }

    /**
     * Set whether a player wants to receive auction announcements
     * @param player The player
     * @param enabled true to enable, false to disable
     */
    public static void setAnnouncementsEnabled(Player player, boolean enabled) {
        setAnnouncementsEnabled(player.getUniqueId(), enabled);
    }

    /**
     * Set whether a player wants to receive auction announcements by UUID
     * @param uuid The UUID of the player
     * @param enabled true to enable, false to disable
     */
    public static void setAnnouncementsEnabled(UUID uuid, boolean enabled) {
        config.set("players." + uuid.toString() + ".announcements", enabled);
        save();
    }

    /**
     * Toggle auction announcements for a player
     * @param player The player
     * @return the new state (true if now enabled, false if now disabled)
     */
    public static boolean toggleAnnouncements(Player player) {
        boolean current = hasAnnouncementsEnabled(player);
        boolean newState = !current;
        setAnnouncementsEnabled(player, newState);
        return newState;
    }

    /**
     * Get the announcement status as a string for placeholders
     * @param player The player
     * @return "enabled" or "disabled" based on player's preference
     */
    public static String getAnnouncementStatus(Player player) {
        return hasAnnouncementsEnabled(player) ? 
            Messages.getFormatted("placeholders.announcements-enabled") : 
            Messages.getFormatted("placeholders.announcements-disabled");
    }
}
