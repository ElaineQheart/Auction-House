package me.elaineqheart.auctionHouse.data.items;

import me.elaineqheart.auctionHouse.data.Permissions;
import me.elaineqheart.auctionHouse.data.yml.SettingManager;
import org.bukkit.Bukkit;
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
