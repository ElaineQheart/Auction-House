package me.elaineqheart.auctionHouse.data.persistentStorage.local.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ConfigManager {

    public static M messages = new M();
    public static Displays displays = new Displays();
    public static BannedPlayers bannedPlayers = new BannedPlayers();
    public static Permissions permissions = new Permissions();
    public static Blacklist blacklist = new Blacklist();
    public static Config categories = new Config();
    public static PlayerPreferences playerPreferences = new PlayerPreferences();
    public static Layout layout = new Layout();
    public static TransactionLogger transactionLogger = new TransactionLogger();
    private static final List<Config> list = new ArrayList<>();
    private static final AuctionHouse instance = AuctionHouse.getInstance();

    public static void setupConfigs() {
        //Setup config.yml
        saveDefaultConfig();
//        if (!Files.exists(Path.of(AuctionHouse.getPlugin().getDataFolder().getPath() + ".config.yml"))) {
//            AuctionHouse.getPlugin().saveResource("config.yml", false);
//        }
        //AuctionHouse.getPlugin().saveResource("config.yml", false);
//        System.out.println(Files.exists(Path.of(AuctionHouse.getPlugin().getDataFolder().getPath() + ".config.yml")));
//        System.out.println(Path.of(AuctionHouse.getPlugin().getDataFolder().getPath() + ".config.yml"));
        //Config defaultConfig = new Config();
        //defaultConfig.setup("config.yml", true, "");
        //AuctionHouse.getPlugin().reloadConfig();
        AuctionHouse.getInstance().getConfig().options().copyDefaults(true);
        AuctionHouse.getInstance().saveConfig();

        messages.setup("messages.yml", true, "");
        displays.setup("displays.yml", false, "/data");
        bannedPlayers.setup("bannedPlayers.yml", false, "/data");
        permissions.setup("permissions.yml", true, "");
        blacklist.setup("blacklist.yml", false, "/data");
        categories.setup("categories.yml", false, "/data");
        playerPreferences.setup("playerPreferences.yml", false, "/data");
        layout.setup("layout.yml", true, "");
        transactionLogger.setup(transactionLogger.getNewName(), false, "/logs");
        instance.getMorePaperLib().scheduling().globalRegionalScheduler().run(displays::backwardsCompatibility);
        //old method: Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), ConfigManager::displaysBackwardsCompatibility);
        permissionsSetup();
    }

    private static void saveDefaultConfig() {
        String resourcePath = "config.yml";
        InputStream in = AuctionHouse.getInstance().getResource(resourcePath);
        assert in != null;
        //FileConfiguration c = AuctionHouse.getPlugin().getConfig();
        File outFile = new File(AuctionHouse.getInstance().getDataFolder(), resourcePath);

        try {
            if (outFile.getParentFile().mkdirs() || outFile.createNewFile()) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            }
        } catch (IOException ex) {
            AuctionHouse.getInstance().getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
//        AuctionHouse.getPlugin().getConfig().options().copyDefaults(true);
//        String[] keyList = {"tax", "auction-setup-time", "default-max-auctions", "sold-message", "auto-collect", "partial-selling",
//                "admin-permission", "display-update", "auction-announcements", "bin-auctions", "bin-auction-duration", "min-bin",
//                "bid-auctions", "bid-auction-duration", "min-bid", "last-bid-extra-time", "bid-increase-percent"};
//        for (String key : keyList) {
//            if (!c.contains(key)) c.set();
//        }
//        System.out.println(test);
    }

    public static boolean backwardsCompatibility() {
        FileConfiguration c = AuctionHouse.getInstance().getConfig();
        if(c.getString("plugin-version") == null) return true;
        return !Objects.equals(c.getString("plugin-version"), AuctionHouse.getInstance().getDescription().getVersion());
    }

    public static void reloadConfigs() {
        AuctionHouse.getInstance().reloadConfig();
        getList().forEach(Config::reload);
        transactionLogger.setup(transactionLogger.getNewName(), false, "/logs");
    }

    private static List<Config> getList() {
        if(list.isEmpty()) list.addAll(List.of(messages, displays, bannedPlayers, permissions, blacklist, categories, playerPreferences, layout, transactionLogger));
        return list;
    }

    private static void permissionsSetup() {
        if(permissions.getCustomFile().getConfigurationSection("auction-slots") == null) {
            permissions.getCustomFile().createSection("auction-slots");
            permissions.save();
        }
        if(permissions.getCustomFile().getConfigurationSection("bin-auction-duration") == null) {
            permissions.getCustomFile().createSection("bin-auction-duration");
            permissions.save();
        }
        if(permissions.getCustomFile().getConfigurationSection("bid-auction-duration") == null) {
            permissions.getCustomFile().createSection("bid-auction-duration");
            permissions.save();
        }
    }

    public static boolean oldVersion21() {
        return oldVersionCheck(List.of("1.21.4-", "1.21.3-", "1.21.2-", "1.21.1-", "1.21-"));
    }

    private static boolean oldVersionCheck(List<String> versions) {
        String currentVersion = Bukkit.getVersion();
        for(String version : versions) {
            if(currentVersion.contains(version)) return true;
        }
        return false;
    }

}
