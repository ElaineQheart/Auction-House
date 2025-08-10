package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.data.SettingManager;
import me.elaineqheart.auctionHouse.data.Messages;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class CollectSoldItemGUI extends InventoryGUI {

    private final ItemNote note;
    private final MyAuctionsGUI.MySort currentSort;

    public CollectSoldItemGUI(ItemNote note, MyAuctionsGUI.MySort sort) {
        super();
        this.note = note;
        this.currentSort = sort;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.collect-sold"));
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
        this.addButton(13, buyingItem());
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
    private InventoryButton buyingItem() {
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
        double price = (double) ((int) (note.getPrice() * 100 * (1 - SettingManager.taxRate))) /100;
        return new InventoryButton()
                .creator(player -> ItemManager.collectSoldItem(StringUtils.formatNumber(price)))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Economy eco = VaultHook.getEconomy();
                    eco.depositPlayer(p, price);
                    Sounds.experience(event);
                    ItemNoteStorageUtil.deleteNote(note);
                    try {
                        ItemNoteStorageUtil.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(0,currentSort,p), p);
                    p.sendMessage(Messages.getFormatted("chat.collect-sold-auction",
                            "%price%", StringUtils.formatPrice(price)));
                });
    }

}
