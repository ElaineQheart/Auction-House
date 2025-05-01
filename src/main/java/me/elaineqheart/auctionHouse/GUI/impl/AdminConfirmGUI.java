package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilSearchGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.ah.ItemManager;
import me.elaineqheart.auctionHouse.ah.ItemNote;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AdminConfirmGUI extends InventoryGUI{

    private final ItemNote note;
    private final String reason;
    private final AnvilSearchGUI.SearchType type;

    public AdminConfirmGUI(String reason, ItemNote note, AnvilSearchGUI.SearchType type) {
        super();
        this.note = note;
        this.reason = reason;
        this.type = type;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,3*9,"Confirm Action");
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # . # . # . # #",
                "# # # # # # # # #"
        },fillerItem());
        if(type == AnvilSearchGUI.SearchType.ITEM_EXPIRE_MESSAGE) {
            this.addButton(11, confirmExpireItem());
            this.addButton(13, expireItem());
        } else if (type == AnvilSearchGUI.SearchType.ITEM_DELETE_MESSAGE) {
            this.addButton(11, confirmDeleteItem());
            this.addButton(13, deleteItem());
        }
        this.addButton(15, cancel());
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
    private InventoryButton deleteItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.createAdminDeleteItem(note, reason))
                .consumer(event -> {
                });
    }
    private InventoryButton expireItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.createAdminExpireItem(note, reason))
                .consumer(event -> {
                });
    }
    private InventoryButton confirmExpireItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.confirm)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    if (note.isSold()) {
                        p.sendMessage(ChatColor.RED + "ERROR! This item has already been sold!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if (ItemNoteStorageUtil.noteDoesNotExist(note)) {
                        p.sendMessage(ChatColor.RED + "ERROR! This item has been removed!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    Sounds.experience(event);
                    Sounds.breakWood(event);
                    p.closeInventory();
                    Date expireDate = new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime();
                    note.setDateCreated(expireDate);
                    note.setAdminMessage(reason);
                    try {
                        ItemNoteStorageUtil.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                    p.sendMessage(ChatColor.GREEN + "Yor successfully expired the auction");
                    p.sendMessage("Reason: " + note.getAdminMessage());
                    p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                });
    }
    private InventoryButton confirmDeleteItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.confirm)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    //check if inventory is full
                    if(p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(ChatColor.RED + "Your inventory is full!");
                        p.sendMessage(ChatColor.RED + "Please empty a slot in order to take the item!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    //check if the item hasn't been sold yet
                    if (note.isSold()) {
                        p.sendMessage(ChatColor.RED + "ERROR! This item has already been sold!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if (ItemNoteStorageUtil.noteDoesNotExist(note)) {
                        p.sendMessage(ChatColor.RED + "ERROR! This item has been removed!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    p.closeInventory();
                    p.getInventory().addItem(note.getItem());
                    Sounds.experience(event);
                    Sounds.breakWood(event);
                    Date expireDate = new GregorianCalendar(2000, Calendar.JANUARY, 1).getTime();
                    note.setDateCreated(expireDate);
                    note.setAdminMessage(reason);
                    note.setItem(ItemManager.createDirt());
                    try {
                        ItemNoteStorageUtil.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                    p.sendMessage(ChatColor.GREEN + "Yor successfully deleted the auction");
                    p.sendMessage("Reason: " + note.getAdminMessage());
                    p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                });
    }
    private InventoryButton cancel(){
        return new InventoryButton()
                .creator(player -> ItemManager.cancel)
                .consumer(event -> {
                    Sounds.click(event);
                    event.getWhoClicked().closeInventory();
                });
    }

}
