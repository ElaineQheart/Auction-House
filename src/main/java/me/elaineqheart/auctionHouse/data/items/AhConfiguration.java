package me.elaineqheart.auctionHouse.data.items;

import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import org.bukkit.entity.Player;

public class AhConfiguration {

    public int currentPage;
    public AuctionHouseGUI.Sort currentSort;
    public String currentSearch;
    public Player currentPlayer;
    public final boolean isAdmin;
    public boolean isAuctionView;

    public AhConfiguration(int currentPage, AuctionHouseGUI.Sort currentSort, String currentSearch, Player currentPlayer, boolean isAdmin, boolean isAuctionView) {
        this.currentPage = currentPage;
        this.currentSort = currentSort;
        this.currentSearch = currentSearch;
        this.currentPlayer = currentPlayer;
        this.isAdmin = isAdmin;
        this.isAuctionView = isAuctionView;
    }

}
