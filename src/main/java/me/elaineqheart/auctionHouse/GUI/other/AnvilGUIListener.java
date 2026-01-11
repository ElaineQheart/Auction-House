package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

public class AnvilGUIListener implements Listener {

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        //ItemStack paperItem = event.getInventory().getItem(0);
        //if (paperItem == null || !paperItem.equals(ItemManager.emptyPaper)) return; // emptyPaper has a custom persistent data value
        AnvilView view = (AnvilView) event.getView();
        if(!AnvilGUIManager.activeAnvils.containsKey(view)) return; // safe method
        event.setCancelled(true);
        view.setRepairCost(0);

        Player player = (Player) event.getWhoClicked();
        if (event.getSlot() != 2) return;
        ItemStack resultItem = event.getCurrentItem();
        if (resultItem == null) return;
        ItemMeta meta = resultItem.getItemMeta();
        if (meta != null && meta.hasDisplayName()) {
            //remove the paper, else it will end up in the players inventory
            player.getOpenInventory().getTopInventory().remove(ItemManager.emptyPaper);
            String typedText = meta.getDisplayName();
            Sounds.click(event);
            AhConfiguration c = AnvilGUIManager.activeAnvils.get(view);
            AnvilGUIManager.activeAnvils.remove(view);
            if(view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-search"))) {
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-admin-search"))) {
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-admin-expire-message"))) {
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-admin-delete-message"))) {
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-set-amount"))) {
                ItemNote note = AnvilGUIManager.activeNoteMap.get(player);
                AnvilGUIManager.activeNoteMap.remove(player);

            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-set-bid"))) {

            }

        }
    }

    @EventHandler
    public void onType(PrepareAnvilEvent event) {
        ItemStack paperItem = event.getInventory().getItem(0);
        if (paperItem == null || !paperItem.equals(ItemManager.emptyPaper)) return;
        //run task later to make sure the repair cost is set to 0 after the event is done
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () ->
                event.getView().setRepairCost(0), 1);
    }

    @EventHandler
    public void onAnvilClose(InventoryCloseEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        AnvilView view = (AnvilView) event.getView();
        if(!AnvilGUIManager.activeAnvils.containsKey(view)) return; // safe method
        //remove the paper, else it will end up in the players inventory
        AhConfiguration c = AnvilGUIManager.activeAnvils.get(view);
        c.getPlayer().getOpenInventory().getTopInventory().remove(ItemManager.emptyPaper);
        c.getPlayer().getOpenInventory().getBottomInventory().remove(ItemManager.emptyPaper);
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () ->
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.getPlayer()),1);

    }

}
