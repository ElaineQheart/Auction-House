package me.elaineqheart.auctionHouse.data.persistentStorage;

import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.JsonNoteStorage;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class ItemNoteStorage {

    private static HashMap<UUID, ItemStack> items = new HashMap<>();
    public static ItemStack getItem(UUID itemNoteID) {
        return items.get(itemNoteID);
    }
    public static void addItem(UUID itemNoteID, ItemStack item) {
        items.put(itemNoteID, item);
    }
    public static void removeItem(UUID itemNoteID) {
        items.remove(itemNoteID);
    }


    public static void createNote(Player p, ItemStack item, double price, boolean isBIDAuction) {
        //if(r()) RedisNoteStorage.createNote(p, item, price); else
        JsonNoteStorage.createNote(p, item, price, isBIDAuction);
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
        removeItem(note.getNoteID());
    }

    public static void setBuyerName(ItemNote note, String buyerName, UUID playerID) {
        //if(r()) RedisNoteStorage.updateField(note.getNoteID(), "buyerName", buyerName); else
        note.setBuyerName(buyerName, playerID);
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

    public static void reload() {
        items = new HashMap<>();
    }


    public static boolean setSoldIfOnAuction(ItemNote note, Player p, int amount, double price) {
        //check synchronously when using database

        setSold(note, true);
        setBuyerName(note, p.getDisplayName(), p.getUniqueId());
        if (price != note.getPrice()) {
            if (note.getPartiallySoldAmountLeft() == 0) {
                setPartiallySoldAmountLeft(note, note.getItem().getAmount() - amount);
            } else {
                setPartiallySoldAmountLeft(note, note.getPartiallySoldAmountLeft() - amount);
            }
        }
        saveNotesWithoutCheck();
        return true;
    }

    public static boolean collectExpiredAuctionItem(ItemNote note) {
        deleteNote(note);
        saveNotesWithoutCheck();
        return true;
    }

    public static boolean collectSoldAuctionItem(ItemNote note, int itemAmount, double price) {
        if (note == null) return false;
        if (note.isBIDAuction() && note.isSold()) return false;
        if (note.getPartiallySoldAmountLeft() != 0) {
            setPrice(note, note.getPrice() - price);
            ItemStack temp = note.getItem();
            temp.setAmount(note.getItem().getAmount() - itemAmount);
            setItem(note, temp);
            if (note.getPartiallySoldAmountLeft() == note.getItem().getAmount()) {
                setPartiallySoldAmountLeft(note, 0);
                setSold(note, false);
                setBuyerName(note, null, null);
            }
        } else {
            if (!note.isBIDAuction()) deleteNote(note);
            else {
                note.setSold(true);
                AuctionHouseStorage.checkRemove(note.getNoteID());
            }
        }
        saveNotesWithoutCheck();
        return true;
    }

    public static boolean addBidIfOnAuction(ItemNote note, Player p, double price) {
        addBid(note, p, price);
        saveNotesWithoutCheck();
        return true;
    }

    public static boolean claimEndedAuctionItem(Player p, ItemNote note) {
        //check synchronously when using database

        removeBid(p, note);
        saveNotesWithoutCheck();
        return true;
    }

    public static boolean adminConfirmDeleteItem(ItemNote note, String reason) {
        setAuctionTime(note, -1);
        setAdminMessage(note, reason);
        setItem(note, ItemManager.createDirt());
        saveNotesWithoutCheck();
        return true;
    }

    public static boolean adminConfirmExpireItem(ItemNote note, String reason) {
        setAuctionTime(note, -1);
        setAdminMessage(note, reason);
        saveNotesWithoutCheck();
        return true;
    }



    private static void saveNotesWithoutCheck() {
        try {
            saveNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
