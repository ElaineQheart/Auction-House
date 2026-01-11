package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.Objects;

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
        p.playSound(p.getLocation(), Sound.valueOf(SettingManager.soundClick), 0.2f,1);
    }


    private static void playSound(InventoryClickEvent event, String soundName, float volume, float pitch) {
        try {
            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), Sound.valueOf(soundName), volume, pitch);
        } catch (IllegalArgumentException e) {
            // Invalid sound
        }
    }
    private static void playSound(InventoryCloseEvent event, String soundName, float volume, float pitch) {
        try {
            ((Player) event.getPlayer()).playSound(event.getPlayer().getLocation(), Sound.valueOf(soundName), volume, pitch);
        } catch (IllegalArgumentException e) {
            // Invalid sound
        }
    }
}
