package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.SettingManager;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class ConfirmBuyGUI extends InventoryGUI{

    private final ItemNote note;

    public ConfirmBuyGUI(ItemNote note) {
        super();
        this.note = note;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,3*9,"Auction House");
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # . # . # . # #",
                "# # # # # # # # #"
        },fillerItem());
        this.addButton(11, confirm());
        this.addButton(13, buyingItem());
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
    private InventoryButton buyingItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.createBuyingItemDisplay(note))
                .consumer(event -> {});
    }
    private InventoryButton confirm(){
        return new InventoryButton()
                .creator(player -> ItemManager.createConfirm(StringUtils.formatNumber(note.getPrice(),0)))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    //check if inventory is full
                    if(p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(ChatColor.RED + "Your inventory is full!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    //check if the item hasn't been sold yet
                    if (note.isSold()) {
                        p.sendMessage(ChatColor.RED + "This item has already been sold!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if (ItemNoteStorageUtil.noteDoesNotExist(note)) {
                        p.sendMessage(ChatColor.RED + "This item has been removed!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    Economy eco = VaultHook.getEconomy();
                    double price = note.getPrice();
                    p.closeInventory();
                    if(!note.canAfford(eco.getBalance(p))) { //extra check to make sure that they have enough coins
                        p.sendMessage(ChatColor.RED + "You don't have enough money to buy this item!");
                        Sounds.villagerDeny(event);
                        return;
                    }
                    eco.withdrawPlayer(p, price);
                    Sounds.experience(event);
                    p.getInventory().addItem(note.getItem());
                    note.setSold(true);
                    note.setBuyerName(p.getName());
                    try {
                        ItemNoteStorageUtil.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                    p.sendMessage(ChatColor.YELLOW + "You purchased an item from " + ChatColor.GRAY + note.getPlayerName() + ChatColor.YELLOW + "'s auction!");
                    p.sendMessage(ChatColor.AQUA + "-------------------------------------------------");
                    Player seller = Bukkit.getPlayer(note.getPlayerName());
                    if(SettingManager.soldMessageEnabled && seller != null && Bukkit.getOnlinePlayers().contains(seller)) {
                        TextComponent component = new TextComponent(ChatColor.GOLD + "[Auction] " + ChatColor.GRAY + p.getName() + ChatColor.YELLOW + " bought " +
                                 StringUtils.getItemName(note.getItem()) + ChatColor.YELLOW + " for " + StringUtils.formatPrice(price, 0) + " ");
                        TextComponent click = new TextComponent(ChatColor.WHITE + "" + ChatColor.BOLD + "CLICK");
                        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ah view " + note.getNoteID().toString()));
                        seller.spigot().sendMessage(component,click);
                    }
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
