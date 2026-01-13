package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.Blacklist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AuctionHouseStorage {

    private static final ArrayList<UUID> itemNotes = new ArrayList<>();
    private static final ArrayList<UUID> sortedHighestPrice = new ArrayList<>();
    private static final ArrayList<UUID> sortedTimeLeft = new ArrayList<>();
    private static final ArrayList<UUID> sortedAlphabetical = new ArrayList<>();
    private static final HashMap<UUID, List<UUID>> sortedBids = new HashMap<>(); // player : itemNotes
    private static final HashMap<UUID, List<UUID>> sortedPlayers = new HashMap<>(); // itemNote : players
    private static final HashMap<UUID, ItemNote> notes = new HashMap<>();
    private static final HashMap<List<Map<?, ?>>, List<UUID>> categories = new HashMap<>();

    private static void addToLists(ItemNote note) {
        itemNotes.add(note.getNoteID());
        if(note.isOnAuction() && !note.isExpired()) {
            sortedHighestPrice.add(note.getNoteID());
            sortedTimeLeft.add(note.getNoteID());
            sortedAlphabetical.add(note.getNoteID());
            categories.forEach((maps, uuids) -> {
                if(!Blacklist.isBlacklisted(note.getItem(), maps)) uuids.add(note.getNoteID());
            });
        }
    }
    private static void removeFromLists(UUID id) {
        itemNotes.remove(id);
        sortedHighestPrice.remove(id);
        sortedTimeLeft.remove(id);
        sortedAlphabetical.remove(id);
        categories.forEach((maps, uuids) -> uuids.remove(id));
    }
    private static void addWhiteList(List<Map<?, ?>> whitelist) {
        categories.put(whitelist, itemNotes.stream()
                .filter(noteID -> !Blacklist.isBlacklisted(notes.get(noteID).getItem(), whitelist))
                .collect(Collectors.toList()));
    }

    public static void add(ItemNote item) {
        addToLists(item);
        notes.put(item.getNoteID(), item);
        updateSortedLists();
    }

    public static void set(ItemNote[] items) {
        clear();
        //Arrays.asList() links them
        for(ItemNote note : items) {
            if(note == null) continue;
            addToLists(note);
            notes.put(note.getNoteID(), note);
        }
        updateBids();
        updateSortedLists();
    }

    private static void clear() {
        notes.clear();
        itemNotes.clear();
        sortedHighestPrice.clear();
        sortedTimeLeft.clear();
        sortedAlphabetical.clear();
    }

    public static void remove(ItemNote item) {
        removeFromLists(item.getNoteID());
        if(!item.isBIDAuction() || !item.hasBidHistory()) notes.remove(item.getNoteID());
    }

    public static boolean canCollectBid(ItemNote item, UUID player) {return !item.getClaimedPlayers().contains(player);}

    public static void removeBid(UUID player, UUID noteID) {
        if(sortedBids.containsKey(player)) sortedBids.get(player).remove(noteID);
        if(sortedPlayers.containsKey(noteID)) {
            sortedPlayers.get(noteID).remove(player);
            checkRemove(noteID);
        }
    }
    public static void checkRemove(UUID noteID) {
        if(!notes.get(noteID).isBIDAuction()) return;
        if(sortedPlayers.get(noteID).isEmpty() && notes.get(noteID).isSold()) {
            sortedPlayers.remove(noteID);
            sortedBids.remove(notes.get(noteID).getPlayerUUID());
            removeFromLists(noteID);
            notes.remove(noteID);
        }
    }

    public static List<ItemNote> getAll() {
        return itemNotes.stream().map(notes::get).toList();
    }

    public static List<ItemNote> getSortedList(ItemNoteStorage.SortMode mode, String search, AhConfiguration.BINFilter binFilter){
        List<UUID> list = new ArrayList<>();
        switch (mode) {
            case DATE -> list = sortedTimeLeft;
            case NAME -> list = sortedAlphabetical;
            case PRICE_ASC -> list = sortedHighestPrice;
            case PRICE_DESC -> list = sortedHighestPrice.reversed();
        }
        return list.stream()
                .map(notes::get)
                .filter(note -> note.isOnAuction() && !note.isExpired() && !note.isOnWaitingList())
                .filter(note -> search.isEmpty() || Arrays.stream(note.getSearchIndex())
                        .anyMatch(s -> s.contains(search.toLowerCase())))
                .filter(note -> switch (binFilter) {
                    case ALL -> true;
                    case AUCTIONS_ONLY -> note.isBIDAuction();
                    case BIN_ONLY ->  !note.isBIDAuction();
                })
                .collect(Collectors.toList());
    }

    public static List<ItemNote> getSortedList(ItemNoteStorage.SortMode mode, String search, AhConfiguration.BINFilter binFilter, List<Map<?, ?>> whitelist){
        List<ItemNote> returnList = getSortedList(mode, search, binFilter);
        if(!categories.containsKey(whitelist)) {addWhiteList(whitelist);}
        returnList.removeIf(note -> categories.get(whitelist).contains(note.getNoteID()));
        return returnList;
    }

    public static List<ItemNote> getMySortedDateCreated(Player p){ //use only for online players
        return itemNotes.stream()
                .map(notes::get)
                .filter(note -> Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()), p))
                .filter(note -> !(note.isBIDAuction() && note.isSold()))
                .toList(); // toList() makes it unmodifiable
    }

    public static int getNumberOfAuctions(Player p) {
        return getMySortedDateCreated(p).size();
    }

    public static ItemNote getNote(UUID noteID) {
        return itemNotes.stream()
                .filter(id -> id.equals(noteID))
                .map(notes::get)
                .findFirst()
                .orElse(null);
    }

    public static List<ItemNote> getMyBids(UUID playerID) {
        if(!sortedBids.containsKey(playerID)) return List.of();
        return sortedBids.get(playerID).stream()
                .map(notes::get)
                .filter(itemNote -> itemNote.canClaimBid(playerID))
                .toList();
    }

    public static void addBid(UUID playerID, UUID noteID) {
        sortedBids.computeIfAbsent(playerID, k -> new ArrayList<>());
        List<UUID> bids = sortedBids.get(playerID);
        if(!bids.contains(noteID)) bids.addFirst(noteID);
        sortedPlayers.computeIfAbsent(noteID, k -> new ArrayList<>());
        List<UUID> players = sortedPlayers.get(noteID);
        if(!players.contains(playerID)) players.addFirst(playerID);
    }

    private static void updateBids() {
        sortedBids.clear();
        sortedPlayers.clear();
        for(UUID noteID : itemNotes) {
            ItemNote note = notes.get(noteID);
            for(UUID playerID : note.getBidders()) {
                if(canCollectBid(note, playerID)) addBid(playerID, note.getNoteID());
            }
        }
    }

    private static void updateSortedLists(){
        //these lists of the auction items are sorted by price, creation data and the alphabet
        sortedAlphabetical.sort(Comparator.comparing(o -> notes.get(o).getItemName()));
        sortedHighestPrice.sort(Comparator.comparing(o -> notes.get(o).getPrice()));
        sortedTimeLeft.sort((Comparator.comparing(o -> notes.get(o).getTimeLeft())));
    }
}
