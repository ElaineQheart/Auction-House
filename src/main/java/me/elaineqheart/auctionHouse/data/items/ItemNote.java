package me.elaineqheart.auctionHouse.data.items;

import me.elaineqheart.auctionHouse.data.Permissions;
import me.elaineqheart.auctionHouse.data.yml.SettingManager;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class ItemNote {

    private final String playerName;
    private String buyerName;
    private final UUID playerUUID;
    private double price;
    private Date dateCreated;
    private String itemData;
    private boolean isSold;
    private int partiallySoldAmountLeft;
    private final UUID noteID = UUID.randomUUID();
    private String adminMessage;
    private long auctionTime;

    public ItemNote(Player player, String itemData, int price) {
        this.playerName = player.getDisplayName();
        this.buyerName = null;
        this.playerUUID = player.getUniqueId();
        this.dateCreated = new Date();
        this.itemData = itemData;
        this.price = price;
        this.isSold = false;
        this.auctionTime = Permissions.getAuctionDuration(player);
    }

    public ItemStack getItem(){
        return ItemStackConverter.decode(itemData);
    }
    public long timeLeft(){
        // +30 seconds [auctionSetupTime] wait time until the item is up on auction
        if(auctionTime == 0) auctionTime = Permissions.getAuctionDuration(Bukkit.getPlayer(playerUUID)); //backwards compatibility
        return auctionTime + SettingManager.auctionSetupTime - (new Date().getTime() - dateCreated.getTime())/1000; // divided by 1000 to get seconds
    }
    public boolean isExpired(){
        return timeLeft()<0;
    }

    public boolean isOnWaitingList() {
        if(auctionTime == 0) auctionTime = Permissions.getAuctionDuration(Bukkit.getPlayer(playerUUID)); //backwards compatibility
        return timeLeft() > auctionTime;
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
    public String getBuyerName() {return buyerName;}
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

    public void setBuyerName(String buyerName) {this.buyerName = buyerName;}
    public void setSold(boolean isSold) {this.isSold = isSold;}
    public void setAdminMessage(String adminMessage) {this.adminMessage = adminMessage;}
    public void setItem(ItemStack item) {this.itemData = ItemStackConverter.encode(item);}
    public void setDateCreated(Date dateCreated) {this.dateCreated = dateCreated;}
    public void setPartiallySoldAmountLeft(int amount) {this.partiallySoldAmountLeft = amount;}
    public void setPrice(double amount) {this.price = amount;}
}
