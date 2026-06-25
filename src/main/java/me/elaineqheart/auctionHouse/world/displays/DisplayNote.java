package me.elaineqheart.auctionHouse.world.displays;

import org.bukkit.Location;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Item;
import org.bukkit.entity.TextDisplay;

import java.util.UUID;

public class DisplayNote {

    public Location location;
    public int rank;
    public String sortType;

    public UUID glassUUID;
    public UUID interactionUUID;
    public UUID itemUUID;
    public UUID textUUID;

    public transient BlockDisplay glassBlock;
    public transient Item itemEntity;
    public transient TextDisplay text;

}
