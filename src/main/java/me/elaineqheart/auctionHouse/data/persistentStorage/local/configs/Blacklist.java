package me.elaineqheart.auctionHouse.data.persistentStorage.local.configs;

import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.Config;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blacklist extends Config {

    //ah blacklist add <exact/material/name_contains/contains_lore> <rule_name>
    //ah blacklist remove <rule_name>

    // <name> = id

    public static boolean isBlacklisted(ItemStack item) {
        return isBlacklisted(item, ConfigManager.blacklist.getData());
    }
    public static boolean isBlacklisted(ItemStack item, List<Map<?, ?>> blacklist) {
        boolean blacklisted = false;
        for(Map<?, ?> entry : blacklist) {
            Object keyObj = entry.get("key");
            switch (entry.get("type").toString()) {
                case "exact" -> blacklisted = isExact(item, (ItemStack) keyObj);
                case "material" -> blacklisted = isMaterial(item, keyObj.toString());
                // the 3 types below are mainly used by the categories (categories.yml), but work in blacklist.yml too
                case "material_contains" -> blacklisted = item.getType().name().contains(keyObj.toString().toUpperCase());
                case "edible" -> blacklisted = item.getType().isEdible();
                case "block" -> blacklisted = item.getType().isBlock();
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

    public void addExact(ItemStack item) {
        add("exact", item);
    }
    public void addMaterial(String material) {
        add("material", material);
    }
    public void addLoreContains(String lore) {
        add("lore", lore);
    }
    public void addNameContains(String itemName) {
        add("name", itemName);
    }
    public void addItemModel(String model) {
        add("item_model", model);
    }
    public void addCustomModelData(String model) {
        add("custom_model_data", model);
    }
    public void addAll() {
        add("all", "0");
    }

    private void add(String type, Object object) {
        List<Map<?, ?>> blacklist = getData();
        Map<String, Object> entry = new HashMap<>();
        entry.put("type", type);
        entry.put("key", object);
        blacklist.add(entry);
        save(blacklist);
    }

    public boolean undo() {
        List<Map<?, ?>> blacklist = getData();
        if(!blacklist.isEmpty()) {
            blacklist.removeLast();
            save(blacklist);
            return true;
        }
        return false;
    }

    private List<Map<?, ?>> getData() {
        List<Map<?, ?>> blacklist = getCustomFile().getMapList("blacklist");
        if(blacklist.isEmpty()) {
            blacklist = new ArrayList<>();
            save(blacklist);
        }
        return blacklist;
    }
    private void save(List<Map<?, ?>> blacklist) {
        getCustomFile().set("blacklist", blacklist);
        save();
        reload();
    }

}
