package me.elaineqheart.auctionHouse.GUI;

import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyBidsGUI;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

//https://www.spigotmc.org/threads/a-modern-approach-to-inventory-guis.594005/

//This is a manager class so it will be treated as a singleton. This means we only create one single
//instance of this class and no more.

public class GUIManager {

    private final HashMap<Inventory, InventoryHandler> activeInventories = new HashMap<>();
    private final HashMap<UUID, LinkedList<Long>> throttle = new HashMap<>();

    public void openGUI(InventoryGUI gui, Player player) {
        UUID playerId = player.getUniqueId();
        if (!throttle.containsKey(playerId)) throttle.put(playerId, new LinkedList<>());
        LinkedList<Long> timestampsList = throttle.get(playerId);
        timestampsList.addFirst(System.currentTimeMillis());
        if (timestampsList.size() > 5) {
            timestampsList.removeLast();
            if (timestampsList.getFirst() - timestampsList.getLast() < 400) {
                return;
            }
        }
        UUID gradualCleanup = (UUID) throttle.keySet().toArray()[throttle.size() - 1];
        if (System.currentTimeMillis() - throttle.get(gradualCleanup).getFirst() > 400) {
            throttle.remove(gradualCleanup);
        }

        this.registerHandledInventory(gui.getInventory(), gui);
        player.openInventory(gui.getInventory());
    }
    public void openGUI(Player p, AhConfiguration c, AhConfiguration.View goBackTo) {
        if (goBackTo == AhConfiguration.View.AUCTION_HOUSE) openGUI(new AuctionHouseGUI(c), p);
        else if (goBackTo == AhConfiguration.View.MY_AUCTIONS) openGUI(new MyAuctionsGUI(c), p);
        else if (goBackTo == AhConfiguration.View.MY_BIDS) openGUI(new MyBidsGUI(c,0), p);
    }

    public void registerHandledInventory(Inventory inventory, InventoryHandler handler) {
        this.activeInventories.put(inventory,handler);
    }

    public void unregisterInventory(Inventory inventory) {
        this.activeInventories.remove(inventory);
    }

    public void handleClick(InventoryClickEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());
        if (handler == null) return;
        handler.onClick(event);
    }

    public void handleOpen(InventoryOpenEvent event) {
        InventoryHandler handler = this.activeInventories.get(event.getInventory());

        if (handler != null){
            handler.onOpen(event);
        }
    }

    public void handleClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHandler handler = this.activeInventories.get(inventory);
        if (handler != null){
            handler.onClose(event);
            this.unregisterInventory(inventory);
        }
    }

    public void forceCloseAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(this.activeInventories.containsKey(player.getOpenInventory().getTopInventory())) player.closeInventory();
        }
    }

}