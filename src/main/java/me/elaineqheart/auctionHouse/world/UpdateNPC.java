package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.world.files.DisplaysConfig;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.UUID;

public class UpdateNPC implements Runnable{
    @Override
    public void run() {
        for(Location loc : locationSet.keySet()) {
            int key = locationSet.get(loc);
            ArmorStand stand = npcs.get(key);
            if(stand == null) { //skip unloaded chunks
                getStand(loc, key);
                continue;
            }
            if(stand.isDead()) continue;
            if(stand.getLocation().distance(loc) > 0.5) { //if the stand is not at the correct location, teleport it
                Block block = loc.getBlock();
                loc.getBlock().setType(Material.AIR);
                boolean success = stand.teleport(loc);
                System.out.println("Teleported: " + success);
                loc.getBlock().setBlockData(block.getBlockData());
            }
        }
    }

    public static final HashMap<Location, Integer> locationSet = new HashMap<>();
    public static final HashMap<Integer, ArmorStand> npcs = new HashMap<>();
    public static final ConfigurationSection ymlData = DisplaysConfig.get().getConfigurationSection("npc");

    public static void init() {
        reload();
        TaskManager.addTaskID(UUID.randomUUID(), Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), new UpdateNPC(), 0, 20).getTaskId());
    }

    public static void reload() {
        locationSet.clear();
        npcs.clear();
        for(String key : ymlData.getKeys(false)) {
            Location loc = (Location) ymlData.get(key);
            if(loc == null) continue;
            locationSet.put(loc, Integer.parseInt(key));
            getStand(loc, Integer.parseInt(key));
        }
    }

    private static void getStand(Location loc, int key) {
        assert loc.getWorld() != null;
        Entity entity = loc.getWorld().getNearbyEntities(loc, 1, 1, 1).stream()
                .filter(e -> e.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_stand"), PersistentDataType.BOOLEAN))
                .findFirst().orElse(null);
        if(entity == null) return; //no armor stand found
        if(entity instanceof ArmorStand stand) {
            npcs.put(key, stand);
        }
    }
}
