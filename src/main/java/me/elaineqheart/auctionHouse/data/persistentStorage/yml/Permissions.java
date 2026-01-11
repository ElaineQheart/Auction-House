package me.elaineqheart.auctionHouse.data.persistentStorage.yml;

import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Permissions {

    public static int getAuctionSlots(Player player) {
        int slots = SettingManager.defaultMaxAuctions;
        ConfigurationSection section = ConfigManager.permissions.get().getConfigurationSection("auction-slots");
        if (section == null) return slots;
        for (String key : section.getKeys(true)) {
            if (player.hasPermission(key)) {
                int newSlots = section.getInt(key, slots);
                if(newSlots > slots) slots = newSlots;
            }
        }
        return slots;
    }

    public static long getAuctionDuration(Player player, boolean BIN) {
        long duration = BIN ? SettingManager.BINAuctionDuration : SettingManager.BIDAuctionDuration;
        ConfigurationSection section = ConfigManager.permissions.get().getConfigurationSection(BIN ? "bin-auction-duration" : "bid-auction-duration");
        if (section == null) return duration;
        for (String key : section.getKeys(true)) {
            if (player.hasPermission(key)) {
                long newDuration = section.getLong(key, duration);
                if(newDuration > duration) duration = newDuration;
            }
        }
        return duration;
    }

}
