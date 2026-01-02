package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class ConfirmBuyGUI extends InventoryGUI{

    private final ItemNote note;
    private final ItemStack item;
    private final AhConfiguration c;
    private final double price;

    public ConfirmBuyGUI(ItemNote note, AhConfiguration configuration, ItemStack item) {
        super();
        this.note = note;
        this.item = item;
        c = configuration;
        price = note.getPrice() / note.getItem().getAmount() * item.getAmount();

    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,3*9, Messages.getFormatted("inventory-titles.auction-house"));
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
                .creator(player -> ItemManager.createBuyingItemDisplay(item.clone()))
                .consumer(event -> {});
    }
    private InventoryButton confirm(){
        return new InventoryButton()
                .creator(player -> ItemManager.createConfirm(StringUtils.formatNumber(price)))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    //check if inventory is full
                    if(p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(Messages.getFormatted("chat.inventory-full"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    String itemName = note.getItemName();

                    if(!ItemNoteStorage.r()) {
                        ItemNote test = AuctionHouseStorage.getNote(note.getNoteID().toString());
                        if (test == null) {
                            p.sendMessage(Messages.getFormatted("chat.non-existent2"));
                            Sounds.villagerDeny(event);
                            return;
                        }
                        if (!test.isOnAuction() || test.getCurrentAmount() < item.getAmount()) {
                            p.sendMessage(Messages.getFormatted("chat.already-sold2"));
                            Sounds.villagerDeny(event);
                            return;
                        }
                        Economy eco = VaultHook.getEconomy();
                        Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), p::closeInventory);
                        if (eco.getBalance(p) < price) { //extra check to make sure that they have enough coins
                            p.sendMessage(Messages.getFormatted("chat.not-enough-money"));
                            Sounds.villagerDeny(event);
                            return;
                        }
                        eco.withdrawPlayer(p, price);
                        Sounds.experience(event);
                        p.getInventory().addItem(item);
                        ItemNoteStorage.setSold(note, true);
                        ItemNoteStorage.setBuyerName(note, p.getName());
                        if (price != note.getPrice()) {
                            if (note.getPartiallySoldAmountLeft() == 0) {
                                ItemNoteStorage.setPartiallySoldAmountLeft(note, note.getItem().getAmount() - item.getAmount());
                            } else {
                                ItemNoteStorage.setPartiallySoldAmountLeft(note, note.getPartiallySoldAmountLeft() - item.getAmount());
                            }
                        }
                        try {
                            ItemNoteStorage.saveNotes();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        p.sendMessage(Messages.getFormatted("chat.purchase-auction", "%player%", note.getPlayerName()));
                        Player seller = Bukkit.getPlayer(note.getPlayerName());
                        if (SettingManager.soldMessageEnabled && seller != null && Bukkit.getOnlinePlayers().contains(seller)) {
                            if(SettingManager.autoCollect) {
                                seller.sendMessage(Messages.getFormatted("chat.sold-message.auto-collect",
                                        "%player%", p.getName(),
                                        "%item%", itemName,
                                        "%price%", StringUtils.formatPrice(price),
                                        "%amount%", String.valueOf(item.getAmount())));
                            } else {
                                TextComponent component = new TextComponent(Messages.getFormatted("chat.sold-message.prefix",
                                        "%player%", p.getName(),
                                        "%item%", itemName,
                                        "%price%", StringUtils.formatPrice(price),
                                        "%amount%", String.valueOf(item.getAmount())));
                                TextComponent click = new TextComponent(Messages.getFormatted("chat.sold-message.interaction"));
                                click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ah view " + note.getNoteID().toString()));
                                seller.spigot().sendMessage(component, click);
                            }
                        }
                        if (SettingManager.autoCollect && Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(note.getPlayerUUID()))) {
                            Bukkit.getScheduler().runTaskAsynchronously(AuctionHouse.getPlugin(), () -> CollectSoldItemGUI.collect
                                    (Bukkit.getOfflinePlayer(note.getPlayerUUID()), note.getNoteID().toString(), item.getAmount(), note.getSoldPrice())
                            );
                        }
                    }
                });
    }
    private InventoryButton cancel(){
        return new InventoryButton()
                .creator(player -> ItemManager.cancel)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
                });
    }

}
