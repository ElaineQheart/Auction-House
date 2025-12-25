package me.elaineqheart.auctionHouse.data.persistentStorage.yml;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blacklist {

    //ah blacklist add <exact/material/name_contains/contains_lore> <rule_name>
    //ah blacklist remove <rule_name>

    // <name> = id

    public static boolean isBlacklisted(ItemStack item) {
        boolean blacklisted = false;
        for(Map<?, ?> entry : getData()) {
            Object keyObj = entry.get("key");
            switch (entry.get("type").toString()) {
                case "exact" -> blacklisted = isExact(item, (ItemStack) keyObj);
                case "material" -> blacklisted = isMaterial(item, keyObj.toString());
                case "lore" -> blacklisted = loreContains(item, keyObj.toString());
                case "name" -> blacklisted = nameContains(item, keyObj.toString());
                case null -> {}
                default -> throw new IllegalStateException("Unexpected value: " + keyObj);
            }
            if(blacklisted) return true;
        }
        return false;
    }

    private static boolean isExact(ItemStack item, ItemStack key) {
        key.setAmount(item.getAmount());
        return item.equals(key);
    }
    private static boolean isMaterial(ItemStack item, String key) {
        return item.getType() == Material.getMaterial(key);
    }
    private static boolean loreContains(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null || meta.getLore() == null) return false;
        return meta.getLore().contains(key);
    }
    private static boolean nameContains(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        return meta.getDisplayName().contains(key) || meta.getItemName().contains(key);
    }

    public static void addExact(ItemStack item) {
        List<Map<?, ?>> blacklist = getData();
        Map<String, Object> entry1 = new HashMap<>();
        entry1.put("type", "exact");
        entry1.put("key", item);
        blacklist.add(entry1);
        save(blacklist);
    }

    public static void addMaterial(String material) {
        List<Map<?, ?>> blacklist = getData();
        Map<String, Object> entry1 = new HashMap<>();
        entry1.put("type", "material");
        entry1.put("key", material);
        blacklist.add(entry1);
        save(blacklist);
    }

    public static void addLoreContains(String lore) {
        List<Map<?, ?>> blacklist = getData();
        Map<String, Object> entry1 = new HashMap<>();
        entry1.put("type", "lore");
        entry1.put("key", lore);
        blacklist.add(entry1);
        save(blacklist);
    }

    public static void addNameContains(String itemName) {
        List<Map<?, ?>> blacklist = getData();
        Map<String, Object> entry1 = new HashMap<>();
        entry1.put("type", "name");
        entry1.put("key", itemName);
        blacklist.add(entry1);
        save(blacklist);
    }

    public static boolean undo() {
        List<Map<?, ?>> blacklist = getData();
        if(!blacklist.isEmpty()) {
            blacklist.removeLast();
            save(blacklist);
            return true;
        }
        return false;
    }

    public static boolean exists(String name) {
        return ConfigManager.blacklist.get().get(name) != null;
    }

    private static List<Map<?, ?>> getData() {
        List<Map<?, ?>> blacklist = ConfigManager.blacklist.get().getMapList("blacklist");
        if(blacklist.isEmpty()) {
            blacklist = new ArrayList<>();
            save(blacklist);
        }
        return blacklist;
    }
    private static void save(List<Map<?, ?>> blacklist) {
        ConfigManager.blacklist.get().set("blacklist", blacklist);
        ConfigManager.blacklist.save();
        ConfigManager.blacklist.reload();
    }

}
