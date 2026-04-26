package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

import java.util.HashMap;
import java.util.Map;

public class AnvilGUIManager implements Listener {

    private static final Map<Inventory, AnvilHandler> activeInventories = new HashMap<>();

    public enum SearchType {
        AH,
        ADMIN_AH,
        ITEM_EXPIRE_MESSAGE,
        ITEM_DELETE_MESSAGE,
        SET_AMOUNT,
        SET_BID
    }

    public void open(Player player, String inventoryTitleKey, AnvilHandler handler) {
        AnvilView view = MenuType.ANVIL.create(player, M.getFormatted(inventoryTitleKey));
        view.setMaximumRepairCost(0);
        view.setItem(0, ItemManager.emptyPaper);
        registerHandledInventory(view.getTopInventory(), handler);
        player.openInventory(view);
    }

    public void registerHandledInventory(Inventory inventory, AnvilHandler handler) {
        activeInventories.put(inventory,handler);
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        AnvilHandler handler = activeInventories.get(event.getView().getTopInventory());
        if (handler == null) return;

        event.setCancelled(true);
        ItemStack paperItem = event.getInventory().getItem(0);
        AnvilView view = (AnvilView) event.getView();
        if (event.getSlot() == 1) view.setItem(0, ItemManager.emptyPaper); // this removes the enchantment cost for some reason
        if (event.getSlot() != 2) return;
        ItemStack resultItem = event.getCurrentItem();
        if (resultItem == null) return;
        ItemMeta meta = resultItem.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            //remove the paper, else it will end up in the players inventory
            assert paperItem != null;
            Player player = (Player) event.getWhoClicked();
            player.getOpenInventory().getTopInventory().remove(paperItem);
            player.getOpenInventory().getBottomInventory().remove(paperItem);
            String typedText = meta.getDisplayName();
            Sounds.click(event);
            activeInventories.remove(event.getView().getTopInventory());
            handler.execute(player, typedText);
        }
    }

    @EventHandler //also set the name formatted
    public void handleTyping(PrepareAnvilEvent event) {
        AnvilHandler handler = activeInventories.get(event.getView().getTopInventory());
        if (handler == null) return;

        ItemStack result = event.getInventory().getItem(2);
        if (result == null) return;

        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> event.getView().setRepairCost(0),1);
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        AnvilHandler handler = activeInventories.get(event.getView().getTopInventory());
        if (handler == null) return;
        ItemStack paperItem = event.getInventory().getItem(0);
        Player p = (Player) event.getPlayer();
        //remove the paper, else it will end up in the players inventory
        assert paperItem != null;
        p.getOpenInventory().getTopInventory().remove(paperItem);
        p.getOpenInventory().getBottomInventory().remove(paperItem);
        handler.onClose(p);
    }

    public void forceCloseAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Inventory inv = player.getOpenInventory().getTopInventory();
            if(activeInventories.containsKey(inv)) {
                ItemStack paperItem = inv.getItem(0);
                assert paperItem != null;
                player.getOpenInventory().getTopInventory().remove(paperItem);
                player.getOpenInventory().getBottomInventory().remove(paperItem);
                player.closeInventory();
            }
        }
    }

}
