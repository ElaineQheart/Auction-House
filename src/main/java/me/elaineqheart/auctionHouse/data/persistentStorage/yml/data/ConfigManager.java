package me.elaineqheart.auctionHouse.data.persistentStorage.yml.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.OldLayout;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigManager {

    public static Config displays = new Config();
    public static Config bannedPlayers = new Config();
    public static Config permissions = new Config();
    public static Config blacklist = new Config();
    public static Config categories = new Config();
    public static Config playerPreferences = new Config();
    public static Config layout = new Config();

    public static void setupConfigs() {
        AuctionHouse.getPlugin().reloadConfig();
        displays.setup("displays", false, "/data");
        displaysBackwardsCompatibility();
        bannedPlayers.setup("bannedPlayers", false, "/data");
        permissions.setup("permissions", true, "");
        blacklist.setup("blacklist", false, "/data");
        categories.setup("categories", false, "/data");
        playerPreferences.setup("playerPreferences", false, "/data");
        //compatibility to version 1.21.4
        if(!oldVersion21()) {
            layout.setup("layout", true, "");
        } else {
            layout.setup("layout", false, "");

            if (!layout.get().getBoolean("old-layout")) OldLayout.saveOldLayout();
        }
        permissionsSetup();
    }

    public static boolean backwardsCompatibility() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        return !Objects.equals(c.getString("plugin-version"), AuctionHouse.getPlugin().getDescription().getVersion());
    }

    public static void reloadConfigs() {
        displays.reload();
        bannedPlayers.reload();
        permissions.reload();
        blacklist.reload();
        categories.reload();
        playerPreferences.reload();
        layout.reload();
    }


    private static void permissionsSetup() {
        if(permissions.get().getConfigurationSection("auction-slots") == null) {
            permissions.get().createSection("auction-slots");
            permissions.save();
        }
        if(permissions.get().getConfigurationSection("bin-auction-duration") == null) {
            permissions.get().createSection("bin-auction-duration");
            permissions.save();
        }
        if(permissions.get().getConfigurationSection("bid-auction-duration") == null) {
            permissions.get().createSection("bid-auction-duration");
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

    public static boolean oldVersion21() {
        String version = Bukkit.getServer().getVersion();
        List<String> oldVersions = List.of("1.21.4", "1.21.3", "1.21.2", "1.21.1");
        for(String oldVersion : oldVersions) {
            if(version.contains(oldVersion)) return true;
        }
        return false;
    }

}
