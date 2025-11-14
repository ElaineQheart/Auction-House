package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.NoteStorage;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import me.elaineqheart.auctionHouse.data.yml.Permissions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MyAuctionsGUI extends InventoryGUI implements Runnable{

    private int currentPage;
    private MySort currentSort;
    private UUID invID = UUID.randomUUID();
    private final AhConfiguration c;
    private int noteSize;

    @Override
    public void run() {
        decorateItems(c.currentPlayer);
    }

    public enum MySort{
        ALL_AUCTIONS,
        SOLD_ITEMS,
        EXPIRED_ITEMS,
        ACTIVE_AUCTIONS
    }
    public MyAuctionsGUI(int page, MySort sort, AhConfiguration configuration) {
        super();
        this.currentPage = page;
        currentSort = sort;
        c = configuration;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }
    public MyAuctionsGUI(AhConfiguration configuration) {
        super();
        this.currentPage = 0;
        currentSort = MySort.ALL_AUCTIONS;
        c = configuration;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.my-auctions"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                ". . # # . # # # .",
        },fillerItem());
        fillOutBarriers(currentPage, Permissions.getAuctionSlots(player));
        this.addButton(45,back());
        this.addButton(46,sortButton(ItemManager.getMySort(currentSort)));
        this.addButton(49,loading());
        this.addButton(53,info());
        decorateItems(player);
    }
    private void decorateItems(Player player) {
        fillOutItems(player.getUniqueId());
        if(Permissions.getAuctionSlots(player) > 21) {
            this.addButton(48,previousPage(noteSize));
            this.addButton(50,nextPage(noteSize));
        }
        this.addButton(49,refresh());
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

    private void fillOutItems(UUID playerID){
        List<ItemNote> myAuctions = NoteStorage.mySortedDateCreated(playerID);
        List<ItemNote> returnList;
        switch (currentSort){
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
        noteSize = returnList.size();
        createButtonsForAuctionItems(returnList, currentPage);
    }

    private void createButtonsForAuctionItems(List<ItemNote> myAuctions, int page){
        int startPage = page*21;
        int size = myAuctions.size();
        for(int i = startPage; i < startPage+21; ++i){
            int j = i%21+10 + i%21/7 + i%21/7;
            if(size-1<i) {
                if (Permissions.getAuctionSlots(c.currentPlayer) <= startPage + i) continue;
                this.addButton(j, new InventoryButton()
                        .creator(player -> null)
                        .consumer(event -> {}));
                continue;
            }
            ItemNote note = myAuctions.stream().skip(size-i-1).findFirst().orElse(null);
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
                        c.myCurrentSort = currentSort;
                        c.myCurrentPage = currentPage;
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c), c.currentPlayer);
                        return;
                    }
                    Sounds.click(event);
                    if(note.isSold()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note,currentSort, c), c.currentPlayer);
                    } else if (note.isExpired()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectExpiredItemGUI(note,currentSort, c), c.currentPlayer);
                    } else {
                        AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note,currentSort, c), c.currentPlayer);
                    }
                });
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
    private void fillOutBarriers(int currentPage, int auctions) {
        int startPage = currentPage*21 + 21;
        int barriers = startPage - auctions;
        for(int i = 0; i < barriers; i++){
            int j = 34 - i;
            int k = (i/7)*2;
            this.addButton(j-k, barrier());
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
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(currentPage,currentSort,c), c.currentPlayer);
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
                    if(event.isRightClick()) currentSort = previousSort(currentSort);
                    else currentSort = nextSort(currentSort);
                    currentPage = 0;
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

    private InventoryButton nextPage(int auctionItemsAmount){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore",
                "%page%", String.valueOf(currentPage),
                "%pages%", String.valueOf(auctionItemsAmount/21)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        if(currentPage != auctionItemsAmount/21){
                            currentPage = auctionItemsAmount/21;
                            update();
                        }
                    }else {
                        if(currentPage < auctionItemsAmount/21){
                            currentPage++;
                            update();
                        }
                    }
                });
    }
    private InventoryButton previousPage(int auctionItemsAmount){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.previous-page.name"));
        meta.setLore(Messages.getLoreList("items.previous-page.lore",
                "%page%", String.valueOf(currentPage),
                "%pages%", String.valueOf(auctionItemsAmount/21)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        if(currentPage != 0){
                            currentPage = 0;
                            update();
                        }
                    }else {
                        if(currentPage > 0){
                            currentPage--;
                            update();
                        }
                    }
                });
    }

}
