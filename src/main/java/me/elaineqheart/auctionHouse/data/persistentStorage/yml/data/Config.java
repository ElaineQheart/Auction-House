package me.elaineqheart.auctionHouse.data.persistentStorage.yml.data;

import com.google.common.base.Charsets;
import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Config {

    private File file;
    private FileConfiguration customFile;

    public void setup(String fileName, boolean copyDefaults, String parent){
        if(ConfigManager.backwardsCompatibility() && !parent.isEmpty()) backwardsCompatibility(fileName, parent);
        file = new File(AuctionHouse.getPlugin().getDataFolder() + parent,  fileName + ".yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            }catch (IOException e){
                //uwu
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);

        if(!copyDefaults) {
            save();
            return;
        }
        final InputStream defConfigStream = AuctionHouse.getPlugin().getResource(parent + fileName + ".yml");
        if (defConfigStream == null) return;
        customFile.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
        customFile.options().copyDefaults(true);
        save();
    }

    public FileConfiguration get(){
        return customFile;
    }

    public File getFile() {
        return file;
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

    private void backwardsCompatibility(String fileName, String parent) {
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + parent + "/" + fileName + ".yml");
        File old = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/" + fileName + ".yml");
        if (old.exists()) {
            try {
                Files.copy(old.getAbsoluteFile().toPath(), file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
                old.delete();
            } catch (IOException ignored) {}
        }
    }

}
