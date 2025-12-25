package me.elaineqheart.auctionHouse.data.persistentStorage.yml;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

public class SettingManager {

    public static double taxRate;
    public static long auctionDuration; // in seconds, default is 48 hours
    public static long auctionSetupTime;
    public static String fillerItem;
    public static DecimalFormat formatter;
    public static String formatTimeCharacters;
    public static int defaultMaxAuctions;
    public static boolean soldMessageEnabled;
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
    public static List<String> ahLayout;
    public static List<String> myAhLayout;

    static {
        loadData();
    }

    public static void loadData() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        FileConfiguration l = ConfigManager.layout.get();
        taxRate = c.getDouble("tax", 0.01);
        auctionDuration = c.getLong("auction-duration", 60*60*48);
        auctionSetupTime = c.getLong("auction-setup-time", 30);
        fillerItem = c.getString("filler-item", "BLACK_STAINED_GLASS_PANE");
        defaultMaxAuctions = c.getInt("default-max-auctions", 10);
        soldMessageEnabled = c.getBoolean("sold-message", true);
        formatter = new DecimalFormat(Messages.getFormatted("placeholders.format-numbers"));
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
        ahLayout = l.getStringList("ah-layout");
        myAhLayout = l.getStringList("my-ah-layout");
        if(ahLayout.isEmpty() || myAhLayout.isEmpty()) updateLayout(l);
    }

//    multi-server-database:
//    redis: false                              # if Redis as a database should be used. Needed for multiserver support
//    redis-host: ""                            # this is the host/link that points to your database, something like "redis-xxxxx.cXXX.eu-central-1-1.ec2.redns.redis-cloud.com"
//    redis-username: "default"                 # usually it's just "default"
//    redis-password: ""
//    redis-port:                               # the port is the last thing in your public endpoint

    public static void backwardsCompatibility() {
        boolean reload = false;
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        FileConfiguration messageFile = Messages.get();
        if(c.getString("currency") != null) {
            messageFile.set("placeholders.currency-symbol", c.getString("currency"));
            c.set("currency", null);
            c.set("currency-symbol", "has been moved to messages.yml");
            reload = true;
        }
        if(c.get("currency-before-number") != null) {
            messageFile.set("placeholders.price", "%currency-symbol%%number%");
            c.set("currency-before-number", null);
            reload = true;
        }
        if(c.get("format-numbers") != null) {
            messageFile.set("placeholders.format-numbers", c.getString("format-numbers"));
            c.set("format-numbers", null);
            reload = true;
        }
        if(c.get("format-time-characters") != null) {
            messageFile.set("placeholders.format-time-characters", c.getString("format-time-characters"));
            c.set("format-time-characters", null);
            reload = true;
        }
        if(reload) {
            Messages.save();
            Messages.reload();
            AuctionHouse.getPlugin().saveConfig();
        }
    }

    private static void updateLayout(FileConfiguration l) {
        l.set("ah-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "s o # p r n # # m"));
        l.set("my-ah-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "b o # p r n # # i"));
        ConfigManager.layout.save();
        ahLayout = l.getStringList("ah-layout");
        myAhLayout = l.getStringList("my-ah-layout");
    }

}
