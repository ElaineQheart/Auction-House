package me.elaineqheart.auctionHouse.GUI.other;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AdminConfirmGUI;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionViewGUI;
import me.elaineqheart.auctionHouse.GUI.impl.ConfirmBuyGUI;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.vault.VaultHook;
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
        if(!AnvilSearchGUI.activeAnvils.containsKey(view)) return; // safe method
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
            AhConfiguration c = AnvilSearchGUI.activeAnvils.get(view);
            AnvilSearchGUI.activeAnvils.remove(view);
            if(view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-search"))) {
                c.currentSearch = typedText;
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c),player);
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-admin-search"))) {
                c.currentSearch = typedText;
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c),player);
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-admin-expire-message"))) {
                AuctionHouse.getGuiManager().openGUI
                        (new AdminConfirmGUI(typedText, AnvilSearchGUI.activeNoteMap.get(player), AnvilSearchGUI.SearchType.ITEM_EXPIRE_MESSAGE,c),player);
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-admin-delete-message"))) {
                AuctionHouse.getGuiManager().openGUI
                        (new AdminConfirmGUI(typedText, AnvilSearchGUI.activeNoteMap.get(player), AnvilSearchGUI.SearchType.ITEM_DELETE_MESSAGE,c),player);
            } else if (view.getTitle().equals(Messages.getFormatted("inventory-titles.anvil-set-amount"))) {
                ItemNote note = AnvilSearchGUI.activeNoteMap.get(player);
                AnvilSearchGUI.activeNoteMap.remove(player);
                try {
                    int amount = Integer.parseInt(typedText);
                    if (amount <= 0 || amount > note.getCurrentAmount()) throw new RuntimeException();
                    if (note.getPrice() / note.getItem().getAmount() * amount > VaultHook.getEconomy().getBalance(player)) {
                        AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c), player);
                        player.sendMessage(Messages.getFormatted("chat.not-enough-money"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    ItemStack item = note.getItem();
                    item.setAmount(amount);
                    AuctionHouse.getGuiManager().openGUI(new ConfirmBuyGUI(note, c, item), player);
                } catch (Exception e) {
                    AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c), player);
                    player.sendMessage(Messages.getFormatted("chat.invalid-amount"));
                    Sounds.villagerDeny(event);
                }
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
        if(!AnvilSearchGUI.activeAnvils.containsKey(view)) return; // safe method
        //remove the paper, else it will end up in the players inventory
        AhConfiguration c = AnvilSearchGUI.activeAnvils.get(view);
        c.currentPlayer.getOpenInventory().getTopInventory().remove(ItemManager.emptyPaper);
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () ->
                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer),1);

    }

}
