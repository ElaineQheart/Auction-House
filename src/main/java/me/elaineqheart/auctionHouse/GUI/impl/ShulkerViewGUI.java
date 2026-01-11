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
    private final AhConfiguration.View goBackTo;

    public ShulkerViewGUI(ItemNote note, AhConfiguration configuration, AhConfiguration.View goBackTo) {
        super(((ShulkerBox) ((BlockStateMeta) note.getItem().getItemMeta()).getBlockState()).getInventory());
        c = configuration;
        this.note = note;
        this.goBackTo = goBackTo;
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
            Sounds.closeShulker(event);
            switch (c.getView()) {
                case MY_AUCTIONS -> AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), p);
                case AUCTION_HOUSE -> AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
                case CANCEL_AUCTION -> AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note, c), p);
                case COLLECT_SOLD_ITEM -> AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note, c), p);
                case COLLECT_EXPIRED_ITEM -> AuctionHouse.getGuiManager().openGUI(new CollectExpiredItemGUI(note, c), p);
                case CONFIRM_BUY, AUCTION_VIEW -> AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, goBackTo), p);
                case ADMIN_CONFIRM, ADMIN_MANAGE_ITEMS -> AuctionHouse.getGuiManager().openGUI(new AdminManageItemsGUI(note, c), p);
                case MY_BIDS -> AuctionHouse.getGuiManager().openGUI(new MyBidsGUI(c, 0), p);
                case ENDED_AUCTION -> AuctionHouse.getGuiManager().openGUI(new EndedAuctionGUI(note, c, goBackTo), p);
            }
        },0);
    }

    @Override
    protected Inventory createInventory() {return null;}
}
