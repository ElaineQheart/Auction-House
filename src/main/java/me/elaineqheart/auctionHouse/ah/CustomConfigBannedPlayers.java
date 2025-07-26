// src/main/java/me/elaineqheart/auctionHouse/ah/CustomConfigBannedPlayers.java
package me.elaineqheart.auctionHouse.ah;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomConfigBannedPlayers {

    private static File file;
    private static FileConfiguration customFile;

    public static void setup(){
        file = new File(AuctionHouse.getPlugin().getDataFolder(), "bannedPlayers.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                System.out.println("Could not create bannedPlayers.yml");
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try {
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save bannedPlayers.yml");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void banPlayer(Player target, Player banner, int days, String reason){
        String path = "BannedPlayers." + target.getUniqueId();
        customFile.set(path + ".PlayerName", target.getName());
        customFile.set(path + ".reason", reason);
        customFile.set(path + ".banner", banner.getName());
        customFile.set(path + ".unban-date", System.currentTimeMillis() + (long) days * 24 * 60 * 60 * 1000);
        save();
    }

    public static boolean unbanPlayer(String name){
        String sectionPath = "BannedPlayers";
        if(customFile.isConfigurationSection(sectionPath)) {
            for (String key : customFile.getConfigurationSection(sectionPath).getKeys(false)) {
                if (name.equalsIgnoreCase(customFile.getString(sectionPath + "." + key + ".PlayerName"))) {
                    customFile.set(sectionPath + "." + key, null);
                    save();
                    return true;
                }
            }
        }
        return false;
    }

    public static List<String> getBannedPlayers(){
        List<String> bannedPlayers = new ArrayList<>();
        String path = "BannedPlayers";
        if (customFile.isConfigurationSection(path)) {
            Set<String> keys = customFile.getConfigurationSection(path).getKeys(false);
            for (String key : keys) {
                bannedPlayers.add(customFile.getString(path + "." + key + ".PlayerName"));
            }
        }
        return bannedPlayers;
    }


    public static boolean checkIsBannedSendMessage(Player p){
        String path = "BannedPlayers." + p.getUniqueId();
        if(customFile.contains(path)){
            long unbanDate = customFile.getLong(path + ".unban-date");
            if (System.currentTimeMillis() > unbanDate) {
                customFile.set(path, null);
                save();
                return false;
            }
            String reason = customFile.getString(path + ".reason");
            long days = (unbanDate - System.currentTimeMillis()) / (1000 * 60 * 60 * 24);
            p.sendMessage(Messages.getFormatted("banned-from-ah", "%days%", String.valueOf(days + 1), "%reason%", reason));
            return true;
        }
        return false;
    }
}