package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AhConfiguration {

    private static final HashMap<Player, AhConfiguration> currentConfigurations = new HashMap<>();

    private int currentPage;
    private AuctionHouseGUI.Sort currentSort;
    private String currentSearch;
    private UUID currentPlayer;
    private boolean isAdmin;
    private View view;
    private MyAuctionsGUI.MySort myCurrentSort;
    private int myCurrentPage;
    private List<Map<?, ?>> whitelist;
    private String whitelistLetter;
    private BINFilter binFilter;
    private boolean close;

    public AhConfiguration(int currentPage, AuctionHouseGUI.Sort currentSort, String currentSearch, Player currentPlayer, boolean isAdmin) {
        this.currentPage = currentPage;
        this.currentSort = currentSort;
        this.currentSearch = currentSearch;
        this.currentPlayer = currentPlayer.getUniqueId();
        this.isAdmin = isAdmin;
    }
    public AhConfiguration() {}

    public enum View {
        ADMIN_CONFIRM,
        ADMIN_MANAGE_ITEMS,
        AUCTION_HOUSE,
        AUCTION_VIEW,
        CANCEL_AUCTION,
        COLLECT_EXPIRED_ITEM,
        COLLECT_SOLD_ITEM,
        CONFIRM_BUY,
        MY_AUCTIONS,
        MY_BIDS,
        ENDED_AUCTION
    }

    public enum BINFilter {
        ALL,
        BIN_ONLY,
        AUCTIONS_ONLY
    }

    public static AhConfiguration getInstance(Player p) {
        if (currentConfigurations.containsKey(p)) return currentConfigurations.get(p).clone();
        else return new AhConfiguration(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "", p, false);
    }
    public static void removeInstance(Player p) {
        currentConfigurations.remove(p);
    }
    public static void loadInstance(Player p, AhConfiguration c) {
        if(c == null) return;
        if(c.whitelistLetter != null) c.whitelist = ConfigManager.categories.get().getMapList(c.whitelistLetter);
        c.setPlayer(p.getUniqueId());
        currentConfigurations.put(p,c);
    }

    private static void save(AhConfiguration c) {
        currentConfigurations.put(c.getPlayer(), c.clone());
    }
    
    public AhConfiguration clone() {
        try {
            return  (AhConfiguration) super.clone();
        } catch (CloneNotSupportedException e) {
            AhConfiguration configuration = new AhConfiguration();
            configuration.currentSort = currentSort;
            configuration.currentSearch = currentSearch;
            configuration.whitelistLetter = whitelistLetter;
            if(binFilter != BINFilter.ALL )configuration.binFilter = binFilter;
            return configuration;
        }

    }

    public BINFilter getBinFilter() {
        if(binFilter == null) binFilter = BINFilter.ALL;
        return binFilter;
    }
    public void setBinFilter(BINFilter binFilter) {
        this.binFilter = binFilter;
        save(this);
    }
    public List<Map<?, ?>> getWhitelist() {return whitelist;}
    public void setWhitelist(List<Map<?, ?>> whitelist, String letter) {
        this.whitelist = whitelist;
        this.whitelistLetter = letter;
        save(this);
    }
    public int getMyCurrentPage() {return myCurrentPage;}
    public void setMyCurrentPage(int myCurrentPage) {this.myCurrentPage = myCurrentPage;}
    public MyAuctionsGUI.MySort getMyCurrentSort() {
        if(myCurrentSort == null) myCurrentSort = MyAuctionsGUI.MySort.ALL_AUCTIONS;
        return myCurrentSort;
    }
    public void setMyCurrentSort(MyAuctionsGUI.MySort myCurrentSort) {this.myCurrentSort = myCurrentSort;}
    public View getView() {return view;}
    public void setView(View view) {this.view = view;}
    public boolean isAdmin() {return isAdmin;}
    public Player getPlayer() {return Bukkit.getPlayer(currentPlayer);}
    public AhConfiguration setPlayer(UUID player) {this.currentPlayer = player; return this;}
    public String getCurrentSearch() {
        if(currentSearch == null) currentSearch = "";
        return currentSearch;
    }
    public void setCurrentSearch(String currentSearch) {
        this.currentSearch = currentSearch;
        save(this);
    }
    public AuctionHouseGUI.Sort getCurrentSort() {
        if(currentSort == null) currentSort = AuctionHouseGUI.Sort.HIGHEST_PRICE;
        return currentSort;
    }
    public void setCurrentSort(AuctionHouseGUI.Sort currentSort) {
        this.currentSort = currentSort;
        save(this);
    }
    public int getCurrentPage() {return currentPage;}
    public void setCurrentPage(int currentPage) {this.currentPage = currentPage;}
    public boolean shouldKeepOpen() {return !close;}
    public void setShouldClose(boolean shouldClose) {this.close = shouldClose;}
}
