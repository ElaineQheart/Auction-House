package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Layout;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MyAuctionsGUI extends InventoryGUI implements Runnable{

    private UUID invID = UUID.randomUUID();
    private final AhConfiguration c;
    private int noteSize;
    private int screenSize;

    @Override
    public void run() {
        decorate(c.currentPlayer);
    }

    public enum MySort{
        ALL_AUCTIONS,
        SOLD_ITEMS,
        EXPIRED_ITEMS,
        ACTIVE_AUCTIONS
    }

    public MyAuctionsGUI(AhConfiguration configuration) {
        super();
        c = configuration;
        c.view = AhConfiguration.View.MY_AUCTIONS;
        if(c.myCurrentSort == null) c.myCurrentSort = MySort.ALL_AUCTIONS;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.my-auctions"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(Layout.myAhLayout, player);
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

    private void update() {
        decorate(c.currentPlayer);
        TaskManager.cancelTask(invID);
        invID = UUID.randomUUID();
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    private void fillOutItems(UUID playerID, List<Integer> itemSlots){
        List<ItemNote> myAuctions = ItemNoteStorage.mySortedDateCreated(playerID);
        List<ItemNote> returnList;
        switch (c.myCurrentSort){
            case SOLD_ITEMS -> returnList = myAuctions.stream()
                        .filter(ItemNote::isSold)
                        .collect(Collectors.toList());
            case EXPIRED_ITEMS -> returnList = myAuctions.stream()
                        .filter(note -> note.isExpired() && !note.isSold())
                        .collect(Collectors.toList());
            case ACTIVE_AUCTIONS -> returnList = myAuctions.stream()
                        .filter(note -> !note.isExpired() && note.isOnAuction())
                        .collect(Collectors.toList());
            default -> returnList = myAuctions;
        }
        createButtonsForAuctionItems(returnList, itemSlots);
    }

    private void createButtonsForAuctionItems(List<ItemNote> myAuctions, List<Integer> itemSlots) {
        noteSize = myAuctions.size();
        screenSize = itemSlots.size();
        int start = c.myCurrentPage * screenSize;
        int stop = start + screenSize;
        int end = Math.min(noteSize, stop);
        myAuctions = myAuctions.subList(start, end);
        int size = myAuctions.size();
        for(int i = 0; i < screenSize; ++i){
            int j = itemSlots.get(i);
            if(size-1<i) {
                if (Permissions.getAuctionSlots(c.currentPlayer) <= i) continue;
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
        ItemStack item = ItemManager.createItemFromNote(note, c.currentPlayer, true);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(ItemManager.isShulkerBox(item) && event.isRightClick()) {
                        Sounds.openShulker(event);
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c), c.currentPlayer);
                        return;
                    }
                    Sounds.click(event);
                    if(note.isSold()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note, c), c.currentPlayer);
                    } else if (note.isExpired()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectExpiredItemGUI(note, c), c.currentPlayer);
                    } else {
                        AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note, c), c.currentPlayer);
                    }
                });
    }

    private void fillOutPlaces(List<String> places, Player player){
        List<Integer> itemSlots = new ArrayList<>();
        for(int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                if (places.get(i).charAt(j) == '.') itemSlots.add(i*9+j/2);
            }
        }
        fillOutItems(player.getUniqueId(), itemSlots);
        fillOutBarriers(Permissions.getAuctionSlots(player), itemSlots);
        for(int i = 0; i < places.size(); i++){
            for(int j = 0; j < places.get(i).length(); j+=2){
                int slot = i*9+j/2;
                switch (places.get(i).charAt(j)) {
                    case '#' -> this.addButton(slot, fillerItem());
                    case 'b' -> this.addButton(slot, back());
                    case 'o' -> this.addButton(slot, sortButton(ItemManager.getMySort(c.myCurrentSort)));
                    case 'p' -> {
                        if(Permissions.getAuctionSlots(player) > screenSize) this.addButton(slot, previousPage()); else this.addButton(slot, fillerItem());
                    }
                    case 'n' -> {
                        if(Permissions.getAuctionSlots(player) > screenSize) this.addButton(slot, nextPage()); else this.addButton(slot, fillerItem());
                    }
                    case 'r' -> this.addButton(slot, refresh());
                    case 'i' -> this.addButton(slot, info());
                }
            }
        }
    }
    private void fillOutBarriers(int auctions, List<Integer> itemSlots) {
        int startPage = c.myCurrentPage*screenSize + screenSize;
        int barriers = startPage - auctions;
        for(int i = 0; i < barriers; i++){
            this.addButton(itemSlots.get(screenSize-i-1), barrier());
        }
    }
    private InventoryButton fillerItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }
    private InventoryButton barrier() {

        return new InventoryButton()
                .creator(player -> ItemManager.lockedSlot)
                .consumer(event -> {
                });
    }

    private InventoryButton refresh(){
        return new InventoryButton()
                .creator(player -> ItemManager.refresh)
                .consumer(event -> {
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), c.currentPlayer);
                });
    }
    private InventoryButton loading(){
        return new InventoryButton()
                .creator(player -> ItemManager.loading)
                .consumer(event -> {});
    }
    private InventoryButton sortButton(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()) c.myCurrentSort = previousSort(c.myCurrentSort);
                    else c.myCurrentSort = nextSort(c.myCurrentSort);
                    c.myCurrentPage = 0;
                    update();
                });
    }
    private InventoryButton back(){
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Sounds.closeEnderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                });
    }
    private InventoryButton info(){
        return new InventoryButton()
                .creator(player -> ItemManager.info)
                .consumer(event -> {});
    }

    private MySort nextSort(MySort input){
        if(input.equals(MySort.ALL_AUCTIONS)) return MySort.SOLD_ITEMS;
        if(input.equals(MySort.SOLD_ITEMS)) return MySort.EXPIRED_ITEMS;
        if(input.equals(MySort.EXPIRED_ITEMS)) return MySort.ACTIVE_AUCTIONS;
        return MySort.ALL_AUCTIONS;
    }
    private MySort previousSort(MySort input){
        if(input.equals(MySort.ACTIVE_AUCTIONS)) return MySort.EXPIRED_ITEMS;
        if(input.equals(MySort.EXPIRED_ITEMS)) return MySort.SOLD_ITEMS;
        if(input.equals(MySort.SOLD_ITEMS)) return MySort.ALL_AUCTIONS;
        return MySort.ACTIVE_AUCTIONS;
    }

    private InventoryButton nextPage(){
        int pages = (noteSize-1)/screenSize;
        ItemStack item = Layout.getItem("next-page");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore",
                "%page%", String.valueOf(c.myCurrentPage+1),
                "%pages%", String.valueOf(pages+1)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.myCurrentPage == pages) return;
                    if(event.isRightClick()) c.myCurrentPage = pages; else c.myCurrentPage++;
                    Sounds.click(event);
                    update();
                });
    }
    private InventoryButton previousPage(){
        ItemStack item = Layout.getItem("previous-page");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.previous-page.name"));
        meta.setLore(Messages.getLoreList("items.previous-page.lore",
                "%page%", String.valueOf(c.myCurrentPage+1),
                "%pages%", String.valueOf((noteSize-1)/screenSize+1)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.myCurrentPage == 0) return;
                    if(event.isRightClick()) c.myCurrentPage = 0; else c.myCurrentPage--;
                    Sounds.click(event);
                    update();
                });
    }

}
