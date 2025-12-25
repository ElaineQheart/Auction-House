package me.elaineqheart.auctionHouse.data.persistentStorage;

import me.elaineqheart.auctionHouse.data.items.ItemStackConverter;
import me.elaineqheart.auctionHouse.data.persistentStorage.json.JsonNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.redis.RedisNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemNoteStorage {

    public static void createNote(Player p, ItemStack item, int price) {
        if(r()) RedisNoteStorage.createNote(p, item, price);
        else JsonNoteStorage.createNote(p, item, price);
    }

    public static void saveNotes() throws IOException {
        if(!r()) JsonNoteStorage.saveNotes();
    }

    public static void loadNotes() throws IOException {
        if(!r()) JsonNoteStorage.loadNotes();
    }

    public static ItemNote getNote(String noteID) {
        if(r()) return RedisNoteStorage.getNote(UUID.fromString(noteID));
        else return JsonNoteStorage.getNote(noteID);
    }

    public static void deleteNote(ItemNote note) {
        if(r()) RedisNoteStorage.deleteNote(note.getNoteID());
        else JsonNoteStorage.deleteNote(note);
    }

    public static int numberOfAuctions(Player p) {
        if(r()) return RedisNoteStorage.numberOfAuctions(p);
        else return JsonNoteStorage.numberOfAuctions(p);
    }

    public static int numberOfAuctions() {
        if(r()) return RedisNoteStorage.numberOfAuctions();
        else return JsonNoteStorage.numberOfAuctions();
    }


    public static void setBuyerName(ItemNote note, String buyerName) {
        if(r()) RedisNoteStorage.updateField(note.getNoteID(), "buyerName", buyerName);
        note.setBuyerName(buyerName);
    }
    public static void setSold(ItemNote note, boolean isSold) {
        if(r()) RedisNoteStorage.updateField(note.getNoteID(), "isSold", isSold);
        note.setSold(isSold);
    }
    public static void setAdminMessage(ItemNote note, String adminMessage) {
        if(r()) RedisNoteStorage.updateField(note.getNoteID(), "adminMessage", adminMessage);
        note.setAdminMessage(adminMessage);
    }
    public static void setItem(ItemNote note, ItemStack item) {
        if(r()) RedisNoteStorage.updateField(note.getNoteID(), "itemData", ItemStackConverter.encode(item));
        note.setItem(item);
    }
    public static void setAuctionTime(ItemNote note, long time) {
        if(r()) RedisNoteStorage.updateField(note.getNoteID(), "auctionTime", time);
        note.setAuctionTime(time);
    }
    public static void setPartiallySoldAmountLeft(ItemNote note, int amount) {
        if(r()) RedisNoteStorage.updateField(note.getNoteID(), "partiallySoldAmountLeft", amount);
        note.setPartiallySoldAmountLeft(amount);
    }
    public static void setPrice(ItemNote note, double amount) {
        if(r()) RedisNoteStorage.updateField(note.getNoteID(), "price", amount);
        note.setPrice(amount);
    }

    public static boolean r() {return SettingManager.useRedis;}

    public enum SortMode {
        NAME,
        PRICE_ASC,
        PRICE_DESC,
        DATE
    }

    public static List<ItemNote> getSortedList(SortMode mode, String search){
        if(r()) return new ArrayList<>(); //RedisNoteStorage.getNotes(mode, start, stop, search);
        else return JsonNoteStorage.getSortedList(mode, search);
    }

    public static List<ItemNote> mySortedDateCreated(UUID playerID){
        if(r()) return RedisNoteStorage.mySortedDateCreated(playerID);
        else return JsonNoteStorage.mySortedDateCreated(playerID);
    }

    public static void purge() {
        if(r()) RedisNoteStorage.purge();
        else JsonNoteStorage.purge();
    }

}
