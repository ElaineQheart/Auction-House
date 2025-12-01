package me.elaineqheart.auctionHouse.data.yml;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;

public class SettingManager {

    public static double taxRate;
    public static long auctionDuration; // in seconds, default is 48 hours
    public static long auctionSetupTime;
    public static String fillerItem;
    public static String formatNumbers;
    public static DecimalFormat formatter;
    public static int defaultMaxAuctions;
    public static boolean soldMessageEnabled;
    public static String formatTimeCharacters;
    public static String permissionModerate;
    public static boolean partialSelling;
    public static boolean useRedis;
    public static String redisHost;
    public static String redisUsername;
    public static String redisPassword;
    public static int redisPort;
    public static int displayUpdateTicks;
    public static boolean autoCollect;
    public static boolean auctionAnnouncementsEnabled;

    static {
        loadData();
    }

    public static void loadData() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        taxRate = c.getDouble("tax", 0.01);
        auctionDuration = c.getLong("auction-duration", 60*60*48);
        auctionSetupTime = c.getLong("auction-setup-time", 30);
        fillerItem = c.getString("filler-item", "BLACK_STAINED_GLASS_PANE");
        defaultMaxAuctions = c.getInt("default-max-auctions", 10);
        soldMessageEnabled = c.getBoolean("sold-message", true);
        formatNumbers = c.getString("format-numbers", "#,###.##");
        formatter = new DecimalFormat(formatNumbers);
        formatTimeCharacters = c.getString("format-time-characters", "dhms");
        permissionModerate = c.getString("admin-permission", "auctionhouse.moderator");
        partialSelling = c.getBoolean("partial-selling", false);
//        useRedis = c.getBoolean("multi-server-database.redis", false);
//        redisHost = c.getString("multi-server-database.redis-host", "");
//        redisUsername = c.getString("multi-server-database.redis-username", "default");
//        redisPassword = c.getString("multi-server-database.redis-password", "");
//        redisPort = c.getInt("multi-server-database.redis-port", 0);
        displayUpdateTicks = c.getInt("display-update", 80);
        autoCollect = c.getBoolean("auto-collect", false);
        auctionAnnouncementsEnabled = c.getBoolean("auction-announcements", true);
    }

//    multi-server-database:
//    redis: false                              # if Redis as a database should be used. Needed for multiserver support
//    redis-host: ""                            # this is the host/link that points to your database, something like "redis-xxxxx.cXXX.eu-central-1-1.ec2.redns.redis-cloud.com"
//    redis-username: "default"                 # usually it's just "default"
//    redis-password: ""
//    redis-port:                               # the port is the last thing in your public endpoint

    public static void backwardsCompatibility() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        if(c.getString("currency") != null) {
            FileConfiguration messageFile = Messages.get();
            messageFile.set("placeholders.currency-symbol", c.getString("currency"));
            Messages.save();
            Messages.reload();
            c.set("currency", null);
            c.set("currency-symbol", "has been moved to messages.yml");
            AuctionHouse.getPlugin().saveConfig();
        }
        if(c.get("currency-before-number") != null) {
            FileConfiguration messageFile = Messages.get();
            messageFile.set("placeholders.price", "%currency-symbol%%number%");
            Messages.save();
            Messages.reload();
            c.set("currency-before-number", null);
            AuctionHouse.getPlugin().saveConfig();
        }
    }

}
