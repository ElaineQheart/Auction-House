package me.elaineqheart.auctionHouse.data.yml;

import com.google.common.base.Charsets;
import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class Messages {

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


    private static String getValue(String key, boolean convertNewLine) {
        String message = customFile.getString(key);
        if (message == null) {
            return ChatColor.RED + "Missing message key: " + key;
        }
        return convertNewLine ? message.replace("&n", "\n") : message;
    }

    //this is to replace placeholders like %player%
    public static String getFormatted(String key, String... replacements) {
        String message = getValue(key,true);
        message = replacePlaceholders(key, message, replacements);
        return message;
    }

    public static List<String> getLoreList(String key, String... replacements) {
        String message = getValue(key,false);
        message = replacePlaceholders(key, message, replacements);
        return List.of(message.split("&n"));
    }

    private static String replacePlaceholders(String key, String message, String... replacements) {
        if (replacements.length % 2 != 0) {
            return ChatColor.RED + "Invalid placeholder replacements for key: " + key;
        }
        for (int i = 0; i < replacements.length; i += 2) {
            message = message.replace(replacements[i], replacements[i + 1]);
        }
        return message;
    }

}
