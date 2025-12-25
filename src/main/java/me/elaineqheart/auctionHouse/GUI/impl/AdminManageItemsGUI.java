package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilSearchGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class AdminManageItemsGUI extends InventoryGUI implements Runnable{

    private final ItemNote note;
    private final AhConfiguration c;
    private final UUID invID = UUID.randomUUID();

    @Override
    public void run() {
        decorate(c.currentPlayer);
    }

    public AdminManageItemsGUI(ItemNote note, AhConfiguration configuration) {
        super();
        this.note = note;
        c = configuration;
        c.view = AhConfiguration.View.ADMIN_MANAGE_ITEMS;
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.admin-menu"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # # # . # # # #",
                "# # # # # # # # #",
                "# # # . # . # # #",
                "# # # # # # # # #",
                "# # # # . # # # #"
        },fillerItem());
        this.addButton(13, item());
        this.addButton(30, expireAuction());
        this.addButton(32, deleteAuction());
        this.addButton(49, back());
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

    private void fillOutPlaces(String[] places, InventoryButton fillerItem){
        for(int i = 0; i < places.length; i++){
            for(int j = 0; j < places[i].length(); j+=2){
                if(places[i].charAt(j)=='#') {
                    this.addButton(i*9+j/2, fillerItem);
                }
            }
        }
    }

    private InventoryButton fillerItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }
    private InventoryButton item() {
        return new InventoryButton()
                .creator(player -> ItemManager.createItemFromNote(note, player, false))
                .consumer(event -> {});
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
                });
    }
    private InventoryButton deleteAuction() {
        return new InventoryButton()
                .creator(player -> ItemManager.adminCancelAuction)
                .consumer(event -> {
                    Sounds.click(event);
                    new AnvilSearchGUI(c.currentPlayer, AnvilSearchGUI.SearchType.ITEM_DELETE_MESSAGE, note, c);
                });
    }
    private InventoryButton expireAuction() {
        return new InventoryButton()
                .creator(player -> ItemManager.adminExpireAuction)
                .consumer(event -> {
                    Sounds.click(event);
                    new AnvilSearchGUI(c.currentPlayer, AnvilSearchGUI.SearchType.ITEM_EXPIRE_MESSAGE, note, c);
                });
    }

}
