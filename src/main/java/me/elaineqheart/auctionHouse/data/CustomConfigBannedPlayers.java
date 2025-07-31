package me.elaineqheart.auctionHouse.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class CustomConfigBannedPlayers {

    //In the yml file the players that are banned from the auction house will be stored

    private static File file;
    private static FileConfiguration customFile;

    //Finds or generates the custom config file
    public static void setup(){
        file = new File(AuctionHouse.getPlugin().getDataFolder(), "bannedPlayers.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //uwu
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
            Bukkit.getLogger().warning("Couldn't save bannedPlayers.yml");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void saveBannedPlayer(Player p, int durationInDays, String reason){
        int timeInMillis = durationInDays * 24 * 60 * 60 * 1000;
        long banEndDate = new Date().getTime() + timeInMillis;
        Date date = new Date(banEndDate);

        String path = "BannedPlayers." + p.getUniqueId();
        String playerName = p.getName();

        customFile.set(path + ".Date", date);
        customFile.set(path + ".PlayerName", playerName);
        customFile.set(path + ".Reason", reason);
        save();
    }

    //if the player is banned, send them a message
    public static boolean checkIsBannedSendMessage(Player p){
        String path = "BannedPlayers." + p.getUniqueId();
        if (customFile.get(path) == null) return false;
        Date banEndDate = (Date) customFile.get(path + ".Date");
        if (banEndDate == null) return false;
        long currentTime = new Date().getTime();
        System.out.println(currentTime);
        if (currentTime > banEndDate.getTime()){
            customFile.set(path, null);
            save();
            return false;
        }
        long banDuration = banEndDate.getTime() - currentTime;
        p.sendMessage(ChatColor.WHITE + "You are temporarily banned for " + ChatColor.YELLOW + StringUtils.getTime(banDuration/1000, true)
                + ChatColor.WHITE + " from the auction house.");
        p.sendMessage(ChatColor.GRAY + "Reason: " + customFile.getString(path + ".Reason"));
        return true;
    }



}
