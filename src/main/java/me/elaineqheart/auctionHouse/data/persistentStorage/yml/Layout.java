package me.elaineqheart.auctionHouse.data.persistentStorage.yml;

import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Layout {

    public static List<String> ahLayout;
    public static List<String> myAhLayout;

    static {
        loadData();
    }

    public static void loadData() {
        FileConfiguration l = ConfigManager.layout.get();
        ahLayout = l.getStringList("ah-layout");
        myAhLayout = l.getStringList("my-ah-layout");
        if(ahLayout.isEmpty() || myAhLayout.isEmpty()) updateLayout(l);
    }

    private static void updateLayout(FileConfiguration l) {
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

    public static ItemStack getItem(String path) {
        return Objects.requireNonNull(ConfigManager.layout.get().getItemStack(path)).clone();
    }

    public static void saveItem(ItemStack item) {
        ConfigManager.layout.get().set("test", item);
        ConfigManager.layout.save();
    }

    public static void reload() {
        loadData();
        ItemManager.reload();
    }
}
