package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemStackConverter;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Permissions;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class ItemNote {

    private final String playerName;
    private String buyerName;
    private final UUID playerUUID;
    private double price;
    private final Date dateCreated;
    private String itemData;
    private boolean isSold;
    private int partiallySoldAmountLeft;
    private final UUID noteID;
    private String adminMessage;
    private long auctionTime;
    private String itemName;
    private final boolean isBINAuction;
    private List<Bid> bidHistory = new ArrayList<>();
    private Set<UUID> claimedPlayers = new HashSet<>(); 

    public ItemNote(Player player, ItemStack item, int price, boolean isBINAuction) {
        this.noteID = UUID.randomUUID();
        this.playerName = player.getDisplayName();
        this.buyerName = null;
        this.playerUUID = player.getUniqueId();
        this.dateCreated = new Date();
        this.itemData = ItemStackConverter.encode(item);
        this.price = price;
        this.isSold = false;
        this.auctionTime = Permissions.getAuctionDuration(player, isBINAuction);
        this.itemName = StringUtils.getItemName(item);
        this.isBINAuction = isBINAuction;
    }

    public ItemStack getItem(){
        return ItemStackConverter.decode(itemData);
    }
    public long getTimeLeft(){
        // +30 seconds [auctionSetupTime] wait time until the item is up on auction
        if(auctionTime == 0) auctionTime = Permissions.getAuctionDuration(Bukkit.getPlayer(playerUUID), isBINAuction); //backwards compatibility
        return auctionTime + SettingManager.auctionSetupTime - (new Date().getTime() - dateCreated.getTime())/1000; // divided by 1000 to get seconds
    }
    public boolean isExpired(){
        return getTimeLeft()<0;
    }

    public boolean isOnWaitingList() {
        if(auctionTime == 0) auctionTime = Permissions.getAuctionDuration(Bukkit.getPlayer(playerUUID), isBINAuction); //backwards compatibility
        return getTimeLeft() > auctionTime;
    }

    public double getCurrentPrice() {
        if(getPartiallySoldAmountLeft() == 0) return price;
        return price / getItem().getAmount() * getPartiallySoldAmountLeft();
    }
    public double getSoldPrice() {
        return partiallySoldAmountLeft == 0 ? price : price - getCurrentPrice();
    }
    public int getCurrentAmount() {
        return partiallySoldAmountLeft == 0 ? getItem().getAmount() : partiallySoldAmountLeft;
    }

    public String[] getSearchIndex() {
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        ArrayList<String> index = new ArrayList<>(Collections.singleton(item.toString().toLowerCase()));
        if(meta != null && ItemManager.isShulkerBox(item)) {
            for (ItemStack itemStack : ((ShulkerBox) ((BlockStateMeta) meta).getBlockState()).getInventory().getContents()) {
                if(itemStack != null) index.add(itemStack.toString().toLowerCase());
            }
        }
        return index.toArray(String[]::new);
    }

    //Getters and Setters
    public String getPlayerName() {return playerName;}
    public String getBuyerName() {return isBINAuction ? buyerName : getLastBidderName();}
    public UUID getPlayerUUID() {return playerUUID;}
    public Date getDateCreated() {return dateCreated;}
    public double getPrice() {return price;}
    public String getPriceTrimmed() {
        if(price < 1000) {
            return String.valueOf(price);
        } else if(price < 1000000) {
            return String.format("%.1fK", price / 1000.0);
        } else {
            return String.format("%.1fM", price / 1000000.0);
        }
    }
    public boolean isSold() {return isSold;}
    public boolean isOnAuction() {return !isSold || partiallySoldAmountLeft != 0;} //NOT INCLUDING EXPIRED
    public int getPartiallySoldAmountLeft() {return partiallySoldAmountLeft;}
    public String getAdminMessage() {return adminMessage;}
    public UUID getNoteID() {return noteID;}
    public String getItemName() {
        if (itemName == null) itemName = StringUtils.getItemName(getItem());
        return itemName;
    }
    public List<Bid> getBidHistoryList() {
        if(bidHistory == null) bidHistory = new ArrayList<>();
        return bidHistory;
    }
    public boolean hasBidHistory() {return bidHistory != null && !bidHistory.isEmpty();}
    public boolean isBINAuction() {return isBINAuction;}
    public String getLastBidderName() {return getBidHistoryList().isEmpty() ? null : getBidHistoryList().getLast().getPlayerName();}
    public UUID getLastBidder() {return getBidHistoryList().isEmpty() ? null : getBidHistoryList().getLast().getPlayerID();}
    public Set<UUID> getBidders() {
        return getBidHistoryList().stream()
                .map(Bid::getPlayerID)
                .collect(Collectors.toSet());
    }
    public double getBid(Player p) {
        return getBidHistoryList().stream()
                .filter(bid -> bid.getPlayerID().equals(p.getUniqueId()))
                .map(Bid::getPrice)
                .reduce((first, second) -> second)
                .orElse(0.0);
    }
    public Set<UUID> getClaimedPlayers() {
        if(claimedPlayers == null) claimedPlayers = new HashSet<>();
        return claimedPlayers;
    }
    public boolean canClaimBid(UUID playerID) {return !getClaimedPlayers().contains(playerID);}

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }
    public void addBid(Player player, double bid) {
        this.bidHistory.add(new Bid(player, new Date(), bid));
        this.price = bid;
        if (getTimeLeft() < SettingManager.lastBIDExtraTime) {
            auctionTime = SettingManager.lastBIDExtraTime - SettingManager.auctionSetupTime + (new Date().getTime() - dateCreated.getTime())/1000;
        }
        AuctionHouseStorage.addBid(player.getUniqueId(), noteID);
    }
    public void removeBid(Player player) {getClaimedPlayers().add(player.getUniqueId());}
    public void setSold(boolean isSold) {this.isSold = isSold;}
    public void setAdminMessage(String adminMessage) {this.adminMessage = adminMessage;}
    public void setItem(ItemStack item) {this.itemData = ItemStackConverter.encode(item);}
    public void setAuctionTime(long time) {this.auctionTime = time;}
    public void setPartiallySoldAmountLeft(int amount) {this.partiallySoldAmountLeft = amount;}
    public void setPrice(double amount) {this.price = amount;}
}
