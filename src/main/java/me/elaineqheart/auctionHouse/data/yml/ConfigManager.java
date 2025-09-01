package me.elaineqheart.auctionHouse.data.yml;

public class ConfigManager {

    public static Config displays = new Config();
    public static Config bannedPlayers = new Config();
    public static Config permissions = new Config();

    public static void setupConfigs() {
        displays.setup("displays", false);
        bannedPlayers.setup("bannedPlayers", false);
        permissions.setup("permissions", true);
    }

    public static void reloadConfigs() {
        displays.reload();
        bannedPlayers.reload();
        permissions.reload();
    }

}
