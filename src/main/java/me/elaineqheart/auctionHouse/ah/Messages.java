package me.elaineqheart.auctionHouse.ah;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;


public class Messages {

    private static FileConfiguration messagesConfig;
    private static File messagesFile;
    private static String prefix;

    public static void init(JavaPlugin plugin) {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = messagesConfig.getString("prefix", "&8[&6AH&8] &r");
    }

    public static String get(String key) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        return ChatColor.translateAlternateColorCodes('&', prefix + message);
    }

    public static String getRaw(String key) {
        String message = messagesConfig.getString(key);
        if (message == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getFormatted(String key, String... replacements) {
        String message = get(key);
        if (replacements.length % 2 != 0) {
            return ChatColor.RED + "Invalid placeholder replacements for key: " + key;
        }
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

    public static void reload() {
        if (messagesFile == null) {
            return;
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        prefix = messagesConfig.getString("prefix", "&8[&6AH&8] &r");
    }
}