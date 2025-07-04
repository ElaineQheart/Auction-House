package me.elaineqheart.auctionHouse.world;

import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataType;

public class CreateNPC{

    public static void createAuctionMaster(Location loc, String facing) {
        World world = loc.getWorld();
        if(world == null) { AuctionHouse.getPlugin().getLogger().severe("Creating an npc failed. The world is null."); return; }
        int rotation = 0; //default rotation is south
        switch (facing) {
            case "north" -> rotation = 180;
            case "west" -> rotation = 90;
            case "east" -> rotation = -90;
        }
        loc.setYaw(rotation);
        ArmorStand stand = (ArmorStand) world.spawnEntity(loc.add(0,-1,0), EntityType.ARMOR_STAND); //creating an armor stand

        stand.setSmall(true);
        stand.setInvulnerable(true);
        stand.setBasePlate(false);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setInvisible(true);
        stand.setAI(false);
        stand.setSilent(true);
        stand.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "auction_stand"), PersistentDataType.BOOLEAN, true);

        Villager npc = (Villager) world.spawnEntity(loc, EntityType.VILLAGER); //creating a villager
        stand.addPassenger(npc);
        npc.setProfession(Villager.Profession.SHEPHERD);
        npc.setBreed(false);
        npc.setHealth(1);
        npc.setVillagerLevel(5);
        npc.setVillagerType(Villager.Type.JUNGLE);
        npc.setAdult();
        npc.setCanPickupItems(false);
        npc.setCustomName(ChatColor.GOLD + "Auction Master");
        npc.setCustomNameVisible(true);
        npc.setCollidable(false);
        npc.setGravity(false);
        npc.setInvulnerable(true);
        npc.setSilent(true);
        //this is to identify the npc
        npc.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "auction_master"), PersistentDataType.BOOLEAN, true);
    }

    public static void removeAuctionMaster(Villager npc) {
        if(npc == null) return;
        for(Entity entity : npc.getNearbyEntities(1,1,1)) {
            if(entity instanceof ArmorStand stand) {
                if(stand.getPersistentDataContainer().has(new NamespacedKey(AuctionHouse.getPlugin(), "auction_stand"), PersistentDataType.BOOLEAN)) {
                    stand.remove();
                }
            }
        }
        npc.remove();
    }
    public static void removeAuctionMaster(ArmorStand stand) {
        if(stand == null) return;
        Villager npc = (Villager) stand.getPassengers().getFirst();
        npc.remove();
        stand.remove();
    }

}
