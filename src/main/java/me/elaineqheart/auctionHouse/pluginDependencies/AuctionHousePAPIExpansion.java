package me.elaineqheart.auctionHouse.pluginDependencies;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class AuctionHousePAPIExpansion extends PlaceholderExpansion {

    @Override
    public @NotNull String getIdentifier() {
        return "auctionhouse";
    }

    @Override
    public @NotNull String getAuthor() {
        return "ElaineQheart";
    }

    @Override
    public @NotNull String getVersion() {
        return AuctionHouse.getPlugin().getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // The expansion continues even if PAPI is recharged
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        // %auctionhouse_announcements% > "enabled" or "disabled"
        if (params.equals("announcements")) {
            return ConfigManager.playerPreferences.hasAnnouncementsEnabled(player.getUniqueId())
                    ? "enabled"
                    : "disabled";
        }

        // %auctionhouse_active_auctions% > number of active auctions for the player
        if (params.equals("active_auctions")) {
            return String.valueOf(AuctionHouseStorage.getNumberOfAuctions(player.getUniqueId()));
        }

        return null; // Unrecognized placeholder
    }
}