package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Layout extends Config {

    public List<String> ahLayout;
    public List<String> myAhLayout;
    private HashMap<String, ItemStack> items = new HashMap<>();

    @Override
    public void setup() {
        FileConfiguration l = ConfigManager.layout.getCustomFile();
        ahLayout = l.getStringList("ah-layout");
        myAhLayout = l.getStringList("my-ah-layout");
        if(ahLayout.isEmpty() || myAhLayout.isEmpty()) updateLayout(l);
    }

    private void updateLayout(FileConfiguration l) {
        l.set("ah-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "s o # p r n # # m"));
        l.set("my-ah-layout", Arrays.asList(
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                "b o # p r n # # i"));
        ConfigManager.layout.save();
        ahLayout = l.getStringList("ah-layout");
        myAhLayout = l.getStringList("my-ah-layout");
    }

    public ItemStack getItem(String path) {
        if (items.get(path) != null) return items.get(path).clone();
        ItemStack item = ConfigManager.layout.getCustomFile().getItemStack(path);
        assert item != null : "The provided item at " + path + " is not serializable.";
        items.put(path, item);
        return item.clone();
    }

    public void saveItem(ItemStack item) {
        ConfigManager.layout.getCustomFile().set("test", item);
        ConfigManager.layout.save();
    }

    @Override
    public void reloadChild() {
        setup();
        ItemManager.reload();
        items = new HashMap<>();
    }
}
