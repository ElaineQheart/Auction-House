package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.List;

// The main menu opened by /ah with no arguments (config.yml: hub-menu).
// 3 tiles: Shop (AuctionHouseGUI), History (HistoryGUI) and Sell (SellGUI).
public class HubGUI extends InventoryGUI {

    private final AhConfiguration c;

    public HubGUI(Player p) {
        super();
        this.c = AhConfiguration.getInstance(p);
        init();
    }

    public HubGUI(AhConfiguration configuration) {
        super();
        this.c = configuration;
        init();
    }

    // what /ah and the NPC open: the main menu, or the shop directly (config.yml: hub-menu)
    public static void openMainMenu(Player p) {
        if (ConfigManager.bannedPlayers.checkIsBannedSendMessage(p)) return;
        AuctionHouse.getGuiManager().openGUI(SettingManager.hubMenu ? new HubGUI(p) : new AuctionHouseGUI(p), p);
    }

    private void init() {
        c.setView(AhConfiguration.View.HUB);
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, ConfigManager.layout.hubLayout.size() * 9, M.getFormatted("inventory-titles.hub"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(ConfigManager.layout.hubLayout);
        super.decorate(player);
    }

    private void fillOutPlaces(List<String> places) {
        for (int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                int slot = i * 9 + j / 2;
                switch (places.get(i).charAt(j)) {
                    case '#' -> this.addButton(slot, fillerItem());
                    case 'S' -> this.addButton(slot, shopButton());
                    case 'H' -> this.addButton(slot, historyButton());
                    case 'V' -> this.addButton(slot, sellButton());
                    case '.', ' ' -> {}
                }
            }
        }
    }

    private InventoryButton fillerItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }

    private InventoryButton shopButton() {
        return new InventoryButton()
                .creator(player -> ItemManager.hubShop)
                .consumer(event -> {
                    Sounds.openEnderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.getPlayer());
                });
    }

    private InventoryButton historyButton() {
        return new InventoryButton()
                .creator(player -> ItemManager.hubHistory)
                .consumer(event -> {
                    Sounds.openEnderChest(event);
                    HistoryGUI.open(c.getPlayer());
                });
    }

    private InventoryButton sellButton() {
        return new InventoryButton()
                .creator(player -> ItemManager.hubSell)
                .consumer(event -> {
                    Sounds.openEnderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new SellGUI(c.getPlayer()), c.getPlayer());
                });
    }

}
