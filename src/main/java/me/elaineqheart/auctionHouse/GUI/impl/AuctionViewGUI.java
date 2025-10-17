package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilSearchGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import me.elaineqheart.auctionHouse.data.yml.SettingManager;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionViewGUI extends InventoryGUI implements Runnable{

    private final ItemNote note;
    private final UUID invID = UUID.randomUUID();
    private final AhConfiguration c;

    @Override
    public void run() {
        decorate(c.currentPlayer);
    }

    public AuctionViewGUI(ItemNote note, AhConfiguration configuration) {
        super();
        this.note = note;
        c = configuration;
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.auction-house"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # # # . # # # #",
                "# # # # # # # # #",
                "# # # # # # # # #",
                "# # # # # # # # #",
                "# # # # . # # # #"
        },fillerItem());
        this.addButton(13, buyingItem());
        int slot = SettingManager.partialSelling && note.getCurrentAmount() > 1 ? 30 : 31;
        if(VaultHook.getEconomy().getBalance(player) < note.getPrice()) {
            this.addButton(slot,armadilloScute());
        }else{
            this.addButton(slot,turtleScute());
        }
        if(slot == 30) {
            this.addButton(32,sign());
        }
        this.addButton(49, back());
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
        return new InventoryButton()
                .creator(player -> ItemManager.createItemFromNote(note, player, false))
                .consumer(event -> {});
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
                });
    }
    private InventoryButton armadilloScute() {
        return new InventoryButton()
                .creator(player -> ItemManager.createArmadilloScute(StringUtils.formatNumber(note.getPrice())))
                .consumer(Sounds::villagerDeny);
    }
    private InventoryButton turtleScute() {
        return new InventoryButton()
                .creator(player -> ItemManager.createTurtleScute(StringUtils.formatNumber(note.getCurrentPrice())))
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
                    new AnvilSearchGUI(p, AnvilSearchGUI.SearchType.SET_AMOUNT, note, c);
                });
    }

}
