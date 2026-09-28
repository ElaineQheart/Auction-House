package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.elaineqheart.auctionHouse.data.persistentStorage.local.LayoutGenerator;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class Layout extends Config {

    public List<String> ahLayout;
    public List<String> myAhLayout;
    public List<String> hubLayout;
    public List<String> sellLayout;
    public List<String> historyLayout;
    private HashMap<String, ItemStack> items = new HashMap<>();

    @Override
    public void setup() {
        FileConfiguration c = getCustomFile();
        if(c.getStringList("ah-layout").isEmpty() || c.getStringList("my-ah-layout").isEmpty()) {
            LayoutGenerator.generate(c);
            save();
        }
        if (ConfigManager.backwardsCompatibility()) {
            LayoutGenerator.backWardsCompatibility(c);
            save();
        }
        // one-time migration to the default layout with the category sidebar (see LayoutGenerator.LAYOUT_VERSION)
        if (c.getInt("layout-version", 0) < LayoutGenerator.LAYOUT_VERSION) {
            LayoutGenerator.migrateLayout(c);
            c.set("layout-version", LayoutGenerator.LAYOUT_VERSION);
            save();
        }
        if (LayoutGenerator.addMissing(c)) save();
        ahLayout = c.getStringList("ah-layout");
        myAhLayout = c.getStringList("my-ah-layout");
        hubLayout = c.getStringList("hub-layout");
        sellLayout = c.getStringList("sell-layout");
        historyLayout = c.getStringList("history-layout");
    }

    public ItemStack getItem(String path) {
        if (items.get(path) != null) return items.get(path).clone();
        ItemStack item = getCustomFile().getItemStack(path);
        assert item != null : "The provided item at " + path + " is not serializable.";
        items.put(path, item);
        return item.clone();
    }

    public void saveItem(ItemStack item) {
        getCustomFile().set("test", item);
        save();
    }

    @Override
    public void reloadChild() {
        setup();
        ItemManager.reload();
        items = new HashMap<>();
    }
}
