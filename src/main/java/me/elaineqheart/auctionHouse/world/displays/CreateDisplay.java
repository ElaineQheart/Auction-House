package me.elaineqheart.auctionHouse.world.displays;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
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
        if (world == null) {
            AuctionHouse.getPlugin().getLogger().severe("Creating an npc failed. The world is null.");
            return;
        }
        BlockDisplay glass = (BlockDisplay) world.spawnEntity(loc, EntityType.BLOCK_DISPLAY); // creating a block
                                                                                              // display
        glass.setBlock(SettingManager.getDisplayGlass(sortType, rank));
        glass.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));
        // Set scale to 0.6
        Vector3f scale = new Vector3f(0.6f, 0.6f, 0.6f);
        // Move to the center
        Vector3f translation = new Vector3f(0.2f, 1, 0.2f);
        // No rotation
        AxisAngle4f zeroRotation = new AxisAngle4f(0, 0, 0, 0);
        glass.setTransformation(new Transformation(translation, zeroRotation, scale, zeroRotation));
        glass.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), sortType),
                PersistentDataType.INTEGER, rank);

        Interaction interaction = (Interaction) world.spawnEntity(loc.clone().add(0.5, 1, 0.5), EntityType.INTERACTION);
        interaction.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "rank"),
                PersistentDataType.INTEGER, rank); // rank #
        interaction.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(), "type"),
                PersistentDataType.STRING, sortType); // sort type
        interaction.setInteractionHeight(0.8f);
        interaction.setInteractionWidth(0.6f);
        interaction.setResponsive(true);

        placeBlocks(loc, rank, sortType);

        int displayID = 1; // default
        if (!UpdateDisplay.displays.isEmpty())
            displayID = Collections.max(UpdateDisplay.displays.keySet()) + 1; // new display ID
        assert UpdateDisplay.getYmlData() != null;
        UpdateDisplay.getYmlData().set(String.valueOf(displayID), loc); // save the location in the config
        ConfigManager.displays.save();
        UpdateDisplay.reload();
    }

    public static void placeBlocks(Location loc, int rank, String sortType) {
        org.bukkit.block.data.BlockData baseData = SettingManager.getDisplayBase(sortType, rank);
        org.bukkit.block.data.BlockData signData = SettingManager.getDisplaySign(sortType, rank);

        loc.getBlock().setBlockData(baseData, false);

        org.bukkit.block.data.BlockData eastData = signData.clone();
        if (eastData instanceof Directional dir)
            dir.setFacing(BlockFace.EAST);
        loc.clone().add(1, 0, 0).getBlock().setBlockData(eastData, false);
        Sign east = (Sign) loc.clone().add(1, 0, 0).getBlock().getState();
        east.setWaxed(true);
        east.update();

        org.bukkit.block.data.BlockData westData = signData.clone();
        if (westData instanceof Directional dir)
            dir.setFacing(BlockFace.WEST);
        loc.clone().add(-1, 0, 0).getBlock().setBlockData(westData, false);
        Sign west = (Sign) loc.clone().add(-1, 0, 0).getBlock().getState();
        west.setWaxed(true);
        west.update();

        org.bukkit.block.data.BlockData northData = signData.clone();
        if (northData instanceof Directional dir)
            dir.setFacing(BlockFace.NORTH);
        loc.clone().add(0, 0, -1).getBlock().setBlockData(northData, false);
        Sign north = (Sign) loc.clone().add(0, 0, -1).getBlock().getState();
        north.setWaxed(true);
        north.update();

        org.bukkit.block.data.BlockData southData = signData.clone();
        if (southData instanceof Directional dir)
            dir.setFacing(BlockFace.SOUTH);
        loc.clone().add(0, 0, 1).getBlock().setBlockData(southData, false);
        Sign south = (Sign) loc.clone().add(0, 0, 1).getBlock().getState();
        south.setWaxed(true);
        south.update();
    }

    public static boolean notEnoughSpace(Location loc) {
        if (!loc.getBlock().isEmpty())
            return true;
        if (!loc.add(0, 0, 1).getBlock().isEmpty())
            return true;
        if (!loc.add(0, 0, -2).getBlock().isEmpty())
            return true;
        if (!loc.add(1, 0, 1).getBlock().isEmpty())
            return true;
        if (!loc.add(-2, 0, 0).getBlock().isEmpty())
            return true;
        return !loc.add(1, 1, 0).getBlock().isEmpty();
    }

}
