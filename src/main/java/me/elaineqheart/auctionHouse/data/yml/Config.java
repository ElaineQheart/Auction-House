package me.elaineqheart.auctionHouse.data.yml;

import com.google.common.base.Charsets;
import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Config {

    private File file;
    private FileConfiguration customFile;

    public void setup(String fileName, boolean copyDefaults){
        file = new File(AuctionHouse.getPlugin().getDataFolder(),  fileName + ".yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //uwu
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);

        if(!copyDefaults) return;
        final InputStream defConfigStream = AuctionHouse.getPlugin().getResource(fileName + ".yml");
        if (defConfigStream == null) return;
        customFile.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        customFile.options().copyDefaults(true);
        save();
    }

    public FileConfiguration get(){
        return customFile;
    }

    public void save(){
        try {
            customFile.save(file);
        }catch (IOException e){
            AuctionHouse.getPlugin().getLogger().severe("Couldn't save displays.yml file");
        }
    }

    public void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }

}
