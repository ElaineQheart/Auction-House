package me.elaineqheart.auctionHouse.GUI.other.input;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.MenuType;

public class InputGUIManager implements Listener {

    final private AnvilGUIManager anvilManager = new AnvilGUIManager();
    final private ChatInputManager chatManager = new ChatInputManager();

    public InputGUIManager(AuctionHouse plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(anvilManager, plugin);
        Bukkit.getPluginManager().registerEvents(chatManager, plugin);
    }

    @SuppressWarnings("UnstableApiUsage")
    public void open(Player player, String inventoryTitleKey, InputHandler handler) {
        String inventoryTitle = M.getFormatted(inventoryTitleKey);
        try {
            MenuType.class.getName();

            anvilManager.open(player, inventoryTitle, handler);
        } catch (NoClassDefFoundError e) {
            chatManager.open(player, inventoryTitle,  handler);
        }
    }


    public void forceCloseAll() {
        anvilManager.forceCloseAll();
        chatManager.forceCloseAll();
    }

}
