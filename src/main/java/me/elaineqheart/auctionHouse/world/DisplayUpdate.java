package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.world.files.CustomConfigDisplayLocations;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DisplayUpdate implements Runnable{
    @Override
    public void run() {
        for(Integer display : displays) {
            Location loc = displayLocations.get(display);
            BlockDisplay entity = displayEntities.get(display);

        }
    }

    public static Set<Integer> displays;
    private static HashMap<Integer,Location> displayLocations = new HashMap<>();
    private static HashMap<Integer, BlockDisplay> displayEntities = new HashMap<>();
    private static HashMap<Integer, String> displayTypes = new HashMap<>();

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
                displayLocations.put(display, loc);
                //get the block display
                BlockDisplay entity = null;
                for(Entity test : loc.getWorld().getNearbyEntities(loc,2,2,2)) {
                    if(isDisplay(test)) entity = (BlockDisplay) test;
                }
                if (entity != null) {
                    displayEntities.put(display, entity);
                    displayTypes.put(display, getEntityType(entity));
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
            return display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.STRING) ||
                   display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.STRING);
        }
        return false;
    }
    private static String getEntityType(BlockDisplay display) {
        if(display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "highest_price"), PersistentDataType.STRING)) {
            return "highest_price";
        } else if(display.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "ending_soon"), PersistentDataType.STRING)) {
            return "ending_soon";
        }
        return null;
    }

}
