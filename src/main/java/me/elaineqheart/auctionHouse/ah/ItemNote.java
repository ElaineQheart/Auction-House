package me.elaineqheart.auctionHouse.ah;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Date;
import java.util.UUID;

public class ItemNote {

    private final String playerName;
    private String buyerName;
    private final UUID playerUUID;
    private final int price;
    private final Date dateCreated;
    private final String itemData;
    private boolean isSold;

    public ItemNote(Player player, String itemData, int price) {
        this.playerName = player.getDisplayName();
        this.buyerName = null;
        this.playerUUID = player.getUniqueId();
        this.dateCreated = new Date();
        this.itemData = itemData;
        this.price = price;
        this.isSold = false;
    }

    public ItemStack getItem(){
        return ItemStackConverter.decode(itemData);
    }
    private long timeLeft(){
        return 60*60*48 - (new Date().getTime() - dateCreated.getTime())/1000; // devided by 1000 to get seconds
    }
    public String getTimeLeft(){
        String s;
        String m;
        String h;
        long timeLeft = timeLeft();
        int sec = (int) ((timeLeft)%60);
        if(String.valueOf(sec).length()==1) {
            s = '0' + String.valueOf(sec);
        }else{
            s = String.valueOf(sec);
        }
        int min = (int) ((timeLeft/60)%60);
        if(String.valueOf(min).length()==1) {
            m = '0' + String.valueOf(min);
        }else{
            m = String.valueOf(min);
        }
        int hours = (int) (timeLeft/60/60);
        if(String.valueOf(hours).length()==1) {
            h = '0' + String.valueOf(hours);
        }else{
            h = String.valueOf(hours);
        }
        return (ChatColor.YELLOW+h+"h "+m+"m "+s+"s");
    }
    public boolean isExpired(){
        return timeLeft()<0;
    }

    public boolean canAfford(double coins){
        return coins >= price;
    }

    //Getters and Setters
    public String getPlayerName() {return playerName;}
    public String getBuyerName() {return buyerName;}
    public UUID getPlayerUUID() {return playerUUID;}
    public Date getDateCreated() {return dateCreated;}
    public int getPrice() {return price;}
    public boolean isSold() {return isSold;}

    public void setBuyerName(String buyerName) {this.buyerName = buyerName;}
    public void setSold(boolean isSold) {this.isSold = isSold;}
}
