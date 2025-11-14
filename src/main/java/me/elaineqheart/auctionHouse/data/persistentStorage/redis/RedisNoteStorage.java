package me.elaineqheart.auctionHouse.data.persistentStorage.redis;

import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.NoteStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redis.clients.jedis.Jedis;

import java.util.*;

public class RedisNoteStorage {

    public static void createNote(Player p, ItemStack item, int price) {
        try (Jedis jedis = RedisManager.getResource()) {
            ItemNote note = new ItemNote(p, item, price);
            Map<String, String> data = getNoteMap(note);

            jedis.hset("auction:note:" + note.getNoteID(), data);
            jedis.sadd("auction:player:" + note.getPlayerUUID(), note.getNoteID().toString());

            jedis.zadd("auction:byPrice", note.getPrice(), note.getNoteID().toString());
            jedis.zadd("auction:byDate", note.getDateCreated().getTime(), note.getNoteID().toString());
            jedis.zadd("auction:byName", 0, note.getItemName().toLowerCase() + ":" + note.getNoteID());

        }
    }

    public static void updateField(UUID noteID, String field, Object value) {
        try (Jedis jedis = RedisManager.getResource()) {
            String key = "auction:note:" + noteID;
            jedis.hset(key, field, String.valueOf(value));
        }
    }

    public static ItemNote getNote(UUID noteID) {
        try (Jedis jedis = RedisManager.getResource()) {
            Map<String, String> data = jedis.hgetAll("auction:note:" + noteID);
            if (data.isEmpty()) return null;

            return new ItemNote(
                    data.get("playerName"),
                    UUID.fromString(data.get("playerUUID")),
                    data.get("buyerName"),
                    Double.parseDouble(data.get("price")),
                    data.get("itemData"),
                    new Date(Long.parseLong(data.get("dateCreated"))),
                    Boolean.parseBoolean(data.get("isSold")),
                    data.get("adminMessage"),
                    Integer.parseInt(data.get("partiallySoldAmountLeft")),
                    Long.parseLong(data.get("auctionTime")),
                    data.get("itemName"),
                    UUID.fromString(noteID.toString())
            );
        }
    }

    public static void deleteNote(UUID noteID) {
        try (Jedis jedis = RedisManager.getResource()) {
            Map<String, String> data = jedis.hgetAll("auction:note:" + noteID);
            if (data == null || data.isEmpty()) return;

            String itemName = data.get("itemName");
            if (itemName == null) itemName = "";

            jedis.del("auction:note:" + noteID);
            jedis.srem("auction:player:" + data.get("playerUUID"), noteID.toString());

            jedis.zrem("auction:byPrice", noteID.toString());
            jedis.zrem("auction:byDate", noteID.toString());
            jedis.zrem("auction:byName", itemName.toLowerCase() + ":" + noteID);
        }
    }

    private static Map<String, String> getNoteMap(ItemNote note) {
        Map<String, String> data = new HashMap<>();
        data.put("noteID", note.getNoteID().toString());
        data.put("itemName", note.getItemName());
        data.put("playerName", note.getPlayerName());
        data.put("buyerName", note.getBuyerName() == null ? "" : note.getBuyerName());
        data.put("playerUUID", note.getPlayerUUID().toString());
        data.put("price", String.valueOf(note.getPrice()));
        data.put("dateCreated", String.valueOf(note.getDateCreated().getTime()));
        data.put("itemData", note.getItemData());
        data.put("isSold", String.valueOf(note.isSold()));
        data.put("partiallySoldAmountLeft", String.valueOf(note.getPartiallySoldAmountLeft()));
        data.put("adminMessage", note.getAdminMessage() == null ? "" : note.getAdminMessage());
        data.put("auctionTime", String.valueOf(note.getAuctionTime()));
        return data;
    }



    public static List<ItemNote> getNotes(NoteStorage.SortMode mode, int start, int stop, String search) {
        List<ItemNote> notes = new ArrayList<>();

        while (notes.size() < stop) {
            List<String> ids = notes(mode, start, stop);
            if(ids.isEmpty()) break;
            for (String id : ids) {
                ItemNote note = getNote(UUID.fromString(id));
                if (note != null && note.isOnAuction() && !note.isExpired() && !note.isOnWaitingList()) {
                    if(search.isEmpty() || Arrays.stream(note.getSearchIndex()).anyMatch(s -> s.contains(search.toLowerCase()))) {
                        notes.add(note);
                    }
                } else {
                    //delete expired items from the byPrice/byDate/byName lists, but not the individual player list
                    try (Jedis jedis = RedisManager.getResource()) {
                        jedis.zrem("auction:byPrice", id);
                        jedis.zrem("auction:byDate", id);
                        String itemName = jedis.hget("auction:note:" + id, "itemName");
                        jedis.zrem("auction:byName", itemName.toLowerCase() + ":" + id);
                    }
                }
            }
            start += stop;
        }
        return notes;
    }

    private static List<String> notes(NoteStorage.SortMode mode, int start, int stop) {
        final int MAX_RETRIES = 3;
        final long RETRY_DELAY_MS = 200;

        for (int attempts = 0; attempts < MAX_RETRIES; attempts++) {
            try (Jedis jedis = RedisManager.getResource()) {
                List<String> ids;
                switch (mode) {
                    case NAME -> ids = jedis.zrange("auction:byName", start, stop)
                            .stream()
                            .map(s -> s.split(":")[1])
                            .toList();
                    case PRICE_ASC -> ids = jedis.zrange("auction:byPrice", start, stop);
                    case PRICE_DESC -> ids = jedis.zrevrange("auction:byPrice", start, stop);
                    case DATE -> ids = jedis.zrange("auction:byDate", start, stop);
                    default -> ids = Collections.emptyList();
                }
                return new ArrayList<>(ids);
            } catch (redis.clients.jedis.exceptions.JedisConnectionException e) {
                Bukkit.getLogger().warning("Redis read timed out (attempt " + attempts + " of " + MAX_RETRIES + ").");
                if (attempts == MAX_RETRIES - 1) {
                    Bukkit.getLogger().severe("Redis read can't be reached, returning empty list. Cause: " + e);
                }
                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (Exception ie) {
                    Thread.currentThread().interrupt();
                    return new ArrayList<>();
                }
            }
        }
        return new ArrayList<>();
    }

    public static int numberOfAuctions(Player p) {
        try (Jedis jedis = RedisManager.getResource()) {
            return (int) jedis.scard("auction:player:" + p.getUniqueId());
        }
    }

    public static int numberOfAuctions() {
        try (Jedis jedis = RedisManager.getResource()) {
            long totalNotes = jedis.zcard("auction:byPrice");
            return (int) totalNotes;
        }
    }

    public static List<ItemNote> mySortedDateCreated(UUID playerID){
        try(Jedis jedis = RedisManager.getResource()) {
            Set<String> noteIds = jedis.smembers("auction:player:" + playerID);
            if (noteIds == null || noteIds.isEmpty()) return List.of();
            return noteIds.stream()
                    .map(UUID::fromString)
                    .map(RedisNoteStorage::getNote)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(ItemNote::getDateCreated))
                    .toList();
        }
    }

    public static void purge() {
        try(Jedis jedis = RedisManager.getResource()) {
            jedis.flushDB();
        }
    }

}
