package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.Permissions;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.persistence.PersistentDataType;

public class DisplayBreakListener implements Listener {

    @EventHandler
    public void onDisplayBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        Player p = event.getPlayer();
        Location displayLoc = isProtected(loc);
        if(displayLoc == null) {
            return; // Not a display location
        }

        if(!p.getGameMode().equals(GameMode.CREATIVE) || !p.hasPermission(Permissions.MODERATE)) {
            event.setCancelled(true);
            return;
        }
        DisplayUpdate.removeDisplay(displayLoc);
    }

    private Location isProtected(Location loc) {
        for (Location loc2 : DisplayUpdate.locations.keySet()) {
            if (loc.equals(loc2) || loc.add(0,0,1).equals(loc2) || loc.add(0,0,-2).equals(loc2) || loc.add(1,0,1).equals(loc2) || loc.add(-2,0,0).equals(loc2)) {
                loc.add(1,0,0);
                return loc2;
            }
            loc.add(1,0,0); // Reset the location to the original
        }
        return null;
    }

    @EventHandler // this is to prevent the item from being picked up by hoppers and hopper minecarts
    public void onItemPickup(InventoryPickupItemEvent event) {
        if(event.getItem().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(),"display_item"), PersistentDataType.BOOLEAN)) {
            event.setCancelled(true);
        }
    }

}
