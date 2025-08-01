package me.elaineqheart.auctionHouse.data;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

public class DisplaysConfig {

    private static File file;
    private static FileConfiguration customFile;

    //Finds or generates the custom config file
    public static void setup(){
        file = new File(AuctionHouse.getPlugin().getDataFolder(), "displays.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //uwu
            }

        }
        customFile = YamlConfiguration.loadConfiguration(file);
        backwardsCompatibility();
    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try {
            customFile.save(file);
        }catch (IOException e){
            System.out.println("Couldn't save file");
        }
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }


    private static void backwardsCompatibility() {
        Set<Integer> oldSet = null;
        try {
            // This method is for backwards compatibility
            oldSet = customFile.getKeys(false).stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toSet());
        } catch (NumberFormatException ignored) {}

        //This section of code is needed, even without backwards compatibility
        if (customFile.getConfigurationSection("displays") == null) {
            customFile.createSection("displays");
        }

        if(oldSet != null) {
            for (Integer displayID : oldSet) {
                customFile.getConfigurationSection("displays").set(String.valueOf(displayID), customFile.get(String.valueOf(displayID)));
                customFile.set(String.valueOf(displayID), null); // Remove the old key
            }
        }
        save();

    }
}
