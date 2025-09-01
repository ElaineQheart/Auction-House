package me.elaineqheart.auctionHouse.data;

public class ConfigManager {

    public static Config Displays = new Config();
    public static Config BannedPlayers = new Config();
    public static Config Permissions = new Config();

    public static void setupConfigs() {
        Displays.setup("displays", false);
        BannedPlayers.setup("bannedPlayers", false);
        Permissions.setup("permissions", true);
    }

}
