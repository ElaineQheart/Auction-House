package me.elaineqheart.auctionHouse.ah;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Date;
import java.util.UUID;

public class ItemNote {

    private String id;

    private String playerName;
    private String buyerName;
    private UUID playerUUID;
    private int price;
    private Date dateCreated;
    private String itemData;
    private boolean isSold;

    public ItemNote(Player player, String itemData, int price) {
        this.playerName = player.getDisplayName();
        this.buyerName = null;
        this.playerUUID = player.getUniqueId();
        this.dateCreated = new Date();
        this.itemData = itemData;
        this.price = price;
        this.id = UUID.randomUUID().toString();
        this.isSold = false;
    }

    public ItemStack getItem(){
        return ItemStackConverter.decode(itemData);
    }
    public String[] getSearchIndexes(){
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        System.out.println(meta);
        assert meta != null;
        return null;
    }
    private long timeLeft(){
        return 60*60*48/3600 - (new Date().getTime() - dateCreated.getTime())/1000; // devided by 1000 to get seconds
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
    public String getId() {return id;}
    public String getPlayerName() {return playerName;}
    public String getBuyerName() {return buyerName;}
    public UUID getPlayerUUID() {return playerUUID;}
    public Date getDateCreated() {return dateCreated;}
    public String getItemData() {return itemData;}
    public int getPrice() {return price;}
    public boolean isSold() {return isSold;}

    public void setPlayerName(String playerName) {this.playerName = playerName;}
    public void setBuyerName(String buyerName) {this.buyerName = buyerName;}
    public void setPlayerUUID(UUID playerUUID) {this.playerUUID = playerUUID;}
    public void setDateCreated(Date dateCreated) {this.dateCreated = dateCreated;}
    public void setItemData(String item) {this.itemData= item;}
    public void setPrice(int price) {this.price = price;}
    public void setSold(boolean isSold) {this.isSold = isSold;}
}
