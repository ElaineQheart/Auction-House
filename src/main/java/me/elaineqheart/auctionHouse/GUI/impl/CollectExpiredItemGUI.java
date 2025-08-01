package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class CollectExpiredItemGUI extends InventoryGUI {

    private final ItemNote note;
    private final MyAuctionsGUI.MySort currentSort;

    public CollectExpiredItemGUI(ItemNote note, MyAuctionsGUI.MySort sort) {
        super();
        this.note = note;
        this.currentSort = sort;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9,"Collect Expired Item");
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
                .creator(player -> ItemManager.createItemFromNote(note, player))
                .consumer(Sounds::click);
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMyAuctions)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(0,currentSort,p), p);
                });
    }
    private InventoryButton collectItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.collectExpiredItem)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    //check if inventory is full
                    if(p.getInventory().firstEmpty() == -1){
                        p.sendMessage(ChatColor.RED + "Your inventory is full!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    Sounds.experience(event);
                    //expired by a moderator:
                    if(note.getAdminMessage() != null) {
                        if(note.getItem().equals(ItemManager.createDirt())) {
                            p.sendMessage(ChatColor.DARK_RED + "-------------------------------------------------");
                            p.sendMessage(ChatColor.RED + "Yor auction was deleted by a moderator");
                            p.sendMessage(ChatColor.GRAY + "Reason: " + note.getAdminMessage());
                            p.sendMessage(ChatColor.DARK_RED + "-------------------------------------------------");
                            p.closeInventory();
                        }else {
                            p.sendMessage(ChatColor.DARK_RED + "-------------------------------------------------");
                            p.sendMessage(ChatColor.RED + "Your auction was expired by a moderator");
                            p.sendMessage(ChatColor.GRAY + "Reason: " + note.getAdminMessage());
                            p.sendMessage(ChatColor.DARK_RED + "-------------------------------------------------");
                            p.getInventory().addItem(note.getItem());
                            ItemNoteStorageUtil.deleteNote(note);
                            p.closeInventory();
                        }
                    } else {
                        p.getInventory().addItem(note.getItem());
                        ItemNoteStorageUtil.deleteNote(note); //delete it first, before opening the new GUI!!
                        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () ->
                                AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(0,currentSort,p), p)
                        ,1);
                    }

                    try {
                        ItemNoteStorageUtil.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}

