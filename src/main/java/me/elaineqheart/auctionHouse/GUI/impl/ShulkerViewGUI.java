package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerViewGUI extends InventoryGUI {

    private final AhConfiguration c;
    private final ItemNote note;

    public ShulkerViewGUI(ItemNote note, AhConfiguration configuration) {
        super(((ShulkerBox) ((BlockStateMeta) note.getItem().getItemMeta()).getBlockState()).getInventory());
        c = configuration;
        this.note = note;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
            Sounds.closeShulker(event);
            if(c.isAuctionView) {
                c.isAuctionView = false;
                AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c), p);
            } else if(c.myCurrentSort!=null) {
                AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c.myCurrentPage, c.myCurrentSort, c), p);
                c.myCurrentSort = null;
            } else {
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
            }
        },0);
    }

    @Override
    protected Inventory createInventory() {return null;}
}
