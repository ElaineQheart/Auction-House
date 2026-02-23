package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionViewGUI;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;

public class DisplayListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDisplayBreak(BlockBreakEvent event) {
        Location loc = event.getBlock().getLocation();
        Player p = event.getPlayer();
        Location displayLoc = isProtected(loc);
        if (displayLoc == null) {
            return; // Not a display location
        }

        boolean isBaseBlock = (loc.getBlockX() == displayLoc.getBlockX() &&
                loc.getBlockY() == displayLoc.getBlockY() &&
                loc.getBlockZ() == displayLoc.getBlockZ());

        if (isBaseBlock) {
            if (p.getGameMode() == GameMode.CREATIVE && p.hasPermission(SettingManager.permissionModerate)
                    && p.isSneaking()) {
                event.setCancelled(true);
                UpdateDisplay.removeDisplay(displayLoc, true);
            } else {
                event.setCancelled(true);
                if (p.hasPermission(SettingManager.permissionModerate)) {
                    p.sendMessage(M.getFormatted("world.displays.break-instruction"));
                }
            }
        } else { // Sign block
            event.setCancelled(true);
            if (p.hasPermission(SettingManager.permissionModerate)) {
                p.sendMessage(M.getFormatted("world.displays.break-instruction"));
            }
        }
    }

    private Location isProtected(Location inputLoc) {
        for (Location baseLoc : UpdateDisplay.locations.keySet()) {
            if (inputLoc.getWorld() != baseLoc.getWorld())
                continue;
            if (inputLoc.getBlockY() != baseLoc.getBlockY())
                continue;
            int dx = Math.abs(inputLoc.getBlockX() - baseLoc.getBlockX());
            int dz = Math.abs(inputLoc.getBlockZ() - baseLoc.getBlockZ());
            if (dx + dz <= 1) { // Base block and adjacent blocks
                return baseLoc;
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST) // open the auction house when the display is clicked
    public void onDisplayClick(PlayerInteractAtEntityEvent event) {
        if (event.getRightClicked().getPersistentDataContainer()
                .has(new NamespacedKey(AuctionHouse.getPlugin(), "type"), PersistentDataType.STRING)) {
            event.setCancelled(true);
            Player p = event.getPlayer();
            String type = event.getRightClicked().getPersistentDataContainer()
                    .get(new NamespacedKey(AuctionHouse.getPlugin(), "type"), PersistentDataType.STRING);
            if (type == null)
                throw new RuntimeException("The display type is null. This should never happen.");
            int rank = event.getRightClicked().getPersistentDataContainer()
                    .get(new NamespacedKey(AuctionHouse.getPlugin(), "rank"), PersistentDataType.INTEGER);
            ItemNote note = UpdateDisplay.getNote(type, rank);
            if (note != null) {
                p.playSound(p, Sound.UI_STONECUTTER_SELECT_RECIPE, 0.2f, 1);
                AhConfiguration configuration = new AhConfiguration(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p,
                        false);
                AuctionHouse.getGuiManager()
                        .openGUI(new AuctionViewGUI(note, configuration, 0, AhConfiguration.View.AUCTION_HOUSE), p);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST) // open the auction house when interacting directly with the blocks
                                                    // of the
    // display
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK)
            return;
        if (event.getClickedBlock() == null)
            return;

        Location loc = event.getClickedBlock().getLocation();
        Location displayLoc = isProtected(loc);
        if (displayLoc == null)
            return;

        event.setCancelled(true);

        Player p = event.getPlayer();

        // Find the block display entity to get type and rank
        Integer rank = null;
        String type = null;
        assert displayLoc.getWorld() != null;
        for (Entity test : displayLoc.getWorld().getNearbyEntities(displayLoc, 1, 1, 1)) {
            if (UpdateDisplay.isDisplayGlass(test)) {
                if (test.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"),
                        PersistentDataType.INTEGER)) {
                    type = "highest_price";
                    rank = test.getPersistentDataContainer().get(
                            new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.INTEGER);
                } else if (test.getPersistentDataContainer()
                        .has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.INTEGER)) {
                    type = "ending_soon";
                    rank = test.getPersistentDataContainer().get(
                            new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.INTEGER);
                }
                break;
            }
        }

        if (type != null && rank != null) {
            ItemNote note = UpdateDisplay.getNote(type, rank);
            if (note != null) {
                p.playSound(p, Sound.UI_STONECUTTER_SELECT_RECIPE, 0.2f, 1);
                AhConfiguration configuration = new AhConfiguration(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p,
                        false);
                AuctionHouse.getGuiManager()
                        .openGUI(new AuctionViewGUI(note, configuration, 0, AhConfiguration.View.AUCTION_HOUSE), p);
            }
        }
    }

    // prevent the tuff block to be moved by pistons
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPiston(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            Location loc = block.getLocation();
            for (Location loc2 : UpdateDisplay.locations.keySet()) {
                if (loc.equals(loc2)) {
                    event.setCancelled(true);
                    return; // Prevent piston movement if a display is present
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPiston(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            Location loc = block.getLocation();
            for (Location loc2 : UpdateDisplay.locations.keySet()) {
                if (loc.equals(loc2)) {
                    event.setCancelled(true);
                    return; // Prevent piston movement if a display is present
                }
            }
        }
    }

    // protect the tuff block from explosions
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Location loc = block.getLocation();
            return isProtected(loc) != null;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onExplosion(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> {
            Location loc = block.getLocation();
            return isProtected(loc) != null;
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTeleport(EntityTeleportEvent event) {
        Entity entity = event.getEntity();
        if (UpdateDisplay.isDisplayGlass(entity) || UpdateDisplay.isDisplayInteraction(entity)) {
            event.setCancelled(true);
        }
    }

}
