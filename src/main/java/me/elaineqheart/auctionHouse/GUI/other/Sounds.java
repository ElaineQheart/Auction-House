package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
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
    public static void openShulker(Player p) {
        playSound(p, SettingManager.soundOpenShulker, 0.5f, 1);
    }
    public static void closeShulker(InventoryCloseEvent event) {
        playSound(event, SettingManager.soundCloseShulker, 0.5f, 1);
    }
    public static void openBundle(Player p) {
        playSound(p, SettingManager.soundOpenBundle, 0.5f, 1);
    }
    public static void closeBundle(InventoryCloseEvent event) {
        playSound(event, SettingManager.soundCloseBundle, 0.5f, 1);
    }

    public static void click(Player p) {
        playSound(p, SettingManager.soundClick, 0.2f,1);
    }
    public static void breakWood(Player p) {
        playSound(p, SettingManager.soundBreakWood, 0.5f, 1);
    }

    public static void npcClick(Player p) {
        p.playSound(p.getLocation(), getSound(SettingManager.soundNPCClick), 0.5f,1);
    }

    private static void playSound(Player p, String soundName, float volume, float pitch) {
        p.playSound(p.getLocation(), getSound(soundName), volume, pitch);
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
        //return Registry.SOUNDS.get(NamespacedKey.minecraft(name));
        if(!ConfigManager.oldVersion21()) return Registry.SOUNDS.get(NamespacedKey.minecraft(name));
        Sound sound;
        switch (name) {
            case "block.ender_chest.open" -> sound = Sound.BLOCK_ENDER_CHEST_OPEN;
            case "block.ender_chest.close" -> sound = Sound.BLOCK_ENDER_CHEST_CLOSE;
            case "block.wood.break" -> sound = Sound.BLOCK_WOOD_BREAK;
            case "entity.experience_orb.pickup" -> sound = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            case "entity.villager.no" -> sound = Sound.ENTITY_VILLAGER_NO;
            case "block.shulker_box.open" -> sound = Sound.BLOCK_SHULKER_BOX_OPEN;
            case "block.shulker_box.close" -> sound = Sound.BLOCK_SHULKER_BOX_CLOSE;
            default -> sound = Sound.UI_STONECUTTER_SELECT_RECIPE;
        }
        return sound;
    }
}
