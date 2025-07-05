package me.elaineqheart.auctionHouse.world;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Item;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;

public class DisplayNote {

    public Location location;
    public BlockDisplay glassBlock;
    public String type;
    public ItemStack itemStack;
    public Item itemEntity;
    public TextDisplay text;
    public String itemName;
    public String playerName;
    public boolean reloaded;

    public DisplayNote() {}

}
