package me.elaineqheart.auctionHouse.data.items;

import me.elaineqheart.auctionHouse.data.yml.ConfigManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class Blacklist {

    //ah blacklist add <exact/material/name_contains/contains_lore> <rule_name>
    //ah blacklist remove <rule_name>

    // <name> = id

    public static boolean isBlacklisted(ItemStack item) {
        boolean blacklisted = false;
        for(String key : ConfigManager.blacklist.get().getKeys(false)) {
            String valueKey = key + ".value";
            switch (ConfigManager.blacklist.get().getString(key + ".key")) {
                case "exact" -> blacklisted = isExact(item, valueKey);
                case "material" -> blacklisted = isMaterial(item, valueKey);
                case "lore" -> blacklisted = loreContains(item, valueKey);
                case "name" -> blacklisted = nameContains(item, valueKey);
                case null -> {}
                default -> throw new IllegalStateException("Unexpected value: " + ConfigManager.blacklist.get().getString(key + ".key"));
            }
            if(blacklisted) return true;
        }
        return false;
    }

    private static boolean isExact(ItemStack item, String key) {
        ItemStack blacklistedItem = ConfigManager.blacklist.get().getItemStack(key);
        assert blacklistedItem != null;
        blacklistedItem.setAmount(item.getAmount());
        return item == blacklistedItem;
    }
    private static boolean isMaterial(ItemStack item, String key) {
        String material = ConfigManager.blacklist.get().getString(key);
        assert material != null;
        return item.getType() == Material.getMaterial(material);
    }
    private static boolean loreContains(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null || meta.getLore() == null) return false;
        String lore = ConfigManager.blacklist.get().getString(key);
        assert lore != null;
        return meta.getLore().contains(lore);
    }
    private static boolean nameContains(ItemStack item, String key) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        String name = ConfigManager.blacklist.get().getString(key);
        assert name != null;
        return meta.getDisplayName().contains(name) || meta.getItemName().contains(name);
    }

    public static void addExact(ItemStack item, String name) {
        ConfigManager.blacklist.get().set(name + ".key", "exact");
        ConfigManager.blacklist.get().set(name + ".value", item);
        ConfigManager.blacklist.save();
        ConfigManager.blacklist.reload();
    }

    public static void addMaterial(String material, String name) {
        ConfigManager.blacklist.get().set(name + ".key", "material");
        ConfigManager.blacklist.get().set(name + ".value", material);
        ConfigManager.blacklist.save();
        ConfigManager.blacklist.reload();
    }

    public static void addLoreContains(String lore, String name) {
        ConfigManager.blacklist.get().set(name + ".key", "lore");
        ConfigManager.blacklist.get().set(name + ".value", lore);
        ConfigManager.blacklist.save();
        ConfigManager.blacklist.reload();
    }

    public static void addNameContains(String itemName, String name) {
        ConfigManager.blacklist.get().set(name + ".key", "name");
        ConfigManager.blacklist.get().set(name + ".value", itemName);
        ConfigManager.blacklist.save();
        ConfigManager.blacklist.reload();
    }

    public static void remove(String name) {
        ConfigManager.blacklist.get().set(name, null);
        ConfigManager.blacklist.save();
        ConfigManager.blacklist.reload();
    }

    public static boolean exists(String name) {
        return ConfigManager.blacklist.get().get(name) != null;
    }

}
