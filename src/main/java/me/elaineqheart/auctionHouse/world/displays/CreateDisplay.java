package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.ConfigManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Interaction;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Collections;

public class CreateDisplay {

    public static void createDisplayHighestPrice(Location loc, int itemRank) {
        createDisplay(loc, itemRank, "highest_price");
    }

    public static void createDisplayEndingSoon(Location loc, int itemRank) {
        createDisplay(loc, itemRank, "ending_soon");
    }

    public static void createDisplay(Location loc, int rank, String sortType) {
        World world = loc.getWorld();
        if(world == null) { AuctionHouse.getPlugin().getLogger().severe("Creating an npc failed. The world is null."); return; }
        BlockDisplay glass = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY); //creating a block display
        glass.setBlock(Material.GLASS.createBlockData());
        // Set scale to 0.6
        Vector3f scale = new Vector3f(0.6f, 0.6f, 0.6f);
        // Move to the center
        Vector3f translation = new Vector3f(0.2f, 1, 0.2f);
        // No rotation
        AxisAngle4f zeroRotation = new AxisAngle4f(0, 0, 0, 0);
        glass.setTransformation(new Transformation(translation, zeroRotation, scale, zeroRotation));
        glass.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), sortType), PersistentDataType.INTEGER, rank);

        Interaction interaction = (Interaction) world.spawnEntity(loc.clone().add(0.5,1,0.5), EntityType.INTERACTION);
        interaction.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "rank"), PersistentDataType.INTEGER, rank); //rank #
        interaction.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "type"), PersistentDataType.STRING, sortType); //sort type
        interaction.setInteractionHeight(0.8f);
        interaction.setInteractionWidth(0.6f);
        interaction.setResponsive(true);

        placeBlocks(loc);

        int displayID = 1; //default
        if(!UpdateDisplay.displays.isEmpty()) displayID = Collections.max(UpdateDisplay.displays.keySet()) + 1; //new display ID
        assert UpdateDisplay.getYmlData() != null;
        UpdateDisplay.getYmlData().set(String.valueOf(displayID), loc); //save the location in the config
        ConfigManager.displays.save();
        UpdateDisplay.reload();
    }

    public static void placeBlocks(Location loc) {
        loc.getBlock().setType(Material.CHISELED_TUFF_BRICKS, false);
        loc.add(1,0,0).getBlock().setType(Material.DARK_OAK_WALL_SIGN);
        loc.add(-2,0,0).getBlock().setType(Material.DARK_OAK_WALL_SIGN);
        loc.add(1,0,-1).getBlock().setType(Material.DARK_OAK_WALL_SIGN);
        loc.add(0,0,2).getBlock().setType(Material.DARK_OAK_WALL_SIGN);
        Sign east = (Sign) loc.add(1,0,-1).getBlock().getState();
        east.setWaxed(true);
        east.update();
        Directional eastData = (Directional) east.getBlockData();
        eastData.setFacing(BlockFace.EAST);
        loc.getBlock().setBlockData(eastData);
        Sign west = (Sign) loc.add(-2,0,0).getBlock().getState();
        west.setWaxed(true);
        west.update();
        Directional westData = (Directional) west.getBlockData();
        westData.setFacing(BlockFace.WEST);
        loc.getBlock().setBlockData(westData);
        Sign north = (Sign) loc.add(1,0,-1).getBlock().getState(); //default is north
        north.setWaxed(true);
        north.update();
        Sign south = (Sign) loc.add(0,0,2).getBlock().getState();
        south.setWaxed(true);
        south.update();
        Directional southData = (Directional) south.getBlockData();
        southData.setFacing(BlockFace.SOUTH);
        loc.getBlock().setBlockData(southData);
        loc.add(0,0,-1);
    }

}
