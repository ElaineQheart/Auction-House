package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.NoteStorage;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class CollectExpiredItemGUI extends InventoryGUI {

    private final ItemNote note;
    private final MyAuctionsGUI.MySort currentSort;
    private final AhConfiguration c;
    public CollectExpiredItemGUI(ItemNote note, MyAuctionsGUI.MySort sort,  AhConfiguration configuration) {
        super();
        this.note = note;
        this.currentSort = sort;
        c = configuration;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.collect-expired"));
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
                .creator(player -> ItemManager.createItemFromNote(note, player, true))
                .consumer(Sounds::click);
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMyAuctions)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(0,currentSort,c), p);
                });
    }
    private InventoryButton collectItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.collectExpiredItem)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    //check if inventory is full
                    if(p.getInventory().firstEmpty() == -1){
                        p.sendMessage(Messages.getFormatted("chat.inventory-full"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    Sounds.experience(event);
                    //expired by a moderator:
                    if(note.getAdminMessage() != null && !note.getAdminMessage().isEmpty()) {
                        if(note.getItem().equals(ItemManager.createDirt())) {
                            p.sendMessage(Messages.getFormatted("chat.deleted-auction-by-admin", "%reason%", note.getAdminMessage()));
                        }else {
                            p.sendMessage(Messages.getFormatted("chat.expired-auction-by-admin", "%reason%", note.getAdminMessage()));
                            p.getInventory().addItem(note.getItem());
                        }
                        NoteStorage.deleteNote(note);
                        p.closeInventory();
                    } else {
                        p.getInventory().addItem(note.getItem());
                        NoteStorage.deleteNote(note); //delete it first, before opening the new GUI!!
                        AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(0, currentSort, c), p);
                    }

                    try {
                        NoteStorage.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}

