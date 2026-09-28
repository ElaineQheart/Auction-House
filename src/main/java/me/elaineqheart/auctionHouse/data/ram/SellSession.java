package me.elaineqheart.auctionHouse.data.ram;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// What a player is currently setting up in the sell menu. Kept in memory so it survives the price input (anvil) and going back to the hub.
// The item stays in the player's inventory until the auction is confirmed; only its slot is remembered here.
public class SellSession {

    private static final Map<UUID, SellSession> sessions = new ConcurrentHashMap<>();

    public static SellSession get(Player p) {
        return sessions.computeIfAbsent(p.getUniqueId(), k -> new SellSession());
    }
    public static void remove(UUID playerID) {
        sessions.remove(playerID);
    }

    public int slot = -1;
    public ItemStack item;
    public int amount;
    public double price;
    public Boolean bid;       // null = not chosen yet
    public int tierIndex = -1; // -1 = not chosen yet

    public boolean hasItem() {
        return item != null;
    }
    public void clearItem() {
        slot = -1;
        item = null;
        amount = 0;
    }
    public void reset() {
        clearItem();
        price = 0;
    }

}
