package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionViewGUI;
import me.elaineqheart.auctionHouse.data.SettingManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityRemoveEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class DisplayListener implements Listener {

    @EventHandler
    public void onDisplayBreak(BlockBreakEvent event) {
        if(!event.getBlock().getType().equals(Material.CHISELED_TUFF_BRICKS) && !event.getBlock().getType().equals(Material.DARK_OAK_WALL_SIGN)) {
            return; // Not a display block
        }
        Location loc = event.getBlock().getLocation();
        Player p = event.getPlayer();
        Location displayLoc = isProtected(loc);
        if(displayLoc == null) {
            return; // Not a display location
        }

        if(!p.getGameMode().equals(GameMode.CREATIVE) || !p.hasPermission(SettingManager.permissionModerate)) {
            event.setCancelled(true);
            return;
        }
        UpdateDisplay.removeDisplay(displayLoc);
    }

    private Location isProtected(Location loc) {
        for (Location loc2 : UpdateDisplay.locations.keySet()) {
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
            ItemNote note = UpdateDisplay.getNote(type,rank);
            if(note != null) {
                p.playSound(p, Sound.UI_STONECUTTER_SELECT_RECIPE,0.2f,1);
                AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, p), p);
            }
        }
    }

    //prevent the tuff block to be moved by pistons
    @EventHandler
    public void onPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Location loc = block.getLocation();
            for (Location loc2 : UpdateDisplay.locations.keySet()) {
                if(loc.equals(loc2)) {
                    event.setCancelled(true);
                    return; // Prevent piston movement if a display is present
                }
            }
        }
    }
    @EventHandler
    public void onPiston(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            Location loc = block.getLocation();
            for (Location loc2 : UpdateDisplay.locations.keySet()) {
                if(loc.equals(loc2)) {
                    event.setCancelled(true);
                    return; // Prevent piston movement if a display is present
                }
            }
        }
    }

    //protect the tuff block from explosions
    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Location loc = block.getLocation();
            return isProtected(loc) != null;
        });
    }
    @EventHandler
    public void onExplosion(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Location loc = block.getLocation();
            return isProtected(loc) != null;
        });
    }

    @EventHandler
    public void onKill(EntityRemoveEvent event) { //this event can cause an infinite loop when the display is removed in the code again
        Entity entity = event.getEntity();
        if(!entity.isValid()) return;
        if(entity.isDead()) return;
        if(UpdateDisplay.isDisplayGlass(entity)) {
            Location loc = entity.getLocation();
            UpdateDisplay.safeRemoveInteraction(loc); // safety measurement, in case both entities are removed at the same time
            UpdateDisplay.removeDisplay(loc);
        }
        if(UpdateDisplay.isDisplayInteraction(entity)) {
            Location loc = entity.getLocation().add(-0.5,-1,-0.5);
            UpdateDisplay.removeDisplay(loc);
        }
    }

    @EventHandler
    public void onTeleport(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if(UpdateDisplay.isDisplayGlass(entity) || UpdateDisplay.isDisplayInteraction(entity)) {
            event.setCancelled(true);
        }
    }

}
