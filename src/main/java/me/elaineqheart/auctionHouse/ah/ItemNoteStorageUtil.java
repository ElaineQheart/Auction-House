package me.elaineqheart.auctionHouse.ah;

import com.google.gson.Gson;
import me.elaineqheart.auctionHouse.AuctionHouse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class ItemNoteStorageUtil {

    //This class is where the Note objects are managed
    //gson is used to convert the Note objects into json Strings and backwards

    private static ArrayList<ItemNote> itemNotes = new ArrayList<>();
    private static LinkedHashMap<ItemNote, Integer> sortedHighestPrice = new LinkedHashMap<>();
    private static LinkedHashMap<ItemNote, Long> sortedDateCreated = new LinkedHashMap<>();
    private static LinkedHashMap<ItemNote, String> sortedAlphabetical = new LinkedHashMap<>();

    public static void createNote(Player p, ItemStack item, int price){

        String data = ItemStackConverter.encode(item);

        ItemNote itemNote = new ItemNote(p, data, price);
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

    public static ItemNote readNote(String id){

        for (ItemNote itemNote : itemNotes){
            if(itemNote.getId().equalsIgnoreCase(id)){
                return itemNote;
            }
        }
        return null;
    }

    public static void saveNotes() throws IOException {

        Gson gson = new Gson();
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/notes.json");
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
        Gson gson = new Gson();
        File file = new File(AuctionHouse.getPlugin().getDataFolder().getAbsolutePath() + "/notes.json");
        if(file.exists()){
            Reader reader = new FileReader(file);
            ItemNote[] n = gson.fromJson(reader, ItemNote[].class);
            itemNotes = new ArrayList<>(Arrays.asList(n));
            updateSortedLists();
        }
    }

    public static List<ItemNote> findAllNotes(){
        return itemNotes;
    }

    private static void updateSortedLists(){
        //these lists of the auction items are sorted by price, creation data and the alphabet
        Map<ItemNote,Integer> map = new HashMap<>();
        for (ItemNote note : itemNotes){
            if(!note.isSold() && !note.isExpired())
                map.put(note,note.getPrice());
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
            if(!note.isSold() && !note.isExpired())
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
            if(!note.isSold() && !note.isExpired())
                map3.put(note,note.getItem().getType().toString());
        }
        sortedAlphabetical = map3.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    public static LinkedHashMap<ItemNote, Integer> sortedHighestPrice(){
        LinkedHashMap<ItemNote, Integer> returnList = new LinkedHashMap<>();
        for(ItemNote note : sortedHighestPrice.keySet()) {
            if(!note.isSold() && !note.isExpired()) {
                returnList.put(note, note.getPrice());
            }
        }
        return returnList;
    }
    public static LinkedHashMap<ItemNote, Long> sortedDateCreated(){
        LinkedHashMap<ItemNote, Long> returnList = new LinkedHashMap<>();
        for(ItemNote note : sortedDateCreated.keySet()) {
            if(!note.isSold() && !note.isExpired()) {
                returnList.put(note, note.getDateCreated().getTime());
            }
        }
        return returnList;
    }
    public static LinkedHashMap<ItemNote, String> sortedAlphabetical(){
        LinkedHashMap<ItemNote, String> returnList = new LinkedHashMap<>(sortedAlphabetical);
        for(ItemNote note : returnList.keySet()) {
            if(!note.isSold() && !note.isExpired()) {
                returnList.put(note, note.getItem().getType().toString());
            }
        }
        return returnList;
    }
    public static LinkedHashMap<ItemNote, Long> mySortedDateCreated(){
        LinkedHashMap<ItemNote, Long> newSortedDateCreated = new LinkedHashMap<>();
        for(ItemNote note : itemNotes){
            newSortedDateCreated.put(note,note.getDateCreated().getTime());
        }
        newSortedDateCreated = newSortedDateCreated.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return newSortedDateCreated;
    }

    public static LinkedHashMap<ItemNote, Integer> hiPrSearch(String search){
        LinkedHashMap<ItemNote, Integer> newSortedMap = new LinkedHashMap<>();
        for(ItemNote note : sortedHighestPrice.keySet()){
            if(note.getItem().toString().toLowerCase().contains(search.toLowerCase())){
                newSortedMap.put(note,note.getPrice());
            }
        }
        newSortedMap = newSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return newSortedMap;
    }
    public static LinkedHashMap<ItemNote, Long> dateSearch(String search){
        LinkedHashMap<ItemNote, Long> newSortedMap = new LinkedHashMap<>();
        for(ItemNote note : sortedDateCreated.keySet()){
            if(note.getItem().toString().toLowerCase().contains(search.toLowerCase())){
                newSortedMap.put(note,note.getDateCreated().getTime());
            }
        }
        newSortedMap = newSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return newSortedMap;
    }
    public static LinkedHashMap<ItemNote, String> alphaSearch(String search){
        LinkedHashMap<ItemNote, String> newSortedMap = new LinkedHashMap<>();
        for(ItemNote note : sortedAlphabetical.keySet()){
            if(note.getItem().toString().toLowerCase().contains(search.toLowerCase())){
                newSortedMap.put(note,note.getItem().getType().toString());
            }
        }
        newSortedMap = newSortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));
        return newSortedMap;
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


}
