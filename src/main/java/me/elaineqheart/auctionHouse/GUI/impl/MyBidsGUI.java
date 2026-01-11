package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Layout;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Permissions;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.listeners.PlayerJoinCollectListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MyBidsGUI extends InventoryGUI implements Runnable {

    private UUID invID = UUID.randomUUID();
    private final AhConfiguration c;
    private int noteSize;
    private int screenSize;
    private final int rows;
    private int page;

    @Override
    public void run() {
        decorate(c.getPlayer());
    }

    public MyBidsGUI(AhConfiguration c, int page) {
        super(Bukkit.createInventory(null,9 * switch (AuctionHouseStorage.getMyBids(c.getPlayer().getUniqueId()).size()/7) {
            case 0 -> 3;
            case 1 -> 4;
            case 2 -> 5;
            default -> 6;
        }, Messages.getFormatted("inventory-titles.my-bids")));
        this.c = c;
        this.c.setView(AhConfiguration.View.MY_BIDS);
        this.page = page;
        rows = getInventory().getSize()/9;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(player);
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

    @Override
    protected Inventory createInventory() {return null;}

    private void update() {
        decorate(c.getPlayer());
        TaskManager.cancelTask(invID);
        invID = UUID.randomUUID();
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    private void createButtonsForAuctionItems(List<ItemNote> myAuctions, List<Integer> itemSlots) {
        noteSize = myAuctions.size();
        screenSize = itemSlots.size();
        int start = page * screenSize;
        int stop = start + screenSize;
        int end = Math.min(noteSize, stop);
        myAuctions = myAuctions.subList(start, end);
        int size = myAuctions.size();
        for(int i = 0; i < screenSize; ++i){
            int j = itemSlots.get(i);
            if(size-1<i) {
                if (Permissions.getAuctionSlots(c.getPlayer()) <= i) continue;
                this.addButton(j, new InventoryButton()
                        .creator(player -> null)
                        .consumer(event -> {}));
                continue;
            }
            ItemNote note = myAuctions.stream().skip(i).findFirst().orElse(null);
            if(note == null) continue;
            this.addButton(j,auctionItem(note));
        }
    }

    private InventoryButton auctionItem(ItemNote note){
        ItemStack item = ItemManager.createItemFromNote(note, c.getPlayer(), true);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(ItemManager.isShulkerBox(item) && event.isRightClick()) {
                        Sounds.openShulker(event);
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c, AhConfiguration.View.MY_AUCTIONS), c.getPlayer());
                        return;
                    }
                    Sounds.click(event);
                    if(note.isExpired()) {
                        AuctionHouse.getGuiManager().openGUI(new EndedAuctionGUI(note, c, AhConfiguration.View.MY_BIDS), c.getPlayer());
                    } else {
                        AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, AhConfiguration.View.MY_BIDS), c.getPlayer());
                    }

//                    if(!SettingManager.autoCollect) return;
//                    Player p = Bukkit.getPlayer(note.getPlayerUUID());
//                    if(p != null) PlayerJoinCollectListener.sell(note, p);
                });
    }

    private void fillOutPlaces(Player player){
        List<String> places = new ArrayList<>();
        places.add("# # # # # # # # #");
        for(int i = 0; i < rows-2; i++) {places.add("# . . . . . . . #");}
        places.add("# # # # b # # # #");
        List<Integer> itemSlots = new ArrayList<>();
        for(int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                if (places.get(i).charAt(j) == '.') itemSlots.add(i*9+j/2);
            }
        }
        List<ItemNote> bidList = AuctionHouseStorage.getMyBids(player.getUniqueId());
        createButtonsForAuctionItems(bidList, itemSlots);
        for(int i = 0; i < places.size(); i++){
            for(int j = 0; j < places.get(i).length(); j+=2){
                int slot = i*9+j/2;
                switch (places.get(i).charAt(j)) {
                    case '#' -> this.addButton(slot, fillerItem());
                    case 'b' -> this.addButton(slot, back());
                }
            }
        }
        if(bidList.size() > screenSize) {
            this.addButton(45, previousPage());
            this.addButton(53, nextPage());
        }
    }

    private InventoryButton fillerItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }

    private InventoryButton back(){
        return new InventoryButton()
                .creator(player -> ItemManager.backToMyAuctions)
                .consumer(event -> {
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), c.getPlayer());
                });
    }
    private InventoryButton info(){
        return new InventoryButton()
                .creator(player -> ItemManager.info)
                .consumer(event -> {});
    }

    private InventoryButton nextPage(){
        int pages = (noteSize-1)/screenSize;
        ItemStack item = Layout.getItem("n");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore",
                "%page%", String.valueOf(page+1),
                "%pages%", String.valueOf(pages+1)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(page == pages) return;
                    if(event.isRightClick()) page = pages; else page++;
                    Sounds.click(event);
                    update();
                });
    }
    private InventoryButton previousPage(){
        ItemStack item = Layout.getItem("p");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.previous-page.name"));
        meta.setLore(Messages.getLoreList("items.previous-page.lore",
                "%page%", String.valueOf(page+1),
                "%pages%", String.valueOf((noteSize-1)/screenSize+1)));
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

}
