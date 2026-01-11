package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.ram.*;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class ConfirmBidGUI extends InventoryGUI {

    private final ItemNote note;
    private final AhConfiguration c;
    private final double price;
    private final boolean goBackToAuctionHouse;

    public ConfirmBidGUI(ItemNote note, AhConfiguration configuration, double bid, boolean goBackToAuctionHouse) {
        super();
        this.note = note;
        c = configuration;
        price = bid;
        this.goBackToAuctionHouse = goBackToAuctionHouse;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,3*9, Messages.getFormatted("inventory-titles.confirm-bid"));
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
                .creator(player -> ItemManager.createBuyingItemDisplay(note.getItem()))
                .consumer(event -> {});
    }
    private InventoryButton confirm(){
        return new InventoryButton()
                .creator(player -> ItemManager.createConfirm(price - note.getBid(player)))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();

                    ItemNote test = AuctionHouseStorage.getNote(note.getNoteID());
                    if (test == null) {
                        p.sendMessage(Messages.getFormatted("chat.non-existent2"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if(!test.isOnAuction()) {
                        p.sendMessage(Messages.getFormatted("chat.already-sold2"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if(test.isExpired()) {
                        event.getWhoClicked().sendMessage(Messages.getFormatted("chat.expired"));
                        return;
                    }
                    if ((note.hasBidHistory() ? Bid.nextMinBid(note.getPrice()) : note.getPrice()) > price) {
                        p.sendMessage(Messages.getFormatted("chat.already-sold3"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    double increase = price - note.getBid(p);
                    Economy eco = VaultHook.getEconomy();
                    if (eco.getBalance(p) < increase) { //extra check to make sure that they have enough coins
                        p.sendMessage(Messages.getFormatted("chat.not-enough-money"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    eco.withdrawPlayer(p, increase);
                    Sounds.experience(event);
                    ItemNoteStorage.addBid(note, p, price);
                    try {
                        ItemNoteStorage.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    p.sendMessage(Messages.getFormatted("chat.placed-bid", price,
                            "%item%", note.getItemName()));
                    if(c.shouldKeepOpen()) AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, goBackToAuctionHouse ? AhConfiguration.View.AUCTION_HOUSE : AhConfiguration.View.MY_AUCTIONS), p);
                    else Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), p::closeInventory);

                    Set<UUID> bidders = note.getBidders();
                    bidders.remove(p.getUniqueId());
                    for(UUID id : bidders) {
                        Player bidder = Bukkit.getPlayer(id);
                        if(bidder == null) continue;
                        double difference = price - note.getBid(bidder);
                        bidder.sendMessage(Messages.getFormatted("chat.outbid.prefix", difference,
                                "%player%", p.getDisplayName(),
                                "%item%", note.getItemName()));
                        TextComponent click = new TextComponent(Messages.getFormatted("chat.outbid.interaction"));
                        click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ah view " + note.getNoteID().toString()));
                        bidder.spigot().sendMessage(click);
                        if(AuctionViewGUI.currentGUIs.get(bidder) == null) continue;
                        AuctionViewGUI.currentGUIs.get(bidder).update();
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
