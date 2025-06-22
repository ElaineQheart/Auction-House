package me.elaineqheart.auctionHouse.ah;

import me.elaineqheart.auctionHouse.AuctionHouse;

public class SettingManager {

    public static String currencySymbol;

    public static void loadData() {
        currencySymbol = AuctionHouse.getPlugin().getConfig().getString("currency");
    }

}
