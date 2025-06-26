package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.ah.ItemNote;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.ah.SettingManager;
import me.elaineqheart.auctionHouse.world.files.CustomConfigDisplayLocations;
import org.bukkit.*;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DisplayUpdate implements Runnable{
    @Override
    public void run() {
        for(Integer display : displays) {
            Location loc = displayLocationsReverse.get(display);
            BlockDisplay entity = displayEntities.get(display);
            String type = displayTypes.get(display);
            int rank = entity.getPersistentDataContainer().get(new NamespacedKey(AuctionHouse.getPlugin(), type), PersistentDataType.INTEGER);

            ItemNote note = null;
            if(type.equals("highest_price")) {
                Map.Entry<ItemNote, Integer> entry = ItemNoteStorageUtil.sortedHighestPrice().entrySet().stream().skip(rank-1).findFirst().orElse(null);
                if(entry == null) continue;
                note = entry.getKey();
            } else if (type.equals("ending_soon")) {
                Map.Entry<ItemNote, Long> entry = ItemNoteStorageUtil.sortedDateCreated().entrySet().stream().skip(rank-1).findFirst().orElse(null);
                if(entry == null) continue;
                note = entry.getKey();
            } else {continue;}
            String price = note.getPriceTrimmed();
            String time = note.getTimeLeftTrimmed(note.timeLeft());
            String playerName = note.getPlayerName();
            ItemStack item = note.getItem();
            String itemName = item.getItemMeta() != null && item.getItemMeta().hasDisplayName() ?
                    item.getItemMeta().getDisplayName() : item.getItemMeta().getItemName();


            Sign east = (Sign) loc.add(1,0,0).getBlock().getState();
            Sign west = (Sign) loc.add(-2,0,0).getBlock().getState();
            Sign north = (Sign) loc.add(1,0,-1).getBlock().getState();
            Sign south = (Sign) loc.add(0,0,2).getBlock().getState();
            loc.add(0,0,-1);
            Sign[] signs = {east, west, north, south};
            for(Sign sign : signs) {
                sign.getSide(Side.FRONT).setLine(0, ChatColor.GOLD + price + SettingManager.currencySymbol);
                sign.getSide(Side.FRONT).setLine(1, ChatColor.YELLOW + time);
                sign.getSide(Side.FRONT).setLine(3, ChatColor.LIGHT_PURPLE + "[CLICK]");
                sign.update();
            }

        }
    }

    public static Set<Integer> displays;
    private static final HashMap<Integer, Location> displayLocationsReverse = new HashMap<>();
    public static final HashMap<Location, Integer> locations = new HashMap<>();
    private static final HashMap<Integer, BlockDisplay> displayEntities = new HashMap<>();
    private static final HashMap<Integer, String> displayTypes = new HashMap<>();

    public static void init() {
        reload();
        TaskManager.addTaskID(UUID.randomUUID(),Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), new DisplayUpdate(), 0, 20).getTaskId());
    }
    public static void reload() {
        displays = CustomConfigDisplayLocations.get().getKeys(false).stream()
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        for(Integer display : displays) { //find the data for each display
            Location loc = CustomConfigDisplayLocations.get().getLocation(String.valueOf(display));
            if (loc != null && loc.getWorld() != null) {
                displayLocationsReverse.put(display, loc);
                locations.put(loc, display);
                //get the block display
                BlockDisplay entity = null;
                for(Entity test : loc.getWorld().getNearbyEntities(loc,2,2,2)) {
                    if(isDisplay(test)) entity = (BlockDisplay) test;
                }
                if (entity != null) {
                    displayEntities.put(display, entity);
                    displayTypes.put(display, getType(entity));
                } else {
                    AuctionHouse.getPlugin().getLogger().warning("Display entity with ID " + display + " not found in world.");
                }
            } else {
                AuctionHouse.getPlugin().getLogger().warning("Display location for ID " + display + " is null.");
            }
        }
    }

    private static boolean isDisplay(Entity entity) {
        if(entity instanceof BlockDisplay display) {
            return display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.INTEGER) ||
                   display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.INTEGER);
        }
        return false;
    }

    private static String getType(BlockDisplay display) {
        if(display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.INTEGER)) {
            return "highest_price";
        } else if(display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.INTEGER)) {
            return "ending_soon";
        }
        return null;
    }

    public static void removeDisplay(Location loc) {
        Integer display = locations.get(loc);
        loc.add(1,0,0).getBlock().setType(Material.AIR);
        loc.add(-2,0,0).getBlock().setType(Material.AIR);
        loc.add(1,0,-1).getBlock().setType(Material.AIR);
        loc.add(0,0,2).getBlock().setType(Material.AIR);
        loc.add(0,0,-1).getBlock().setType(Material.AIR);
        if(display != null) {
            displayLocationsReverse.remove(display);
            locations.remove(loc);
            BlockDisplay entity = displayEntities.remove(display);
            if(entity != null) {
                entity.remove();
            }
            displayTypes.remove(display);
            CustomConfigDisplayLocations.get().set(String.valueOf(display), null);
            CustomConfigDisplayLocations.save();
            reload();
        } else {
            AuctionHouse.getPlugin().getLogger().warning("Display at location " + loc + " not found.");
        }
    }

}
