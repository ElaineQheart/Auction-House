package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilSearchGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.ah.ItemManager;
import me.elaineqheart.auctionHouse.ah.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class AdminManageItemGUI extends InventoryGUI implements Runnable{

    private final ItemNote note;
    private final Player currentPlayer;
    private final UUID invID = UUID.randomUUID();

    @Override
    public void run() {
        decorate(currentPlayer);
    }

    public AdminManageItemGUI(ItemNote note, Player p) {
        super();
        this.note = note;
        this.currentPlayer = p;
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9,"Admin Menu");
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
                .creator(player -> ItemManager.createItemFromNote(note, player))
                .consumer(event -> {});
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0, AuctionHouseGUI.Sort.HIGHEST_PRICE,"",currentPlayer,true), p);
                });
    }
    private InventoryButton deleteAuction() {
        return new InventoryButton()
                .creator(player -> ItemManager.adminCancelAuction)
                .consumer(event -> {
                    Sounds.click(event);
                    new AnvilSearchGUI(currentPlayer, AnvilSearchGUI.SearchType.ITEM_DELETE_MESSAGE, note);
                });
    }
    private InventoryButton expireAuction() {
        return new InventoryButton()
                .creator(player -> ItemManager.adminExpireAuction)
                .consumer(event -> {
                    Sounds.click(event);
                    new AnvilSearchGUI(currentPlayer, AnvilSearchGUI.SearchType.ITEM_EXPIRE_MESSAGE, note);
                });
    }

}
