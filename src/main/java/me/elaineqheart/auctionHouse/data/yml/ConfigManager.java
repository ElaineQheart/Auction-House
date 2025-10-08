package me.elaineqheart.auctionHouse.data.yml;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigManager {

    public static Config displays = new Config();
    public static Config bannedPlayers = new Config();
    public static Config permissions = new Config();
    public static Config blacklist = new Config();

    public static void setupConfigs() {
        displays.setup("displays", false);
        displaysBackwardsCompatibility();
        bannedPlayers.setup("bannedPlayers", false);
        permissions.setup("permissions", true);
        blacklist.setup("blacklist", false);
        permissions();
    }

    public static void reloadConfigs() {
        displays.reload();
        bannedPlayers.reload();
        permissions.reload();
        blacklist.reload();
    }


    private static void permissions() {
        if(permissions.get().getConfigurationSection("auction-slots") == null) {
            permissions.get().createSection("auction-slots");
            permissions.save();
        }
        if(permissions.get().getConfigurationSection("auction-duration") == null) {
            permissions.get().createSection("auction-duration");
            permissions.save();
        }
    }

    private static void displaysBackwardsCompatibility() {
        Set<Integer> oldSet = null;
        FileConfiguration customFile = displays.get();
        try {
            // This method is for backwards compatibility
            oldSet = customFile.getKeys(false).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        } catch (NumberFormatException ignored) {}

        //This section of code is needed, even without backwards compatibility
        if (customFile.getConfigurationSection("displays") == null) {
            customFile.createSection("displays");
        }

        if(oldSet != null) {
            for (Integer displayID : oldSet) {
                Objects.requireNonNull(customFile.getConfigurationSection("displays")).set(String.valueOf(displayID), customFile.get(String.valueOf(displayID)));
                customFile.set(String.valueOf(displayID), null); // Remove the old key
            }
        }
        displays.save();
    }

}
