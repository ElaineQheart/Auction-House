package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilHandler;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import me.elaineqheart.auctionHouse.data.ram.*;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AuctionViewGUI extends InventoryGUI implements Runnable{

    private final ItemNote note;
    private UUID invID = UUID.randomUUID();
    private final AhConfiguration c;
    private double bid;
    private boolean topBid;
    private final AhConfiguration.View goBackTo;

    public static Map<Player, AuctionViewGUI> currentGUIs = new HashMap<>();

    @Override
    public void run() {
        decorate(c.getPlayer());
    }

    public void update() {
        TaskManager.cancelTask(invID);
        Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), () -> decorate(c.getPlayer()));
        invID = UUID.randomUUID();
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    public AuctionViewGUI(ItemNote note, AhConfiguration configuration, double bid, AhConfiguration.View backTo) {
        super();
        this.note = note;
        c = configuration;
        this.goBackTo = backTo;
        c.setView(AhConfiguration.View.AUCTION_VIEW);
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
        this.bid = bid;
        if(this.bid == 0) this.bid = note.hasBidHistory() ? Bid.nextMinBid(note.getPrice()) : note.getPrice();
        currentGUIs.put(c.getPlayer(), this);
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.auction-view"));
    }

    @Override
    public void decorate(Player player) {
        topBid = Objects.equals(note.getLastBidder(), player.getUniqueId());
        double newBid = note.hasBidHistory() ? Bid.nextMinBid(note.getPrice()) : note.getPrice();
        if(this.bid < newBid) this.bid = newBid;
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # # # . # # # #",
                "# # # # # # # # #",
                "# # # # # # # # #",
                "# # # # # # # # #",
                "# # # # # # # # #"
        },fillerItem());
        this.addButton(13, buyingItem());

        if(note.isBIDAuction()) decorateBID(player);
        else decorateBIN(player);

        if(c.shouldKeepOpen()) this.addButton(49, back());
        super.decorate(player);
    }

    private void decorateBID(Player player) {
        this.addButton(33, bidHistory());
        if(topBid) {
            this.addButton(29, topBid());
            return;
        }
        double increase = bid - note.getBid(player);
        if(note.getPlayerUUID().equals(player.getUniqueId())) {
            if(!note.hasBidHistory()) this.addButton(31, cancelAuction());
            this.addButton(29, submitBid());
            return;
        }
        if(VaultHook.getEconomy().getBalance(player) < increase) {
            this.addButton(29, cannotAffordBid());
        }else{
            this.addButton(29, submitBid());
            this.addButton(31, bidExplanation());
        }
    }

    private void decorateBIN(Player player) {
        int slot = SettingManager.partialSelling && note.getCurrentAmount() > 1 ? 30 : 31;
        if(VaultHook.getEconomy().getBalance(player) < note.getPrice()) {
            this.addButton(slot,armadilloScute());
        }else{
            this.addButton(slot,turtleScute());
        }
        if(slot == 30) {
            this.addButton(32,sign());
        }
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
        currentGUIs.remove(c.getPlayer());
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
    private InventoryButton armadilloScute() {
        return new InventoryButton()
                .creator(player -> ItemManager.createArmadilloScute(note.getPrice()))
                .consumer(Sounds::villagerDeny);
    }
    private InventoryButton turtleScute() {
        return new InventoryButton()
                .creator(player -> ItemManager.createTurtleScute(note.getCurrentPrice()))
                .consumer(event -> {
                    Sounds.click(event);
                    if(note.getPlayerName().equals(event.getWhoClicked().getName())) {
                        event.getWhoClicked().sendMessage(Messages.getFormatted("chat.own-auction"));
                        return;
                    }
                    ItemStack item = note.getItem();
                    item.setAmount(note.getCurrentAmount());
                    AuctionHouse.getGuiManager().openGUI(new ConfirmBuyGUI(note, c, item), (Player) event.getWhoClicked());
                });
    }
    private InventoryButton sign() {
        return new InventoryButton()
                .creator(player -> ItemManager.chooseItemBuyAmount)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    if(note.getPlayerName().equals(p.getName())) {
                        p.sendMessage(Messages.getFormatted("chat.own-auction"));
                        return;
                    }
                    AnvilHandler handler = new AnvilHandler() {
                        public void execute(Player p, String typedText) {
                            try {
                                int amount = Integer.parseInt(typedText);
                                if (amount <= 0 || amount > note.getCurrentAmount()) throw new RuntimeException();
                                if (note.getPrice() / note.getItem().getAmount() * amount > VaultHook.getEconomy().getBalance(p)) {
                                    AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, goBackTo), p);
                                    p.sendMessage(Messages.getFormatted("chat.not-enough-money"));
                                    Sounds.villagerDeny(event);
                                    return;
                                }
                                ItemStack item = note.getItem();
                                item.setAmount(amount);
                                AuctionHouse.getGuiManager().openGUI(new ConfirmBuyGUI(note, c, item), p);
                            } catch (Exception e) {
                                AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, goBackTo), p);
                                p.sendMessage(Messages.getFormatted("chat.invalid-amount"));
                                Sounds.villagerDeny(event);
                            }
                        }
                        public void onClose(Player p) {
                            Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () ->
                                    AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, goBackTo), c.getPlayer()),1);
                        }
                    };
                    AuctionHouse.getAnvilManager().open(p, "inventory-titles.anvil-set-amount", handler);
                });
    }

    private InventoryButton bidHistory() {
        return new InventoryButton()
                .creator(player -> ItemManager.createBidHistory(note.getBidHistoryList()))
                .consumer(event -> {});
    }
    private InventoryButton bidExplanation() {
        return new InventoryButton()
                .creator(player -> ItemManager.createBidExplanation(bid))
                .consumer(event -> {
                    Sounds.click(event);
                    AnvilHandler handler = new AnvilHandler() {
                        public void execute(Player p, String typedText) {
                            double amount = StringUtils.parsePositiveNumber(typedText);
                            if (amount <= bid) {
                                p.sendMessage(Messages.getFormatted("chat.invalid-amount"));
                                Sounds.villagerDeny(event);
                                AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, goBackTo), p);
                                return;
                            }
                            if (amount > VaultHook.getEconomy().getBalance(p)) {
                                p.sendMessage(Messages.getFormatted("chat.not-enough-money"));
                                Sounds.villagerDeny(event);
                                AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, goBackTo), p);
                            } else {
                                bid = amount;
                                AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, bid, goBackTo), p);
                            }
                        }
                        public void onClose(Player p) {
                            Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () ->
                                    AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, bid, goBackTo), c.getPlayer()),1);
                        }
                    };
                    AuctionHouse.getAnvilManager().open(c.getPlayer(), "inventory-titles.anvil-set-bid", handler);
                });
    }
    private InventoryButton submitBid() {
        return new InventoryButton()
                .creator(player -> note.getPlayerUUID().equals(player.getUniqueId()) ?
                        ItemManager.createOwnBid(bid) : ItemManager.createSubmitBid(bid, note.getBid(player)))
                .consumer(event -> {
                    Sounds.click(event);
                    if(note.getPlayerName().equals(event.getWhoClicked().getName())) {
                        event.getWhoClicked().sendMessage(Messages.getFormatted("chat.own-auction"));
                        return;
                    }
                    if(note.isExpired()) {
                        event.getWhoClicked().sendMessage(Messages.getFormatted("chat.expired"));
                        return;
                    }
                    AuctionHouse.getGuiManager().openGUI(new ConfirmBidGUI(note, c, bid, goBackTo == AhConfiguration.View.AUCTION_HOUSE), c.getPlayer());
                });
    }
    private InventoryButton cannotAffordBid() {
        return new InventoryButton()
                .creator(player -> ItemManager.createCannotAffordBid(bid))
                .consumer(Sounds::villagerDeny);
    }
    private InventoryButton topBid() {
        return new InventoryButton()
                .creator(player -> ItemManager.createTopBid(note.getPrice(), bid))
                .consumer(event -> c.getPlayer().sendMessage(Messages.getFormatted("chat.already-top-bid")));
    }
    private InventoryButton cancelAuction() {
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
                    if (note.hasBidHistory()) {
                        p.sendMessage(Messages.getFormatted("chat.already-sold3"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    Sounds.experience(event);
                    Sounds.breakWood(event);
                    p.getInventory().addItem(note.getItem());
                    ItemNoteStorage.deleteNote(note);
                    openGUI(p);
                    p.sendMessage(Messages.getFormatted("chat.auction-canceled"));
                });
    }

    private void openGUI(Player p) {
        if (goBackTo == AhConfiguration.View.AUCTION_HOUSE) AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
        else if (goBackTo == AhConfiguration.View.MY_AUCTIONS) AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), p);
        else if (goBackTo == AhConfiguration.View.MY_BIDS) AuctionHouse.getGuiManager().openGUI(new MyBidsGUI(c,0), p);
    }

}
