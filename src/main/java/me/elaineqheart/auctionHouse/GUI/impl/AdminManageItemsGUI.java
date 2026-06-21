package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilHandler;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class AdminManageItemsGUI extends InventoryGUI implements Runnable{

    private static final AuctionHouse instance = AuctionHouse.getInstance();

    private final ItemNote note;
    private final AhConfiguration c;
    private final UUID invID = UUID.randomUUID();

    @Override
    public void run() {
        if (this.getInventory().getViewers().isEmpty()) TaskManager.cancelTask(invID);
        decorate(c.getPlayer());
    }

    public AdminManageItemsGUI(ItemNote note, AhConfiguration configuration) {
        super();
        this.note = note;
        c = configuration;
        c.setView(AhConfiguration.View.ADMIN_MANAGE_ITEMS);
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getInstance(), this, 20, 20).getTaskId()); // Not folia supported
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, M.getFormatted("inventory-titles.admin-menu"));
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
        TaskManager.cancelTask(invID); // not folia supported
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
                    AnvilHandler handler = new AnvilHandler() {
                        public void execute(Player p, String typedText) {
                            AuctionHouse.getGuiManager().openGUI
                                    (new AdminConfirmGUI(typedText, note, true, c), c.getPlayer());
                        }
                        public void onClose(Player p) {
                            instance.getMorePaperLib().scheduling().globalRegionalScheduler().runDelayed(() ->
                                    AuctionHouse.getGuiManager().openGUI(new AdminManageItemsGUI(note, c), c.getPlayer()),1);
                        }
                    };
                    AuctionHouse.getAnvilManager().open(c.getPlayer(),"inventory-titles.anvil-admin-delete-message", handler);
                });
    }
    private InventoryButton expireAuction() {
        return new InventoryButton()
                .creator(player -> ItemManager.adminExpireAuction)
                .consumer(event -> {
                    Sounds.click(event);
                    AnvilHandler handler = new AnvilHandler() {
                        public void execute(Player p, String typedText) {
                            AuctionHouse.getGuiManager().openGUI
                                    (new AdminConfirmGUI(typedText, note, false, c), c.getPlayer());
                        }
                        public void onClose(Player p) {
                            instance.getMorePaperLib().scheduling().globalRegionalScheduler().runDelayed(() ->
                                    AuctionHouse.getGuiManager().openGUI(new AdminManageItemsGUI(note, c), c.getPlayer()),1);
                        }
                    };
                    AuctionHouse.getAnvilManager().open(c.getPlayer(), "inventory-titles.anvil-admin-expire-message", handler);
                });
    }

}
