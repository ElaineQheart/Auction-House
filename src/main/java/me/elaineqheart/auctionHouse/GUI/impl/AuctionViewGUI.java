package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.vault.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class AuctionViewGUI extends InventoryGUI implements Runnable{

    private final ItemNote note;
    private final Player currentPlayer;
    private final UUID invID = UUID.randomUUID();

    @Override
    public void run() {
        decorate(currentPlayer);
    }

    public AuctionViewGUI(ItemNote note, Player player) {
        super();
        this.note = note;
        this.currentPlayer = player;
        TaskManager.addTaskID(invID, Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9,"Auction House");
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
        if(note.canAfford(VaultHook.getEconomy().getBalance(player))) {
            this.addButton(31,turtleScute());
        }else{
            this.addButton(31,armadilloScute());
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
                .creator(player -> ItemManager.createItemFromNote(note, player))
                .consumer(event -> {});
    }
    private InventoryButton back() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(p), p);
                });
    }
    private InventoryButton armadilloScute() {
        return new InventoryButton()
                .creator(player -> ItemManager.createArmadilloScute(StringUtils.formatNumber(note.getPrice(),0)))
                .consumer(Sounds::villagerDeny);
    }
    private InventoryButton turtleScute() {
        return new InventoryButton()
                .creator(player -> ItemManager.createTurtleScute(StringUtils.formatNumber(note.getPrice(),0)))
                .consumer(event -> {
                    Sounds.click(event);
                if(note.getPlayerName().equals(event.getWhoClicked().getName())) {
                    event.getWhoClicked().sendMessage("This is your own auction! You cannot buy it.");
                    return;
                }
                AuctionHouse.getGuiManager().openGUI(new ConfirmBuyGUI(note), (Player) event.getWhoClicked());
                });
    }

}
