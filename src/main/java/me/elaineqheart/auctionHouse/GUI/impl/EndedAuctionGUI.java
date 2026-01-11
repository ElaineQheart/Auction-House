package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class EndedAuctionGUI extends InventoryGUI implements Runnable{
    private final ItemNote note;
    private final UUID invID = UUID.randomUUID();
    private final AhConfiguration c;
    private final boolean topBid;
    private final AhConfiguration.View goBackTo;

    @Override
    public void run() {
        decorate(c.getPlayer());
    }

    public EndedAuctionGUI(ItemNote note, AhConfiguration configuration, AhConfiguration.View goBackTo) {
        super();
        this.note = note;
        c = configuration;
        c.setView(AhConfiguration.View.ENDED_AUCTION);
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
        topBid = Objects.equals(note.getLastBidder(), c.getPlayer().getUniqueId());
        this.goBackTo = goBackTo;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.auction-view"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # # # . # # # #",
                "# # # # # # # # #",
                "# # . # # # . # #",
                "# # # # # # # # #",
                "# # # # # # # # #"
        },fillerItem());

        this.addButton(13, buyingItem());
        this.addButton(33, bidHistory());
        if(topBid) {
            this.addButton(29, collectItem());
        } else {
            this.addButton(29, collectCoins());
        }

        if(c.shouldKeepOpen()) this.addButton(49, back());
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
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
    private InventoryButton buyingItem() {
        ItemStack item = ItemManager.createItemFromNote(note, c.getPlayer(), false);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(ItemManager.isShulkerBox(item) && event.isRightClick()) {
                        Sounds.openShulker(event);
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c, goBackTo), c.getPlayer());
                    }
                });
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    openGUI(p);
                });
    }

    private InventoryButton bidHistory() {
        return new InventoryButton()
                .creator(player -> ItemManager.createBidHistory(note.getBidHistoryList()))
                .consumer(event -> {});
    }

    private InventoryButton collectItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.createCollectAuction(note))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    if(p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(Messages.getFormatted("chat.inventory-full"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    ItemNote test = AuctionHouseStorage.getNote(note.getNoteID());
                    if (test == null) {
                        p.sendMessage(Messages.getFormatted("chat.non-existent2"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    p.getInventory().addItem(note.getItem());
                    p.sendMessage(Messages.getFormatted("chat.claim-auction",
                            "%item%", note.getItemName(),
                            "%player%", note.getPlayerName()));
                    Sounds.experience(event);

                    ItemNoteStorage.removeBid(p, note);
                    try {
                        ItemNoteStorage.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    openGUI(p);
                });
    }
    private InventoryButton collectCoins() {
        return new InventoryButton()
                .creator(player -> ItemManager.createCollectCoins(note, c.getPlayer()))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    double price = note.getBid(p);
                    if (!note.canClaimBid(p.getUniqueId())) {
                        p.sendMessage(Messages.getFormatted("chat.non-existent2"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    Economy eco = VaultHook.getEconomy();
                    eco.depositPlayer(p, price);
                    p.sendMessage(Messages.getFormatted("chat.collect-coins", price,
                            "%item%", note.getItemName()));
                    Sounds.experience(event);

                    ItemNoteStorage.removeBid(p, note);
                    try {
                        ItemNoteStorage.saveNotes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    openGUI(p);
                });
    }

    private void openGUI(Player p) {
        if (goBackTo == AhConfiguration.View.AUCTION_HOUSE) AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
        else if (goBackTo == AhConfiguration.View.MY_AUCTIONS) AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), p);
        else if (goBackTo == AhConfiguration.View.MY_BIDS) AuctionHouse.getGuiManager().openGUI(new MyBidsGUI(c,0), p);
    }

}
