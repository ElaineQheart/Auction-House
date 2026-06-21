package me.elaineqheart.auctionHouse.world.npc;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class NPCListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAuctionMasterClick(PlayerInteractEntityEvent event) {
        if (!NPCManager.isNPC(event.getRightClicked())) return;
        event.setCancelled(true);
        Player p = event.getPlayer();
        Sounds.npcClick(p);
        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
    }

    //connect the stand with the villager, so if one dies, both die
    @EventHandler
    public void onVillagerDeath(EntityDeathEvent event) {
        if(event.getEntity().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getInstance(), "auction_master"))) {
            NPCManager.removeAuctionMaster((Villager) event.getEntity());
        }
        if(event.getEntity().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getInstance(), "auction_stand"))) {
            NPCManager.removeAuctionMaster((ArmorStand) event.getEntity());
        }
    }

    @EventHandler
    public void onVillagerHit(EntityDamageByEntityEvent event) {
        if(!(event.getDamager() instanceof Player p)) return;
        if(!NPCManager.isNPC(event.getEntity())) return;
        if(!p.hasPermission(SettingManager.permissionModerate)) event.setCancelled(true);
    }

}
