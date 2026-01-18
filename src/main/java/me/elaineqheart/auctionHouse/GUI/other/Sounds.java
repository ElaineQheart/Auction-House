package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Sounds {

    public static void click(InventoryClickEvent event) {
        playSound(event, SettingManager.soundClick, 0.2f, 1);
    }
    public static void openEnderChest(InventoryClickEvent event) {
        playSound(event, SettingManager.soundOpenEnderchest, 0.5f, 1);
    }
    public static void closeEnderChest(InventoryClickEvent event) {
        playSound(event, SettingManager.soundCloseEnderchest, 0.5f, 1);
    }
    public static void breakWood(InventoryClickEvent event) {
        playSound(event, SettingManager.soundBreakWood, 0.5f, 1);
    }
    public static void experience(InventoryClickEvent event) {
        playSound(event, SettingManager.soundExperience, 0.5f, 0.9f);
    }
    public static void villagerDeny(InventoryClickEvent event) {
        playSound(event, SettingManager.soundVillagerDeny, 0.5f, 1);
    }
    public static void openShulker(InventoryClickEvent event) {
        playSound(event, SettingManager.soundOpenShulker, 0.5f, 1);
    }
    public static void closeShulker(InventoryCloseEvent event) {
        playSound(event, SettingManager.soundCloseShulker, 0.5f, 1);
    }

    public static void click(Player p) {
        p.playSound(p.getLocation(), getSound(SettingManager.soundClick), 0.2f,1);
    }


    private static void playSound(InventoryClickEvent event, String soundName, float volume, float pitch) {
        try {
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), getSound(soundName), volume, pitch);
        } catch (IllegalArgumentException e) {
            // Invalid sound
        }
    }
    private static void playSound(InventoryCloseEvent event, String soundName, float volume, float pitch) {
        try {
            ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), getSound(soundName), volume, pitch);
        } catch (IllegalArgumentException e) {
            // Invalid sound
        }
    }

    private static Sound getSound(String name) {
        if(!
                ConfigManager.oldVersion21()) return Sound.valueOf(name);
        Sound sound;
        switch (name) {
            case "BLOCK_ENDER_CHEST_OPEN" -> sound = Sound.BLOCK_ENDER_CHEST_OPEN;
            case "BLOCK_ENDER_CHEST_CLOSE" -> sound = Sound.BLOCK_ENDER_CHEST_CLOSE;
            case "BLOCK_WOOD_BREAK" -> sound = Sound.BLOCK_WOOD_BREAK;
            case "ENTITY_EXPERIENCE_ORB_PICKUP" -> sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            case "ENTITY_VILLAGER_NO" -> sound = Sound.ENTITY_VILLAGER_NO;
            case "BLOCK_SHULKER_BOX_OPEN" -> sound = Sound.BLOCK_SHULKER_BOX_OPEN;
            case "BLOCK_SHULKER_BOX_CLOSE" -> sound = Sound.BLOCK_SHULKER_BOX_CLOSE;
            default -> sound = Sound.UI_STONECUTTER_SELECT_RECIPE;
        }
        return sound;
    }
}
