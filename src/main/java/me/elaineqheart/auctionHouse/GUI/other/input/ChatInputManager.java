package me.elaineqheart.auctionHouse.GUI.other.input;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;

public class ChatInputManager implements Listener {

    private final AuctionHouse instance = AuctionHouse.getInstance();
    final private HashMap<Player, InputHandler> activePlayers = new HashMap<>();
    
    public void open(Player player, String inventoryTitle, InputHandler handler) {
        registerPlayer(player, handler);
        player.closeInventory();
        player.sendMessage(M.getFormatted("chat.input-listener", "%request%", inventoryTitle));
    }

    private void registerPlayer(Player player, InputHandler handler) {
        activePlayers.put(player, handler);
    }

    private InputHandler unregisterPlayer(Player player) {
        return activePlayers.remove(player);
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChatInput(AsyncPlayerChatEvent event) {
        Player p = event.getPlayer();
        if (!activePlayers.containsKey(p)) return;
        event.setCancelled(true);
        String input = event.getMessage();
        instance.getScheduler().entitySpecificScheduler(p).run(() -> unregisterPlayer(p).execute(p, input), () -> {});
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        unregisterPlayer((Player) event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        unregisterPlayer(event.getPlayer());
    }

    public void forceCloseAll() {
        activePlayers.clear();
    }

}
