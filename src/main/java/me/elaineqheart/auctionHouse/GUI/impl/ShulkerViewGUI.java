package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
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
            switch (c.view) {
                case MY_AUCTIONS -> AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), c.currentPlayer);
                case AUCTION_HOUSE -> AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                case CANCEL_AUCTION -> AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note, c), c.currentPlayer);
                case COLLECT_SOLD_ITEM -> AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note, c), c.currentPlayer);
                case COLLECT_EXPIRED_ITEM -> AuctionHouse.getGuiManager().openGUI(new CollectExpiredItemGUI(note, c), c.currentPlayer);
                case CONFIRM_BUY, AUCTION_VIEW -> AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c), c.currentPlayer);
                case ADMIN_CONFIRM, ADMIN_MANAGE_ITEMS -> AuctionHouse.getGuiManager().openGUI(new AdminManageItemsGUI(note, c), c.currentPlayer);
            }
        },0);
    }

    @Override
    protected Inventory createInventory() {return null;}
}
