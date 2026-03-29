package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BundleViewGUI extends InventoryGUI {
    private final AhConfiguration c;
    private final ItemNote note;
    private final AhConfiguration.View goBackTo;
    private int page;
    private final Inventory myInv;

    public BundleViewGUI(ItemNote note, AhConfiguration configuration, AhConfiguration.View goBackTo) {
        super(BundleViewGUI.create(note));
        myInv = super.getInventory();
        c = configuration;
        this.note = note;
        this.goBackTo = goBackTo;
        this.page = 0;
        Sounds.openBundle(c.getPlayer());
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
            Sounds.closeBundle(event);
            ShulkerViewGUI.openSwitch(c, note, p, goBackTo);
        },0);
    }

    @Override
    protected Inventory createInventory() {return null;}

    private static Inventory create(ItemNote note) {
        List<ItemStack> items = ((BundleMeta) note.getItem().getItemMeta()).getItems();
        return Bukkit.createInventory(null, Math.min((items.size()/9+1)*9, 54), note.getItemName());
    }

    private void update() {
        decorate(c.getPlayer());
    }

    @Override
    public void decorate(Player player) {
        List<ItemStack> items = ((BundleMeta) note.getItem().getItemMeta()).getItems();
        if (items.size() < 54) myInv.setContents(items.toArray(new ItemStack[0]));
        else {
            myInv.setContents(items.stream().skip(page * 45L).limit(45).toList().toArray(new ItemStack[0]));
            int[] fillers = {45,46,47,49,51,52,53};
            for (int fill : fillers) this.addButton(fill, fillerItem());
            this.addButton(48, previousPage());
            this.addButton(50, nextPage());
        }
        super.decorate(player);
    }

    private InventoryButton nextPage(){
        ItemStack item = ConfigManager.layout.getItem("n");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.next-page.name"));
        meta.setLore(M.getLoreList("items.next-page.lore",
                "%page%", String.valueOf(page+1),
                "%pages%", String.valueOf(2)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(page == 1) return;
                    if(event.isRightClick()) page = 1; else page++;
                    Sounds.click(event);
                    update();
                });
    }
    private InventoryButton previousPage(){
        ItemStack item = ConfigManager.layout.getItem("p");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.previous-page.name"));
        meta.setLore(M.getLoreList("items.previous-page.lore",
                "%page%", String.valueOf(page+1),
                "%pages%", String.valueOf(2)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(page == 0) return;
                    if(event.isRightClick()) page = 0; else page--;
                    Sounds.click(event);
                    update();
                });
    }
    private InventoryButton fillerItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }
}
