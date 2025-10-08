package me.elaineqheart.auctionHouse.GUI.other;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Sounds {

    public static void click(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.UI_STONECUTTER_SELECT_RECIPE,0.2f,1);
    }
    public static void openEnderChest(InventoryClickEvent event) {
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
    public static void openShulker(InventoryClickEvent event) {
        ((Player) event.getWhoClicked()).playSound(event.getWhoClicked(), Sound.BLOCK_SHULKER_BOX_OPEN, 0.5f, 1);
    }
    public static void closeShulker(InventoryCloseEvent event) {
        ((Player) event.getPlayer()).playSound(event.getPlayer(), Sound.BLOCK_SHULKER_BOX_CLOSE, 0.5f, 1);
    }

    public static void click(Player p) {
        p.playSound(p.getLocation(), Sound.UI_STONECUTTER_SELECT_RECIPE,0.2f,1);
    }

}
