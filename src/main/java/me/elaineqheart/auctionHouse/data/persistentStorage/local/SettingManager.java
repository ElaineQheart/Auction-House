package me.elaineqheart.auctionHouse.data.persistentStorage.local;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SettingManager {

    public static double taxRate;
    public static long auctionSetupTime;
    public static DecimalFormat formatter;
    public static String formatTimeCharacters;
    public static int defaultMaxAuctions;
    public static boolean soldMessageEnabled;
    public static String permissionModerate;
    public static boolean partialSelling;
    // public static boolean useRedis;
    // public static String redisHost;
    // public static String redisUsername;
    // public static String redisPassword;
    // public static int redisPort;
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
    public static String soundNPCClick;

    // displays
    public static final Set<Material> displayMaterials = new HashSet<>();
    public static final Map<String, BlockData> displayGlassMap = new HashMap<>();
    public static final Map<String, BlockData> displayBaseMap = new HashMap<>();
    public static final Map<String, BlockData> displaySignMap = new HashMap<>();

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
        formatter = new DecimalFormat(M.getFormatted("placeholders.format-numbers"));
        formatTimeCharacters = c.getString("format-time-characters", "dhms");
        permissionModerate = c.getString("admin-permission", "auctionhouse.moderator");
        partialSelling = c.getBoolean("partial-selling", false);
        // useRedis = c.getBoolean("multi-server-database.redis", false);
        // redisHost = c.getString("multi-server-database.redis-host", "");
        // redisUsername = c.getString("multi-server-database.redis-username",
        // "default");
        // redisPassword = c.getString("multi-server-database.redis-password", "");
        // redisPort = c.getInt("multi-server-database.redis-port", 0);
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
        FileConfiguration layout = ConfigManager.layout.getCustomFile();
        soundClick = layout.getString("sounds.click", "UI_STONECUTTER_SELECT_RECIPE");
        soundOpenEnderchest = layout.getString("sounds.open-enderchest", "BLOCK_ENDER_CHEST_OPEN");
        soundCloseEnderchest = layout.getString("sounds.close-enderchest", "BLOCK_ENDER_CHEST_CLOSE");
        soundBreakWood = layout.getString("sounds.break-wood", "BLOCK_WOOD_BREAK");
        soundExperience = layout.getString("sounds.experience", "ENTITY_EXPERIENCE_ORB_PICKUP");
        soundVillagerDeny = layout.getString("sounds.villager-deny", "ENTITY_VILLAGER_NO");
        soundOpenShulker = layout.getString("sounds.open-shulker", "BLOCK_SHULKER_BOX_OPEN");
        soundCloseShulker = layout.getString("sounds.close-shulker", "BLOCK_SHULKER_BOX_CLOSE");
        soundNPCClick = layout.getString("sounds.npc-click", "UI_STONECUTTER_SELECT_RECIPE");

        loadDisplays(c);

        if (ConfigManager.backwardsCompatibility())
            backwardsCompatibility();
    }

    // multi-server-database:
    // redis: false # if Redis as a database should be used. Needed for multiserver
    // support
    // redis-host: "" # this is the host/link that points to your database,
    // something like "redis-xxxxx.cXXX.eu-central-1-1.ec2.redns.redis-cloud.com"
    // redis-username: "default" # usually it's just "default"
    // redis-password: ""
    // redis-port: # the port is the last thing in your public endpoint

    private static BlockData parseBlockData(String input, Material defaultMaterial) {
        if (input == null || input.isEmpty())
            return Bukkit.createBlockData(defaultMaterial);
        try {
            if (input.contains(":")) {
                String[] parts = input.split(":", 2);
                Material mat = Material.matchMaterial(parts[0]);
                if (mat != null && mat.isBlock()) {
                    BlockData data = Bukkit.createBlockData(mat);
                    int value = Integer.parseInt(parts[1]);
                    if (data instanceof org.bukkit.block.data.type.RespawnAnchor anchor) {
                        anchor.setCharges(Math.min(value, anchor.getMaximumCharges()));
                    } else if (data instanceof org.bukkit.block.data.Levelled levelled) {
                        levelled.setLevel(Math.min(value, levelled.getMaximumLevel()));
                    } else if (data instanceof org.bukkit.block.data.Ageable ageable) {
                        ageable.setAge(Math.min(value, ageable.getMaximumAge()));
                    } else if (data instanceof org.bukkit.block.data.Lightable lightable) {
                        lightable.setLit(value > 0);
                    }
                    return data;
                }
            }
            Material mat = Material.matchMaterial(input);
            if (mat != null && mat.isBlock()) {
                return Bukkit.createBlockData(mat);
            }
            return Bukkit.createBlockData(input);
        } catch (Exception e) {
            AuctionHouse.getPlugin().getLogger().warning("Invalid block data: " + input + ". Using default.");
            return Bukkit.createBlockData(defaultMaterial);
        }
    }

    private static void loadDisplays(FileConfiguration c) {
        displayMaterials.clear();
        displayGlassMap.clear();
        displayBaseMap.clear();
        displaySignMap.clear();

        if (c.getConfigurationSection("displays") != null) {
            for (String sortType : c.getConfigurationSection("displays").getKeys(false)) {
                for (String rankOrDef : c.getConfigurationSection("displays." + sortType).getKeys(false)) {
                    String baseKey = "displays." + sortType + "." + rankOrDef + ".";

                    BlockData glass = parseBlockData(c.getString(baseKey + "glass"), Material.GLASS);
                    displayGlassMap.put(sortType + "-" + rankOrDef, glass);

                    BlockData base = parseBlockData(c.getString(baseKey + "base"), Material.CHISELED_TUFF_BRICKS);
                    displayBaseMap.put(sortType + "-" + rankOrDef, base);
                    displayMaterials.add(base.getMaterial());

                    BlockData sign = parseBlockData(c.getString(baseKey + "sign"), Material.DARK_OAK_WALL_SIGN);
                    displaySignMap.put(sortType + "-" + rankOrDef, sign);
                    displayMaterials.add(sign.getMaterial());
                }
            }
        }
    }

    public static BlockData getDisplayGlass(String type, int rank) {
        return displayGlassMap.getOrDefault(type + "-" + rank,
                displayGlassMap.getOrDefault(type + "-default", Bukkit.createBlockData(Material.GLASS)));
    }

    public static BlockData getDisplayBase(String type, int rank) {
        return displayBaseMap.getOrDefault(type + "-" + rank,
                displayBaseMap.getOrDefault(type + "-default", Bukkit.createBlockData(Material.CHISELED_TUFF_BRICKS)));
    }

    public static BlockData getDisplaySign(String type, int rank) {
        return displaySignMap.getOrDefault(type + "-" + rank,
                displaySignMap.getOrDefault(type + "-default", Bukkit.createBlockData(Material.DARK_OAK_WALL_SIGN)));
    }

    private static void backwardsCompatibility() {
        FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        c.set("plugin-version", AuctionHouse.getPlugin().getDescription().getVersion());
        FileConfiguration messageFile = M.get();
        if (c.contains("currency")) {
            messageFile.set("placeholders.currency-symbol", c.getString("currency"));
            c.set("currency", null);
            c.set("currency-symbol", "has been moved to messages.yml");
        }
        if (c.contains("currency-before-number")) {
            messageFile.set("placeholders.price", "%currency-symbol%%number%");
            c.set("currency-before-number", null);
        }
        if (c.contains("format-numbers")) {
            messageFile.set("placeholders.format-numbers", c.getString("format-numbers"));
            c.set("format-numbers", null);
        }
        if (c.contains("format-time-characters")) {
            messageFile.set("placeholders.format-time-characters", c.getString("format-time-characters"));
            c.set("format-time-characters", null);
        }
        if (c.contains("filler-item")) {
            Material material = Material.matchMaterial(c.getString("filler-item", "BLACK_STAINED_GLASS_PANE"));
            ItemStack fillerItem = material == null ? new ItemStack(Material.AIR) : new ItemStack(material);
            ConfigManager.layout.getCustomFile().set("filler-item", fillerItem);
            ConfigManager.layout.save();
            ConfigManager.layout.reload();
            c.set("filler-item", null);
        }
        if (c.contains("auction-duration")) {
            c.set("bin-auction-duration", c.get("auction-duration"));
            c.set("auction-duration", null);
        }
        if (ConfigManager.permissions.getCustomFile().contains("auction-duration")) {
            ConfigManager.permissions.getCustomFile().set("bin-auction-duration",
                    ConfigManager.permissions.getCustomFile().get("auction-duration"));
            ConfigManager.permissions.getCustomFile().set("auction-duration", null);
            ConfigManager.permissions.save();
            ConfigManager.permissions.reload();
        }
        if (Objects.equals(messageFile.getString("placeholders.currency-symbol"), " §ecoins")) {
            messageFile.set("placeholders.currency-symbol", " coins");
        }
        if (messageFile.contains("world.displays.sign-interaction")) {
            messageFile.set("world.displays.line-3", messageFile.get("world.displays.sign-interaction"));
            messageFile.set("world.displays.sign-interaction", null);
            String by = messageFile.getString("world.displays.by-player");
            if (by != null && !by.contains("%player%")) {
                messageFile.set("world.displays.by-player", messageFile.get("world.displays.by-player") + "%player%");
            }
        }
        if (!c.contains("displays")) {
            // Write defaults directly to config
            c.set("displays.highest_price.1.glass", "GOLD_BLOCK");
            c.set("displays.highest_price.1.base", "CHISELED_TUFF_BRICKS");
            c.set("displays.highest_price.1.sign", "DARK_OAK_WALL_SIGN");
            c.set("displays.highest_price.2.glass", "OBSIDIAN");
            c.set("displays.highest_price.2.base", "CHISELED_TUFF_BRICKS");
            c.set("displays.highest_price.2.sign", "DARK_OAK_WALL_SIGN");
            c.set("displays.highest_price.default.glass", "LODESTONE");
            c.set("displays.highest_price.default.base", "CHISELED_TUFF_BRICKS");
            c.set("displays.highest_price.default.sign", "DARK_OAK_WALL_SIGN");

            c.set("displays.ending_soon.1.glass", "GOLD_BLOCK");
            c.set("displays.ending_soon.1.base", "CHISELED_TUFF_BRICKS");
            c.set("displays.ending_soon.1.sign", "DARK_OAK_WALL_SIGN");
            c.set("displays.ending_soon.2.glass", "OBSIDIAN");
            c.set("displays.ending_soon.2.base", "CHISELED_TUFF_BRICKS");
            c.set("displays.ending_soon.2.sign", "DARK_OAK_WALL_SIGN");
            c.set("displays.ending_soon.default.glass", "LODESTONE");
            c.set("displays.ending_soon.default.base", "CHISELED_TUFF_BRICKS");
            c.set("displays.ending_soon.default.sign", "DARK_OAK_WALL_SIGN");
            loadDisplays(c);
        }
        ConfigManager.messages.save();
        ConfigManager.messages.reload();
        AuctionHouse.getPlugin().saveConfig();
        AuctionHouse.getPlugin().reloadConfig();
    }

}
