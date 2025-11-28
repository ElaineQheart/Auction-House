package me.elaineqheart.auctionHouse.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.yml.PlayerPreferencesManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AuctionHousePlaceholders extends PlaceholderExpansion {

    private final AuctionHouse plugin;

    public AuctionHousePlaceholders(AuctionHouse plugin) {
        this.plugin = plugin;
    }

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
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // This keeps the expansion registered on PlaceholderAPI reload
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        // %auctionhouse_announcements% - returns true/false
        if (params.equalsIgnoreCase("announcements")) {
            return String.valueOf(PlayerPreferencesManager.hasAnnouncementsEnabled(player));
        }

        // %auctionhouse_announcements_status% - returns Enabled/Disabled (configurable text)
        if (params.equalsIgnoreCase("announcements_status")) {
            return PlayerPreferencesManager.getAnnouncementStatus(player);
        }

        return null;
    }
}
