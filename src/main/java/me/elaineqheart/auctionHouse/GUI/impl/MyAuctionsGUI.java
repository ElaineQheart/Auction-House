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
        decorate(c.getPlayer());
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
        c.setView(AhConfiguration.View.MY_AUCTIONS);
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,Layout.myAhLayout.size()*9, Messages.getFormatted("inventory-titles.my-auctions"));
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
        decorate(c.getPlayer());
        TaskManager.cancelTask(invID);
        invID = UUID.randomUUID();
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    private void fillOutItems(Player p, List<Integer> itemSlots){
        List<ItemNote> myAuctions = AuctionHouseStorage.getMySortedDateCreated(p);
        List<ItemNote> returnList;
        switch (c.getMyCurrentSort()){
            case SOLD_ITEMS -> returnList = myAuctions.stream()
                        .filter(note -> note.isSold() || !note.isBINAuction() && note.hasBidHistory() && note.isExpired())
                        .collect(Collectors.toList());
            case EXPIRED_ITEMS -> returnList = myAuctions.stream()
                        .filter(note -> note.isExpired() && (note.isBINAuction() && !note.isSold() || !note.isBINAuction() && !note.hasBidHistory()))
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
        int start = c.getMyCurrentPage() * screenSize;
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
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c, AhConfiguration.View.AUCTION_HOUSE), c.getPlayer());
                        return;
                    }
                    Sounds.click(event);
                    if(note.isSold() || note.isExpired() && note.hasBidHistory()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectSoldItemGUI(note, c), c.getPlayer());
                    } else if (note.isExpired()) {
                        AuctionHouse.getGuiManager().openGUI(new CollectExpiredItemGUI(note, c), c.getPlayer());
                    } else {
                        if(note.isBINAuction()) AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note, c), c.getPlayer());
                        else AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, AhConfiguration.View.MY_AUCTIONS), c.getPlayer());
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
        fillOutItems(player, itemSlots);
        fillOutBarriers(Permissions.getAuctionSlots(player), itemSlots);
        for(int i = 0; i < places.size(); i++){
            for(int j = 0; j < places.get(i).length(); j+=2){
                int slot = i*9+j/2;
                switch (places.get(i).charAt(j)) {
                    case '#' -> this.addButton(slot, fillerItem());
                    case 'b' -> this.addButton(slot, back());
                    case 'o' -> this.addButton(slot, sortButton(ItemManager.getMySort(c.getMyCurrentSort())));
                    case 'p' -> {
                        if(Permissions.getAuctionSlots(player) > screenSize) this.addButton(slot, previousPage()); else this.addButton(slot, fillerItem());
                    }
                    case 'n' -> {
                        if(Permissions.getAuctionSlots(player) > screenSize) this.addButton(slot, nextPage()); else this.addButton(slot, fillerItem());
                    }
                    case 'r' -> this.addButton(slot, refresh());
                    case 'd' -> {
                        if(SettingManager.BIDAuctions) this.addButton(slot, myBids());
                        else this.addButton(slot, fillerItem());
                    }
                    case 'i' -> this.addButton(slot, info());
                }
            }
        }
    }
    private void fillOutBarriers(int auctions, List<Integer> itemSlots) {
        int startPage = c.getMyCurrentPage()*screenSize + screenSize;
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
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), c.getPlayer());
                });
    }
//    private InventoryButton loading(){
//        return new InventoryButton()
//                .creator(player -> ItemManager.loading)
//                .consumer(event -> {});
//    }
    private InventoryButton sortButton(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()) c.setMyCurrentSort(previousSort(c.getMyCurrentSort()));
                    else c.setMyCurrentSort(nextSort(c.getMyCurrentSort()));
                    c.setMyCurrentPage(0);
                    update();
                });
    }
    private InventoryButton back(){
        return new InventoryButton()
                .creator(player -> ItemManager.backToMainMenu)
                .consumer(event -> {
                    Sounds.closeEnderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.getPlayer());
                });
    }
    private InventoryButton info(){
        return new InventoryButton()
                .creator(player -> ItemManager.info)
                .consumer(event -> {});
    }
    private InventoryButton myBids(){
        return new InventoryButton()
                .creator(player -> ItemManager.myBids)
                .consumer(event -> {
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new MyBidsGUI(c, 0), c.getPlayer());
                });
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
        ItemStack item = Layout.getItem("n");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore",
                "%page%", String.valueOf(c.getMyCurrentPage()+1),
                "%pages%", String.valueOf(pages+1)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.getMyCurrentPage() == pages) return;
                    if(event.isRightClick()) c.setMyCurrentPage(pages); else c.setMyCurrentPage(c.getMyCurrentPage()+1);
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
                "%page%", String.valueOf(c.getMyCurrentPage()+1),
                "%pages%", String.valueOf((noteSize-1)/screenSize+1)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.getMyCurrentPage() == 0) return;
                    if(event.isRightClick()) c.setMyCurrentPage(0); else c.setMyCurrentPage(c.getMyCurrentPage()-1);
                    Sounds.click(event);
                    update();
                });
    }

}
