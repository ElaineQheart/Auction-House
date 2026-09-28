package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// The durations (and their tax) a player can choose from in the sell menu. Configured in config.yml (sell-durations).
public class SellOptions {

    public record Tier(long seconds, double tax) {}

    public static List<Tier> getTiers(Player p, boolean bid) {
        long max = ConfigManager.permissions.getAuctionDuration(p, bid);
        List<Tier> tiers = new ArrayList<>();
        List<Map<?, ?>> configured = AuctionHouse.getInstance().getConfig().getMapList("sell-durations." + (bid ? "bid" : "bin"));
        for (Map<?, ?> entry : configured) {
            Object time = entry.get("time");
            Object tax = entry.get("tax");
            if (!(time instanceof Number)) continue;
            long seconds = ((Number) time).longValue();
            double taxRate = tax instanceof Number ? ((Number) tax).doubleValue() : SettingManager.taxRate;
            if (seconds <= 0 || seconds > max) continue;
            if (taxRate < 0 || taxRate >= 1) continue;
            tiers.add(new Tier(seconds, taxRate));
        }
        if (tiers.isEmpty()) tiers.add(new Tier(max, SettingManager.taxRate));
        return tiers;
    }

    // the tier closest to the normal duration of the server (config.yml: bin-auction-duration / bid-auction-duration)
    public static int defaultIndex(List<Tier> tiers, boolean bid) {
        long target = bid ? SettingManager.BIDAuctionDuration : SettingManager.BINAuctionDuration;
        int best = 0;
        for (int i = 1; i < tiers.size(); i++) {
            if (Math.abs(tiers.get(i).seconds() - target) < Math.abs(tiers.get(best).seconds() - target)) best = i;
        }
        return best;
    }

    public static String formatTax(double tax) {
        double percent = Math.round(tax * 10000) / 100.0;
        if (percent % 1 == 0) return (long) percent + "%";
        return percent + "%";
    }

    // what the seller receives; same formula as when collecting the coins
    public static double profit(double price, double tax) {
        return Math.floor(price * 100 * (1 - tax)) / 100;
    }

}
