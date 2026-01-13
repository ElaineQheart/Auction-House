package me.elaineqheart.auctionHouse.data.persistentStorage;

import me.elaineqheart.auctionHouse.data.persistentStorage.json.JsonNoteStorage;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ItemNoteStorage {

    public static void createNote(Player p, ItemStack item, double price, boolean isBINAuction) {
        //if(r()) RedisNoteStorage.createNote(p, item, price); else
        JsonNoteStorage.createNote(p, item, price, isBINAuction);
    }

    public static void saveNotes() throws IOException {
        //if(!r())
        JsonNoteStorage.saveNotes();
    }

    public static void loadNotes() throws IOException {
        //if(!r())
        JsonNoteStorage.loadNotes();
    }

    public static void deleteNote(ItemNote note) {
        //if(r()) RedisNoteStorage.deleteNote(note.getNoteID()); else
        JsonNoteStorage.deleteNote(note);
    }

    public static void setBuyerName(ItemNote note, String buyerName) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "buyerName", buyerName); else
        note.setBuyerName(buyerName);
    }
    public static void setSold(ItemNote note, boolean isSold) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "isSold", isSold); else
        note.setSold(isSold);
    }
    public static void setAdminMessage(ItemNote note, String adminMessage) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "adminMessage", adminMessage); else
        note.setAdminMessage(adminMessage);
    }
    public static void setItem(ItemNote note, ItemStack item) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "itemData", ItemStackConverter.encode(item)); else
        note.setItem(item);
    }
    public static void setAuctionTime(ItemNote note, long time) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "auctionTime", time); else
        note.setAuctionTime(time);
    }
    public static void setPartiallySoldAmountLeft(ItemNote note, int amount) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "partiallySoldAmountLeft", amount); else
        note.setPartiallySoldAmountLeft(amount);
    }
    public static void setPrice(ItemNote note, double amount) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "price", amount); else
        note.setPrice(amount);
    }
    public static void addBid(ItemNote note, Player player, double amount) {
        note.addBid(player, amount);
    }
    public static void removeBid(Player player, ItemNote note) {
        note.removeBid(player);
        AuctionHouseStorage.removeBid(player.getUniqueId(), note.getNoteID());
    }

    //public static boolean r() {return SettingManager.useRedis;}

    public enum SortMode {
        NAME,
        PRICE_ASC,
        PRICE_DESC,
        DATE
    }

    public static void purge() {
        //if(r()) RedisNoteStorage.purge();else
        JsonNoteStorage.purge();
    }

}
