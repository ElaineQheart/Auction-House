package me.elaineqheart.auctionHouse.GUI.other;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class Sounds {

    public static void click(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.UI_STONECUTTER_SELECT_RECIPE,0.2f,1);
    }
    public static void enderChest(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.BLOCK_ENDER_CHEST_OPEN,0.5f,1);
    }
    public static void closeEnderChest(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.BLOCK_ENDER_CHEST_CLOSE,0.5f,1);
    }
    public static void breakWood(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.BLOCK_WOOD_BREAK,0.5f,1);
    }
    public static void experience(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,0.5f,0.9f);
    }
    public static void villagerDeny(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.ENTITY_VILLAGER_NO,0.5f,1);
    }

}
