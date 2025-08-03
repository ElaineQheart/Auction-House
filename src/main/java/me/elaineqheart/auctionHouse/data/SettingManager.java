package me.elaineqheart.auctionHouse.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;

public class SettingManager {

    public static String currencySymbol;
    public static double taxRate;
    public static long auctionDuration; // in seconds, default is 48 hours
    public static long auctionSetupTime;
    public static String fillerItem;
    public static String formatNumbersComma;
    public static String formatNumbersDot;
    public static int defaultMaxAuctions;
    public static boolean soldMessageEnabled;
    public static String formatTimeCharacters;

    static {
        loadData();
    }

    public static void loadData() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        currencySymbol = c.getString("currency", " coins");
        taxRate = c.getDouble("tax", 0.01);
        auctionDuration = c.getLong("auction-duration", 60*60*48);
        auctionSetupTime = c.getLong("auction-setup-time", 30);
        fillerItem = c.getString("filler-item", "BLACK_STAINED_GLASS_PANE");
        defaultMaxAuctions = c.getInt("default-max-auctions", 10);
        soldMessageEnabled = c.getBoolean("sold-message", true);
        String format = c.getString("format-numbers", "#,###.##");
        formatNumbersComma = String.valueOf(format.charAt(1));
        formatNumbersDot = String.valueOf(format.charAt(5));
        formatTimeCharacters = c.getString("format-time-characters", "dhms");
    }

}
