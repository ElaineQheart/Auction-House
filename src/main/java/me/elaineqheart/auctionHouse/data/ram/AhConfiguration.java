package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class AhConfiguration {

    public int currentPage;
    public AuctionHouseGUI.Sort currentSort;
    public String currentSearch;
    public Player currentPlayer;
    public final boolean isAdmin;
    public View view;
    public MyAuctionsGUI.MySort myCurrentSort;
    public int myCurrentPage;
    public List<Map<?, ?>> whitelist;

    public AhConfiguration(int currentPage, AuctionHouseGUI.Sort currentSort, String currentSearch, Player currentPlayer, boolean isAdmin) {
        this.currentPage = currentPage;
        this.currentSort = currentSort;
        this.currentSearch = currentSearch;
        this.currentPlayer = currentPlayer;
        this.isAdmin = isAdmin;
    }

    public enum View {
        ADMIN_CONFIRM,
        ADMIN_MANAGE_ITEMS,
        AUCTION_HOUSE,
        AUCTION_VIEW,
        CANCEL_AUCTION,
        COLLECT_EXPIRED_ITEM,
        COLLECT_SOLD_ITEM,
        CONFIRM_BUY,
        MY_AUCTIONS
    }

}
