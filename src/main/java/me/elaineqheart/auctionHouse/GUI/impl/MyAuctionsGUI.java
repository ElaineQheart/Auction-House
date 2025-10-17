package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.Permissions;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MyAuctionsGUI extends InventoryGUI implements Runnable{

    private final int currentPage;
    private final MySort currentSort;
    private final UUID invID = UUID.randomUUID();
    private AhConfiguration c;

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
        int auctionItems = fillOutItems(currentPage,currentSort,player.getUniqueId());
        this.addButton(45,back());
        this.addButton(46,sortButton(ItemManager.getMySort(currentSort)));

        this.addButton(49,refresh());
        if(Permissions.getAuctionSlots(player) > 21) {
            this.addButton(48,previousPage(auctionItems));
            this.addButton(50,nextPage(auctionItems));
        }
        this.addButton(53,info());
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

    private int fillOutItems(int page,MySort sort,UUID playerID){
        switch (sort){
            case ALL_AUCTIONS -> {return fillOutAllAuctions(playerID,page);}
            case SOLD_ITEMS -> {return fillOutSoldItems(playerID,page);}
            case EXPIRED_ITEMS -> {return fillOutExpiredItems(playerID,page);}
            case ACTIVE_AUCTIONS -> {return fillOutActiveAuctions(playerID,page);}
        }
        return 0;
    }

    private int fillOutAllAuctions(UUID id, int page){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            //comparing the UUIDs directly doesn't work, because it's not persistent with player rejoins
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id))
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions,page);
        return myAuctions.size();
    }
    private int fillOutSoldItems(UUID id, int page){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id) && key.isSold())
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions,page);
        return myAuctions.size();
    }
    private int fillOutExpiredItems(UUID id, int page){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id) && key.isExpired() && !key.isSold())
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions,page);
        return myAuctions.size();
    }
    private int fillOutActiveAuctions(UUID id, int page){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id) && !key.isExpired() && key.isOnAuction())
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions,page);
        return myAuctions.size();
    }
    private void createButtonsForAuctionItems(LinkedHashMap<ItemNote, Long> myAuctions, int page){
        int startPage = page*21;
        int size = myAuctions.size();
        for(int i = startPage; i < startPage+21; ++i){
            if(size-1<i)break;
            Map.Entry<ItemNote,Long> entry = myAuctions.entrySet().stream().skip(size-i-1).findFirst().orElse(null);
            if(entry == null) continue;
            int j = i%21+10 + i%21/7 + i%21/7;
            this.addButton(j,auctionItem(entry.getKey()));
        }
    }

    private InventoryButton auctionItem(ItemNote note){
        ItemStack item = ItemManager.createItemFromNote(note, c.currentPlayer, true);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
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
    private InventoryButton sortButton(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        //go backwards
                        AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(currentPage,previousSort(currentSort),c), c.currentPlayer);
                    }else {
                        AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(currentPage,nextSort(currentSort),c), c.currentPlayer);
                    }
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
                            AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(auctionItemsAmount/21,currentSort,c), c.currentPlayer);
                        }
                    }else {
                        if(currentPage < auctionItemsAmount/21){
                            AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(currentPage+1,currentSort,c), c.currentPlayer);
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
                            AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(0,currentSort,c), c.currentPlayer);
                        }
                    }else {
                        if(currentPage > 0){
                            AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(currentPage-1,currentSort,c),c.currentPlayer);
                        }
                    }
                });
    }

}
