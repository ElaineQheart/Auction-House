package me.elaineqheart.auctionHouse.ah;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

public class ItemNote {

    private final String playerName;
    private String buyerName;
    private final UUID playerUUID;
    private final int price;
    private Date dateCreated;
    private String itemData;
    private boolean isSold;
    private final UUID noteID = UUID.randomUUID();
    private String adminMessage;

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
    public long timeLeft(){
        // +30 seconds wait time until the item is up on auction
        return 60*60*48 + 30 - (new Date().getTime() - dateCreated.getTime())/1000; // devided by 1000 to get seconds
    }
    public String getTimeLeft(Long timeLeft){ //output example: 4h 23m 59s
        String s;
        String m;
        String h;
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
    public String getTimeLeftTrimmed(long timeLeft) { //output example: 4h
        if(timeLeft < 60) {
            return timeLeft + "s";
        } else if(timeLeft < 60*60) {
            return (int)(timeLeft/60) + "m";
        } else {
            return (int)(timeLeft/60/60) + "h";
        }
    }
    public boolean isExpired(){
        return timeLeft()<0;
    }

    public boolean isOnWaitingList() {
        return timeLeft() > 60*60*48;
    }

    public boolean canAfford(double coins){
        return coins >= price;
    }

    public String[] getSearchIndex() {
        ArrayList<String> index = new ArrayList<>();
        index.add(getItem().toString().toLowerCase());
        ItemStack item = getItem();
        ItemMeta meta = item.getItemMeta();
        if(meta!=null) {
            for(Enchantment enchant : meta.getEnchants().keySet()){
                index.add(enchant.toString().toLowerCase());
            }
            index.add(meta.getDisplayName().toLowerCase());
            if(meta.getLore()!=null){
                for(String lore : meta.getLore()){
                    index.add(lore.toLowerCase());
                }
            }
        }
        return index.toArray(index.toArray(new String[0]));
    }

    //Getters and Setters
    public String getPlayerName() {return playerName;}
    public String getBuyerName() {return buyerName;}
    public UUID getPlayerUUID() {return playerUUID;}
    public Date getDateCreated() {return dateCreated;}
    public int getPrice() {return price;}
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
    public String getAdminMessage() {return adminMessage;}
    public UUID getNoteID() {return noteID;}

    public void setBuyerName(String buyerName) {this.buyerName = buyerName;}
    public void setSold(boolean isSold) {this.isSold = isSold;}
    public void setAdminMessage(String adminMessage) {this.adminMessage = adminMessage;}
    public void setItem(ItemStack item) {this.itemData = ItemStackConverter.encode(item);}
    public void setDateCreated(Date dateCreated) {this.dateCreated = dateCreated;}
}
