package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AdminConfirmGUI;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.Messages;
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
        ItemStack paperItem = event.getInventory().getItem(0);
        if (paperItem == null || !paperItem.equals(ItemManager.emptyPaper)) return; // emptyPaper has a custom persistent data value
        event.setCancelled(true);
        AnvilView view = (AnvilView) event.getView();
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
            if(view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-search"))) {
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0,AuctionHouseGUI.Sort.HIGHEST_PRICE,typedText,player,false),player);
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-admin-search"))) {
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0,AuctionHouseGUI.Sort.HIGHEST_PRICE,typedText,player,true),player);
            } else if (view.getTitle().equals(Messages.getFormatted("anvil-admin-expire-message"))) {
                AuctionHouse.getGuiManager().openGUI
                        (new AdminConfirmGUI(typedText, AnvilSearchGUI.currentAdminNoteMap.get(player), AnvilSearchGUI.SearchType.ITEM_EXPIRE_MESSAGE),player);
            } else if (view.getTitle().equals(Messages.getFormatted("anvil-admin-delete-message"))) {
                AuctionHouse.getGuiManager().openGUI
                        (new AdminConfirmGUI(typedText, AnvilSearchGUI.currentAdminNoteMap.get(player), AnvilSearchGUI.SearchType.ITEM_DELETE_MESSAGE),player);
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
        ItemStack paperItem = event.getInventory().getItem(0);
        if (paperItem == null || !paperItem.equals(ItemManager.emptyPaper)) return;
        Player p = (Player) event.getPlayer();
        //remove the paper, else it will end up in the players inventory
        p.getOpenInventory().getTopInventory().remove(ItemManager.emptyPaper);
        if(event.getInventory().getItem(2) != null) return;
        AnvilView view = (AnvilView) event.getView();
        if(view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-search"))) {
            Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "",p,false), p);
            },1);
        } else {
            Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "",p,true), p);
            },1);
        }

    }

}
