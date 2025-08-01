package me.elaineqheart.auctionHouse.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingManager {

    public static String currencySymbol;
    public static double taxRate;
    public static long auctionDuration; // in seconds, default is 48 hours
    public static long auctionWaitingTime;
    public static String fillerItem;
    public static String formatNumbersComma;
    public static String formatNumbersDot;

    static {
        loadData();
    }

    public static void loadData() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        currencySymbol = c.getString("currency", " coins");
        taxRate = c.getDouble("tax", 0.01);
        auctionDuration = c.getLong("auction-duration", 60*60*48);
        auctionWaitingTime = c.getLong("auction-waiting-time", 30);
        fillerItem = c.getString("filler-item", "BLACK_STAINED_GLASS_PANE");
        String format = c.getString("format-numbers", "#,###.##");
        formatNumbersComma = String.valueOf(format.charAt(1));
        formatNumbersDot = String.valueOf(format.charAt(5));
    }

}
