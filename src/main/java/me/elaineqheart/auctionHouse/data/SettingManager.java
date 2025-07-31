package me.elaineqheart.auctionHouse.data;

import me.elaineqheart.auctionHouse.AuctionHouse;

public class SettingManager {

    public static String currencySymbol;
    public static double taxRate;
    public static long auctionDuration; // in seconds, default is 48 hours
    public static long auctionWaitingTime;

    static {
        loadData();
    }

    public static void loadData() {
        currencySymbol = AuctionHouse.getPlugin().getConfig().getString("currency", " coins");
        taxRate = AuctionHouse.getPlugin().getConfig().getDouble("tax", 0.01);
        auctionDuration = AuctionHouse.getPlugin().getConfig().getLong("auction-duration", 60*60*48);
        auctionWaitingTime = AuctionHouse.getPlugin().getConfig().getLong("auction-waiting-time", 30);
    }

}
