package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.BlockStateMeta;

public class ShulkerViewGUI extends InventoryGUI {

    private final ItemNote note;
    private final int currentPage;
    private final AuctionHouseGUI.Sort currentSort;
    private final String currentSearch;
    private final boolean isAdmin;

    public ShulkerViewGUI(ItemNote note, int page, AuctionHouseGUI.Sort sort, String search, boolean isAdmin) {
        super(((ShulkerBox) ((BlockStateMeta) note.getItem().getItemMeta()).getBlockState()).getInventory());
        this.note = note;
        this.currentPage = page;
        this.currentSort = sort;
        this.currentSearch = search;
        this.isAdmin = isAdmin;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
            Sounds.closeShulker(event);
            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPage,currentSort,currentSearch,p,isAdmin), p);
                },0);
    }

    @Override
    protected Inventory createInventory() {return null;}
}
