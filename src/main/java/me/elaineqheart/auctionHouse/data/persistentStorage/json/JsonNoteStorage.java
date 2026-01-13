package me.elaineqheart.auctionHouse.data.persistentStorage.json;

import com.google.gson.Gson;
import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class JsonNoteStorage {

    //This class is where the Note objects are managed
    //gson is used to convert the Note objects into json Strings and backwards

    public static void createNote(Player p, ItemStack item, double price, boolean isBINAuction){

        ItemNote itemNote = new ItemNote(p, item, price, isBINAuction);
        AuctionHouseStorage.add(itemNote);

        try {
            saveNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteNote(ItemNote note) {
        AuctionHouseStorage.remove(note);

        try {
            saveNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveNotes() throws IOException {

        Gson gson = new Gson();
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/data/notes.json");
        //if the parent file of the plugin doesn't exist, it has to be created
        file.getParentFile().mkdir();
        file.createNewFile();
        //if append is true, it will append the json text and not overwrite the file
        Writer writer = new FileWriter(file, false);
        gson.toJson(AuctionHouseStorage.getAll(), writer);
        //flush = write data
        writer.flush();
        writer.close();

    }

    public static void loadNotes() throws IOException {
        if(ConfigManager.backwardsCompatibility()) backwardsCompatibility();
        Gson gson = new Gson();
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/data/notes.json");
        if(file.exists()){
            Reader reader = new FileReader(file);
            ItemNote[] items = gson.fromJson(reader, ItemNote[].class);
            AuctionHouseStorage.set(items);
        }
    }

    private static void backwardsCompatibility() throws IOException {
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/data/notes.json");
        File old = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/notes.json");
        if (old.exists()) {
            Files.copy(old.getAbsoluteFile().toPath(), file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            old.delete();
        }
    }

    public static void purge() {
        AuctionHouseStorage.set(new ItemNote[0]);
        try {
            saveNotes();
            loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
