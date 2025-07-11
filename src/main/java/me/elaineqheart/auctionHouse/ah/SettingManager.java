package me.elaineqheart.auctionHouse.ah;

import me.elaineqheart.auctionHouse.AuctionHouse;

public class SettingManager {

    public static String currencySymbol;
    public static double taxRate;

    static {
        loadData();
    }

    public static void loadData() {
        currencySymbol = AuctionHouse.getPlugin().getConfig().getString("currency");
        taxRate = AuctionHouse.getPlugin().getConfig().getDouble("tax");
    }

}
