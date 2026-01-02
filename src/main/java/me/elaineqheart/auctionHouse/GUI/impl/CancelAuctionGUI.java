package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CancelAuctionGUI extends InventoryGUI implements Runnable{

    private final ItemNote note;
    private final UUID invID = UUID.randomUUID();
    private final AhConfiguration c;
    private final ItemStack item;

    @Override
    public void run() {
        this.addButton(13, Item());
        super.decorate(c.currentPlayer);
    }

    public CancelAuctionGUI(ItemNote note, AhConfiguration configuration) {
        super();
        this.note = note;
        c = configuration;
        c.view = AhConfiguration.View.CANCEL_AUCTION;
        this.item = ItemManager.createItemFromNote(note, c.currentPlayer, true);
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.cancel-auction"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # # # . # # # #",
                "# # # # # # # # #",
                "# # # # . # # # #",
                "# # # # # # # # #",
                "# # # # . # # # #"
        },fillerItem());
        this.addButton(13, Item());
        this.addButton(31, collectItem());
        this.addButton(49, back());
        super.decorate(player);
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

    private InventoryButton fillerItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }
    private InventoryButton Item() {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(ItemManager.isShulkerBox(item) && event.isRightClick()) {
                        Sounds.openShulker(event);
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c), c.currentPlayer);
                    }
                });
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMyAuctions)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), p);
                });
    }
    private InventoryButton collectItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.cancelAuction)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    //check if inventory is full
                    if(p.getInventory().firstEmpty() == -1){
                        p.sendMessage(Messages.getFormatted("chat.inventory-full"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if(!ItemNoteStorage.r()) {
                        //ItemNote test = NoteStorage.getNote(note.getNoteID().toString());
                        if (!note.isOnAuction()) {
                            p.sendMessage(Messages.getFormatted("chat.already-sold2"));
                            Sounds.villagerDeny(event);
                            return;
                        }
                        Sounds.experience(event);
                        Sounds.breakWood(event);
                        p.getInventory().addItem(note.getItem());
                        ItemNoteStorage.deleteNote(note);
                        AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), p);
                        p.sendMessage(Messages.getFormatted("chat.auction-canceled"));
                    } else {

                    }
                });
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

}


