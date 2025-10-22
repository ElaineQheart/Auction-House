package me.elaineqheart.auctionHouse.data.redis;

import com.google.gson.Gson;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import redis.clients.jedis.Jedis;

import java.util.*;
import java.util.stream.Collectors;

public class NoteStorage {

    private static final Gson gson = new Gson();

    public static void saveNote(ItemNote note) {
        try (Jedis jedis = RedisManager.getResource()) {
            String key = "auction:note:" + note.getNoteID();

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

            jedis.hset(key, data);

            jedis.zadd("auction:byPrice", note.getPrice(), note.getNoteID().toString());
            jedis.zadd("auction:byPriceDesc", -note.getPrice(), note.getNoteID().toString());
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
            String noteKey = "auction:note:" + noteID;

            Map<String, String> data = jedis.hgetAll(noteKey);
            if (data == null || data.isEmpty()) {
                return;
            }

            String itemName = data.get("itemName");
            if (itemName == null) itemName = "";

            jedis.zrem("auction:byPrice", noteID.toString());
            jedis.zrem("auction:byPriceDesc", noteID.toString());
            jedis.zrem("auction:byDate", noteID.toString());

            jedis.zrem("auction:byName", itemName.toLowerCase() + ":" + noteID);

            jedis.del(noteKey);
        }
    }


    public enum SortMode { NAME, PRICE_ASC, PRICE_DESC, DATE }

    public static List<ItemNote> getNotes(SortMode mode, int offset, int limit) {
        try (Jedis jedis = RedisManager.getResource()) {
            Set<String> ids;

            switch (mode) {
                case NAME -> {
                    ids = jedis.zrange("auction:byName", offset, offset + limit - 1)
                            .stream()
                            .map(s -> s.split(":")[1])
                            .collect(Collectors.toCollection(LinkedHashSet::new));
                }
                case PRICE_ASC -> ids = jedis.zrange("auction:byPrice", offset, offset + limit - 1)
                        .stream()
                        .map(s -> s.split(":")[1])
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                case PRICE_DESC -> ids = jedis.zrange("auction:byPriceDesc", offset, offset + limit - 1)
                        .stream()
                        .map(s -> s.split(":")[1])
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                case DATE -> ids = jedis.zrevrange("auction:byDate", offset, offset + limit - 1)
                        .stream()
                        .map(s -> s.split(":")[1])
                        .collect(Collectors.toCollection(LinkedHashSet::new));
                default -> ids = Set.of();
            }

            List<ItemNote> notes = new ArrayList<>();
            for (String id : ids) {
                ItemNote note = getNote(UUID.fromString(id));
                if (note != null) notes.add(note);
            }
            return notes;
        }
    }

}
