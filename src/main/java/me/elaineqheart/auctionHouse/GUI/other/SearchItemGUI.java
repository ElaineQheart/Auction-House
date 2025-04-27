package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.ah.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MenuType;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.view.AnvilView;

public class SearchItemGUI implements Listener {

    public static void openAnvilForPlayer(Player player) {
        AnvilView view = MenuType.ANVIL.create(player,"Search Item");
        view.setMaximumRepairCost(0);
        view.setItem(0, ItemManager.emptyPaper);
        player.openInventory(view);
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (event.getInventory().getType() != InventoryType.ANVIL) return;
        ItemStack paperItem = event.getInventory().getItem(0);
        if (paperItem == null || !paperItem.equals(ItemManager.emptyPaper)) return;
        event.setCancelled(true);

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
            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE,typedText,player),player);

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
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE, "",p), p);
        },1);
    }

}
