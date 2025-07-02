package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionViewGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.Permissions;
import me.elaineqheart.auctionHouse.ah.ItemNote;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class DisplayListener implements Listener {

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

    @EventHandler //open the auction house when the display is clicked
    public void onDisplayClick(PlayerInteractAtEntityEvent event) {
        if(event.getRightClicked().getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "type"), PersistentDataType.STRING)) {
            Player p = event.getPlayer();
            String type = event.getRightClicked().getPersistentDataContainer().get(new NamespacedKey(AuctionHouse.getPlugin(), "type"), PersistentDataType.STRING);
            if(type == null) throw new RuntimeException("The display type is null. This should never happen.");
            int rank = event.getRightClicked().getPersistentDataContainer().get(new NamespacedKey(AuctionHouse.getPlugin(), "rank"), PersistentDataType.INTEGER);
            ItemNote note = DisplayUpdate.getNote(type,rank);
            if(note != null) {
                p.playSound(p, Sound.UI_STONECUTTER_SELECT_RECIPE,0.2f,1);
                AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, p), p);
            }
        }
    }

}
