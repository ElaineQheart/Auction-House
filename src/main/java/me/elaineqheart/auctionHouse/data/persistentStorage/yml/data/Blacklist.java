package me.elaineqheart.auctionHouse.data.persistentStorage.yml.data;

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
        return isBlacklisted(item, getData());
    }
    public static boolean isBlacklisted(ItemStack item, List<Map<?, ?>> blacklist) {
        boolean blacklisted = false;
        for(Map<?, ?> entry : blacklist) {
            Object keyObj = entry.get("key");
            switch (entry.get("type").toString()) {
                case "exact" -> blacklisted = isExact(item, (ItemStack) keyObj);
                case "material" -> blacklisted = isMaterial(item, keyObj.toString());
                case "lore" -> blacklisted = loreContains(item, keyObj.toString());
                case "name" -> blacklisted = nameContains(item, keyObj.toString());
                case "item_model" -> blacklisted = itemModelContains(item, keyObj.toString());
                case "custom_model_data" -> blacklisted = customModelContains(item, keyObj.toString());
                case "all" -> blacklisted = true;
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
    private static boolean itemModelContains(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null || !meta.hasItemModel() || meta.getItemModel() == null) return false;
        //if(meta.hasCustomModelData()) blacklisted |= String.valueOf(meta.getCustomModelData()).contains(key); //deprecated
        return meta.getItemModel().getKey().contains(key);
    }
    private static boolean customModelContains(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        return meta.getCustomModelDataComponent().getStrings().stream().anyMatch(s -> s.equals(key));
    }

    public static void addExact(ItemStack item) {
        add("exact", item);
    }
    public static void addMaterial(String material) {
        add("material", material);
    }
    public static void addLoreContains(String lore) {
        add("lore", lore);
    }
    public static void addNameContains(String itemName) {
        add("name", itemName);
    }
    public static void addItemModel(String model) {
        add("item_model", model);
    }
    public static void addCustomModelData(String model) {
        add("custom_model_data", model);
    }
    public static void addAll() {
        add("all", "0");
    }

    private static void add(String type, Object object) {
        List<Map<?, ?>> blacklist = getData();
        Map<String, Object> entry = new HashMap<>();
        entry.put("type", type);
        entry.put("key", object);
        blacklist.add(entry);
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
