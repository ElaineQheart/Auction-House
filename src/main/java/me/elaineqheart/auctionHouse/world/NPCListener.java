package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import java.util.HashSet;
import java.util.Set;

public class NPCListener implements Listener {

    private static final Set<Player> store = new HashSet<>();

    @EventHandler
    public void onAuctionMasterClick(PlayerInteractAtEntityEvent event) {
        //check if the clicked entity is the Auction Master
        if (event.getRightClicked().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_master"))) {
            store.add(event.getPlayer());
        }
    }

    @EventHandler
    public void onAuctionMasterClickOpen(InventoryOpenEvent event){
        Player p = (Player) event.getPlayer();
        if(store.contains(p)){
            store.remove(p);
            event.setCancelled(true);
            p.playSound(p, Sound.UI_STONECUTTER_SELECT_RECIPE,0.5f,1);

            // Open the auction GUI for the player
            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
        }
    }

    //connect the stand with the villager, so if one dies, both die
    @EventHandler
    public void onVillagerDeath(EntityDeathEvent event) {
        if(event.getEntity().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_master"))) {
            CreateNPC.removeAuctionMaster((Villager) event.getEntity());
        }
        if(event.getEntity().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_stand"))) {
            CreateNPC.removeAuctionMaster((ArmorStand) event.getEntity());
        }
    }

}
