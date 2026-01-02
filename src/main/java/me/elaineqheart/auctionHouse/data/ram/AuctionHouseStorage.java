package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.Blacklist;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AuctionHouseStorage {

    private static ArrayList<ItemNote> itemNotes = new ArrayList<>();
    private static LinkedHashMap<ItemNote, Double> sortedHighestPrice = new LinkedHashMap<>();
    private static LinkedHashMap<ItemNote, Long> sortedDateCreated = new LinkedHashMap<>();
    private static LinkedHashMap<ItemNote, String> sortedAlphabetical = new LinkedHashMap<>();


    public static void add(ItemNote item) {
        itemNotes.add(item);
        updateSortedLists();
    }

    public static void set(ItemNote[] items) {
        itemNotes = new ArrayList<>(List.of(items)); //Arrays.asList() links them
        updateSortedLists();
    }

    public static void remove(ItemNote item) {
        itemNotes.remove(item);
        updateSortedLists();
    }

    public static List<ItemNote> getAll() {
        return itemNotes;
    }

    public static List<ItemNote> getSortedList(ItemNoteStorage.SortMode mode, String search){
        Set<ItemNote> set = new HashSet<>();
        switch (mode) {
            case DATE -> set = sortedDateCreated.keySet();
            case NAME -> set = sortedAlphabetical.keySet();
            case PRICE_ASC -> set = sortedHighestPrice.keySet();
            case PRICE_DESC -> {
                List<ItemNote> temp = new ArrayList<>(sortedHighestPrice.keySet());
                Collections.reverse(temp);
                set = new LinkedHashSet<>(temp);
            }
        }
        List<ItemNote> itemList = new ArrayList<>();
        for(ItemNote note : set) {
            if(note.isOnAuction() && !note.isExpired() && !note.isOnWaitingList()) {
                if(search.isEmpty() || Arrays.stream(note.getSearchIndex()).anyMatch(s -> s.contains(search.toLowerCase()))) {
                    itemList.add(note);
                }
            }
        }
        return itemList;
    }

    public static List<ItemNote> getSortedList(ItemNoteStorage.SortMode mode, String search, List<Map<?, ?>> whitelist){
        List<ItemNote> notes = getSortedList(mode, search);
        notes.removeIf(note -> !Blacklist.isBlacklisted(note.getItem(), whitelist));
        return notes;
    }

    public static List<ItemNote> getMySortedDateCreated(UUID playerID){
        List<ItemNote> newSortedDateCreated = new ArrayList<>();
        for(ItemNote note : itemNotes){
            if (Bukkit.getPlayer(note.getPlayerUUID()) == Bukkit.getPlayer(playerID)) {
                newSortedDateCreated.add(note);
            }
        }
        return newSortedDateCreated;
    }

    public static int getNumberOfAuctions(Player p) {
        int count = 0;
        for (ItemNote note : itemNotes) {
            if (Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()), p)) {
                count++;
            }
        }
        return count;
    }

    public static int getNumberOfAuctions() {
        int count = 0;
        for (ItemNote note : itemNotes){
            if(note.isOnAuction() && !note.isExpired()) count++;
        }
        return count;
    }

    public static ItemNote getNote(String noteID) {
        return itemNotes.stream()
                .filter(note -> note.getNoteID().toString().equals(noteID))
                .findFirst()
                .orElse(null);
    }


    //TODO: optimize
    private static void updateSortedLists(){
        //these lists of the auction items are sorted by price, creation data and the alphabet
        Map<ItemNote,Double> map = new HashMap<>();
        for (ItemNote note : itemNotes){
            if(note.isOnAuction() && !note.isExpired())
                map.put(note,note.getCurrentPrice());
        }
        sortedHighestPrice = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Map<ItemNote,Long> map2 = new HashMap<>();
        for (ItemNote note : itemNotes){
            if(note.isOnAuction() && !note.isExpired())
                map2.put(note,note.getDateCreated().getTime());
        }
        sortedDateCreated = map2.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        Map<ItemNote,String> map3 = new HashMap<>();
        for (ItemNote note : itemNotes){
            if(note.isOnAuction() && !note.isExpired())
                map3.put(note, note.getItemName());
        }
        sortedAlphabetical = map3.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }
}
