package me.elaineqheart.auctionHouse.data.persistentStorage.yml;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Objects;

public class SettingManager {

    public static double taxRate;
    public static long auctionSetupTime;
    public static DecimalFormat formatter;
    public static String formatTimeCharacters;
    public static int defaultMaxAuctions;
    public static boolean soldMessageEnabled;
    public static String permissionModerate;
    public static boolean partialSelling;
    //public static boolean useRedis;
    //public static String redisHost;
    //public static String redisUsername;
    //public static String redisPassword;
    //public static int redisPort;
    public static int displayUpdateTicks;
    public static boolean autoCollect;
    public static boolean auctionAnnouncementsEnabled;
    public static boolean BINAuctions;
    public static long BINAuctionDuration; // in seconds, default is 48 hours
    public static boolean BIDAuctions;
    public static long BIDAuctionDuration;
    public static int lastBIDExtraTime;
    public static double bidIncreaseRatio;
    public static double minBINPrice;
    public static double minBIDPrice;
    public static String soundClick;
    public static String soundOpenEnderchest;
    public static String soundCloseEnderchest;
    public static String soundBreakWood;
    public static String soundExperience;
    public static String soundVillagerDeny;
    public static String soundOpenShulker;
    public static String soundCloseShulker;

    static {
        loadData();
    }

    public static void loadData() {
        AuctionHouse.getPlugin().reloadConfig();
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        taxRate = c.getDouble("tax", 0.01);
        auctionSetupTime = c.getLong("auction-setup-time", 30);
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
        BINAuctions = c.getBoolean("bin-auctions", true);
        BINAuctionDuration = c.getLong("bin-auction-duration", 172800);
        BIDAuctions = c.getBoolean("bid-auctions", true);
        BIDAuctionDuration = c.getLong("bid-auction-duration", 7200);
        lastBIDExtraTime = c.getInt("last-bid-extra-time", 60);
        bidIncreaseRatio = c.getDouble("bid-increase-percent", 25) / 100;
        minBINPrice = c.getDouble("min-bin", 1);
        minBIDPrice = c.getDouble("min-bid", 1);
        FileConfiguration layout = ConfigManager.layout.get();
        soundClick = layout.getString("sounds.click", "UI_STONECUTTER_SELECT_RECIPE");
        soundOpenEnderchest = layout.getString("sounds.open-enderchest", "BLOCK_ENDER_CHEST_OPEN");
        soundCloseEnderchest = layout.getString("sounds.close-enderchest", "BLOCK_ENDER_CHEST_CLOSE");
        soundBreakWood = layout.getString("sounds.break-wood", "BLOCK_WOOD_BREAK");
        soundExperience = layout.getString("sounds.experience", "ENTITY_EXPERIENCE_ORB_PICKUP");
        soundVillagerDeny = layout.getString("sounds.villager-deny", "ENTITY_VILLAGER_NO");
        soundOpenShulker = layout.getString("sounds.open-shulker", "BLOCK_SHULKER_BOX_OPEN");
        soundCloseShulker = layout.getString("sounds.close-shulker", "BLOCK_SHULKER_BOX_CLOSE");
        if(ConfigManager.backwardsCompatibility()) backwardsCompatibility();
    }

//    multi-server-database:
//    redis: false                              # if Redis as a database should be used. Needed for multiserver support
//    redis-host: ""                            # this is the host/link that points to your database, something like "redis-xxxxx.cXXX.eu-central-1-1.ec2.redns.redis-cloud.com"
//    redis-username: "default"                 # usually it's just "default"
//    redis-password: ""
//    redis-port:                               # the port is the last thing in your public endpoint

    private static void backwardsCompatibility() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        c.set("plugin-version", AuctionHouse.getPlugin().getDescription().getVersion());
        FileConfiguration messageFile = Messages.get();
        if(c.contains("currency")) {
            messageFile.set("placeholders.currency-symbol", c.getString("currency"));
            c.set("currency", null);
            c.set("currency-symbol", "has been moved to messages.yml");
        }
        if(c.contains("currency-before-number")) {
            messageFile.set("placeholders.price", "%currency-symbol%%number%");
            c.set("currency-before-number", null);
        }
        if(c.contains("format-numbers")) {
            messageFile.set("placeholders.format-numbers", c.getString("format-numbers"));
            c.set("format-numbers", null);
        }
        if(c.contains("format-time-characters")) {
            messageFile.set("placeholders.format-time-characters", c.getString("format-time-characters"));
            c.set("format-time-characters", null);
        }
        if (c.contains("filler-item")) {
            Material material = Material.matchMaterial(c.getString("filler-item", "BLACK_STAINED_GLASS_PANE"));
            ItemStack fillerItem = material == null ? new ItemStack(Material.AIR) : new ItemStack(material);
            ConfigManager.layout.get().set("filler-item", fillerItem);
            ConfigManager.layout.save();
            ConfigManager.layout.reload();
            c.set("filler-item", null);
        }
        if (c.contains("auction-duration")) {
            c.set("bin-auction-duration", c.get("auction-duration"));
            c.set("auction-duration", null);
        }
        if (ConfigManager.permissions.get().contains("auction-duration")) {
            ConfigManager.permissions.get().set("bin-auction-duration", ConfigManager.permissions.get().get("auction-duration"));
            ConfigManager.permissions.get().set("auction-duration", null);
            ConfigManager.permissions.save();
            ConfigManager.permissions.reload();
        }
        if (Objects.equals(messageFile.getString("placeholders.currency-symbol"), " §ecoins")) {
            messageFile.set("placeholders.currency-symbol", " coins");
        }
        Messages.save();
        Messages.reload();
        AuctionHouse.getPlugin().saveConfig();
        AuctionHouse.getPlugin().reloadConfig();
    }


}
