package me.elaineqheart.auctionHouse.data.messages;

import com.google.common.base.Charsets;
import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MessagesConfig {

    private static File file;
    private static FileConfiguration customFile;

    public static void setup() {
        file = new File(AuctionHouse.getPlugin().getDataFolder(), "messages.yml");

        if (!file.exists()) {
            AuctionHouse.getPlugin().saveResource("messages.yml", false);
        }
        customFile = YamlConfiguration.loadConfiguration(file);

        //load the messages.yml file from the jar file and update missing keys with defaults
        final InputStream defConfigStream = AuctionHouse.getPlugin().getResource("messages.yml");
        if (defConfigStream == null) {
            return;
        }
        customFile.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static String getValue(String key) {
        String message = customFile.getString(key);
        if (message == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void save(){
        try {
            customFile.save(file);
        }catch (IOException e){
            AuctionHouse.getPlugin().getLogger().severe("Couldn't save messages.yml file");
        }
    }

    public static void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}
