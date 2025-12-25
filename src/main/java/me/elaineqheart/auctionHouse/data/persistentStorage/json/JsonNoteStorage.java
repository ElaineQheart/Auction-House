package me.elaineqheart.auctionHouse.data.persistentStorage.json;

import com.google.gson.Gson;
import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.NoteStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

public class JsonNoteStorage {

    //This class is where the Note objects are managed
    //gson is used to convert the Note objects into json Strings and backwards

    private static ArrayList<ItemNote> itemNotes = new ArrayList<>();
    private static LinkedHashMap<ItemNote, Double> sortedHighestPrice = new LinkedHashMap<>();
    private static LinkedHashMap<ItemNote, Long> sortedDateCreated = new LinkedHashMap<>();
    private static LinkedHashMap<ItemNote, String> sortedAlphabetical = new LinkedHashMap<>();

    public static void createNote(Player p, ItemStack item, int price){

        ItemNote itemNote = new ItemNote(p, item, price);
        itemNotes.add(itemNote);
        updateSortedLists();

        try {
            saveNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void deleteNote(ItemNote note) {
        itemNotes.remove(note);
        updateSortedLists();

        try {
            saveNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveNotes() throws IOException {

        Gson gson = new Gson();
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/data/notes.json");
        //if the parent file of the plugin doesn't exist, it has to be created
        file.getParentFile().mkdir();
        file.createNewFile();
        //if append is true, it will append the json text and not overwrite the file
        Writer writer = new FileWriter(file, false);
        gson.toJson(itemNotes, writer);
        //flush = write data
        writer.flush();
        writer.close();

    }

    public static void loadNotes() throws IOException {
        backwardsCompatibility();
        Gson gson = new Gson();
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/data/notes.json");
        if(file.exists()){
            Reader reader = new FileReader(file);
            ItemNote[] n = gson.fromJson(reader, ItemNote[].class);
            itemNotes = new ArrayList<>(Arrays.asList(n));
            updateSortedLists();
        }
    }

    private static void backwardsCompatibility() throws IOException {
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/data/notes.json");
        File old = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/notes.json");
        if (old.exists()) {
            Files.copy(old.getAbsoluteFile().toPath(), file.getAbsoluteFile().toPath(), StandardCopyOption.REPLACE_EXISTING);
            old.delete();
        }
    }

    public static ItemNote getNote(String noteID) {
        for (ItemNote note : itemNotes) {
            if (note.getNoteID().toString().equals(noteID)) {
                return note;
            }
        }
        return null;
    }

    public static List<ItemNote> findAllNotes(){
        return itemNotes;
    }

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

    public static List<ItemNote> getSortedList(NoteStorage.SortMode mode, String search){
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

    public static List<ItemNote> mySortedDateCreated(UUID playerID){
        List<ItemNote> newSortedDateCreated = new ArrayList<>();
        for(ItemNote note : itemNotes){
            if (Bukkit.getPlayer(note.getPlayerUUID()) == Bukkit.getPlayer(playerID)) {
                newSortedDateCreated.add(note);
            }
        }
        return newSortedDateCreated;
    }

    public static int numberOfAuctions(Player p) {
        int count = 0;
        for (ItemNote note : itemNotes) {
            if (Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()), p)) {
                count++;
            }
        }
        return count;
    }

    public static int numberOfAuctions() {
        int count = 0;
        for (ItemNote note : itemNotes){
            if(note.isOnAuction() && !note.isExpired()) count++;
        }
        return count;
    }

    public static void purge() {
        itemNotes = new ArrayList<>();
        try {
            saveNotes();
            loadNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
