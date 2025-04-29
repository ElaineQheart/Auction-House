package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskInventoryManager;
import me.elaineqheart.auctionHouse.ah.ItemManager;
import me.elaineqheart.auctionHouse.ah.ItemNote;
import me.elaineqheart.auctionHouse.ah.ItemNoteStorageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class MyAuctionsGUI extends InventoryGUI implements Runnable{

    private final MySort currentSort;
    private final Player currentPlayer;
    private final UUID invID = UUID.randomUUID();

    @Override
    public void run() {
        decorate(currentPlayer);
    }

    public enum MySort{
        ALL_AUCTIONS,
        SOLD_ITEMS,
        EXPIRED_ITEMS,
        ACTIVE_AUCTIONS
    }
    public MyAuctionsGUI(MySort sort, Player p) {
        super();
        currentSort = sort;
        currentPlayer = p;
        TaskInventoryManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }
    public MyAuctionsGUI(Player p) {
        super();
        currentSort = MySort.ALL_AUCTIONS;
        currentPlayer = p;
        TaskInventoryManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9,"My Auctions");
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
        fillOutPlaces(new String[]{
                ". . . . . . . . .",
                ". . . . . . . . .",
                ". . . . # # # # .",
                ". # # # # # # # .",
        },barrier());
        fillOutItems(currentSort,player.getUniqueId());
        this.addButton(45,back());
        this.addButton(46,sortButton(ItemManager.getMySort(currentSort)));
        this.addButton(49,refresh());
        this.addButton(53,info());
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskInventoryManager.cancelTask(invID);
    }

    private void fillOutItems(MySort sort,UUID playerID){
        switch (sort){
            case ALL_AUCTIONS -> fillOutAllAuctions(playerID);
            case SOLD_ITEMS -> fillOutSoldItems(playerID);
            case EXPIRED_ITEMS -> fillOutExpiredItems(playerID);
            case ACTIVE_AUCTIONS -> fillOutActiveAuctions(playerID);
        }
    }

    private void fillOutAllAuctions(UUID id){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            //comparing the UUIDs directly doesn't work, because it's not persistent with player rejoins
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id))
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions);
    }
    private void fillOutSoldItems(UUID id){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id) && key.isSold())
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions);
    }
    private void fillOutExpiredItems(UUID id){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id) && key.isExpired() && !key.isSold())
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions);
    }
    private void fillOutActiveAuctions(UUID id){
        LinkedHashMap<ItemNote, Long> myAuctions = new LinkedHashMap<>();
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.mySortedDateCreated();
        for(ItemNote key : sortedDateCreated.keySet()) {
            if (Bukkit.getPlayer(key.getPlayerUUID()) == Bukkit.getPlayer(id) && !key.isExpired() && !key.isSold())
                myAuctions.put(key, sortedDateCreated.get(key));
        }
        createButtonsForAuctionItems(myAuctions);
    }
    private void createButtonsForAuctionItems(LinkedHashMap<ItemNote, Long> myAuctions){
        int size = myAuctions.size();
        if(myAuctions.size()>10)size=10;
        for(int i = 0; i < 21; ++i){
            if(size-1<i)break;
            Map.Entry<ItemNote,Long> entry = myAuctions.entrySet().stream().skip(size-i-1).findFirst().orElse(null);
            if(entry == null) continue;
            int j = i%21+10 + i%21/7 + i%21/7;
            this.addButton(j,auctionItem(entry.getKey()));
        }
    }

    private InventoryButton auctionItem(ItemNote note){
        ItemStack item = ItemManager.createItemFromNote(note, currentPlayer);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(note.isSold()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note,currentSort), currentPlayer);
                    } else if (note.isExpired()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectExpiredItemGUI(note,currentSort), currentPlayer);
                    } else {
                        AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note,currentSort), currentPlayer);
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
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(currentSort,currentPlayer), currentPlayer);
                });
    }
    private InventoryButton sortButton(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        //go backwards
                        AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(previousSort(currentSort),currentPlayer), currentPlayer);
                    }else {
                        AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(nextSort(currentSort),currentPlayer), currentPlayer);
                    }
                });
    }
    private InventoryButton back(){
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Sounds.closeEnderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPlayer), currentPlayer);
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

}
