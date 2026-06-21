package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Displays extends Config {

    public void addDisplay(int id, Location loc) {
        getYmlData().set(String.valueOf(id), loc);
        save();
    }

    public void removeDisplay(int id) {
        getYmlData().set(String.valueOf(id), null);
        save();
    }

    public ConfigurationSection getYmlData() {
        ConfigurationSection ymlData = getCustomFile().getConfigurationSection("displays");
        if (ymlData != null) return ymlData;
        getCustomFile().createSection("displays");
        save();
        return getCustomFile().getConfigurationSection("displays");
    }

    public void backwardsCompatibility() {
        Set<Integer> oldSet = null;
        FileConfiguration customFile = getCustomFile();
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
        save();
    }

}
