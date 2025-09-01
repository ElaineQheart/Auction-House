package me.elaineqheart.auctionHouse.data.yml;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;

public class SettingManager {

    public static String currencySymbol;
    public static double taxRate;
    public static long auctionDuration; // in seconds, default is 48 hours
    public static long auctionSetupTime;
    public static String fillerItem;
    public static DecimalFormat formatter;
    public static int defaultMaxAuctions;
    public static boolean soldMessageEnabled;
    public static String formatTimeCharacters;
    public static String permissionModerate;

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
        String formatNumbers = c.getString("format-numbers", "#,###.##");
        formatter = new DecimalFormat(formatNumbers);
        formatTimeCharacters = c.getString("format-time-characters", "dhms");
        permissionModerate = c.getString("admin-permission", "auctionhouse.moderator");
    }

}
