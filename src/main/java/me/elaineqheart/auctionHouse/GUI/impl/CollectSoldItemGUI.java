package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class CollectSoldItemGUI extends InventoryGUI {

    private final ItemNote note;
    private final ItemStack item;
    private final AhConfiguration c;
    private final double price;

    public CollectSoldItemGUI(ItemNote note, AhConfiguration configuration) {
        super();
        this.note = note;
        price = note.getSoldPrice();
        item =  ItemManager.createCollectingItemFromNote(note);
        c = configuration;
        c.view = AhConfiguration.View.COLLECT_SOLD_ITEM;
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
                .creator(player -> item)
                .consumer(Sounds::click);
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
                .creator(player -> ItemManager.collectSoldItem(StringUtils.formatNumber(price)))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    if(!ItemNoteStorage.r()) {
                        collect(p, note.getNoteID().toString(), item.getAmount(), price);
                        Sounds.experience(event);
                        AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), p);
                        p.sendMessage(Messages.getFormatted("chat.collect-sold-auction",
                                "%price%", StringUtils.formatPrice(getProfit(price)),
                                "%amount%", String.valueOf(item.getAmount())));
                    } else {

                    }
                });
    }

    public static void collect(OfflinePlayer p, String noteID, int itemAmount, double price) {
        Economy eco = VaultHook.getEconomy();
        eco.depositPlayer(p, getProfit(price));
        ItemNote note = ItemNoteStorage.getNote(noteID);
        if (note.getPartiallySoldAmountLeft() != 0) {
            ItemNoteStorage.setPrice(note, note.getPrice() - price);
            ItemStack temp = note.getItem().clone();
            temp.setAmount(note.getItem().getAmount() - itemAmount);
            ItemNoteStorage.setItem(note, temp);
            if (note.getPartiallySoldAmountLeft() == note.getItem().getAmount()) {
                ItemNoteStorage.setPartiallySoldAmountLeft(note, 0);
                ItemNoteStorage.setSold(note, false);
                ItemNoteStorage.setBuyerName(note, null);
            }
        } else {
            ItemNoteStorage.deleteNote(note);
        }
        try {
            ItemNoteStorage.saveNotes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static double getProfit(double price) {
        return (double) ((int) (price * 100 * (1 - SettingManager.taxRate))) /100;
    }

}
