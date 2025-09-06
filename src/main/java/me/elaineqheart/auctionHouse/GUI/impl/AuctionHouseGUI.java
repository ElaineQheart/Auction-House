package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.GUI.other.AnvilSearchGUI;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.items.ItemNote;
import me.elaineqheart.auctionHouse.data.items.ItemNoteStorageUtil;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AuctionHouseGUI extends InventoryGUI implements Runnable {

    private final int currentPage;
    private final Sort currentSort;
    private final String currentSearch;
    private final Player currentPlayer;
    private final UUID invID = UUID.randomUUID();
    private final boolean isAdmin;

    @Override
    public void run() {
        decorate(currentPlayer);
    }

    public enum Sort{
        HIGHEST_PRICE,
        LOWEST_PRICE,
        ENDING_SOON,
        ALPHABETICAL
    }

    public AuctionHouseGUI(int page, Sort sort, String search, Player p, boolean isAdmin) {
        super();
        this.currentPage = page;
        this.currentSort = sort;
        this.currentSearch = search;
        this.currentPlayer = p;
        this.isAdmin = isAdmin;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }
    public AuctionHouseGUI(Player p) {
        super();
        this.currentPage = 0;
        this.currentSort = Sort.HIGHEST_PRICE;
        this.currentSearch = "";
        this.currentPlayer = p;
        this.isAdmin = false;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.auction-house"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# . . . . . . . #",
                "# # # # # # # # #",
                ". . # . . . # # .",
        });
        fillOutItems(currentPage,currentSort);
        this.addButton(45,searchOption());
        this.addButton(46,sortButton(ItemManager.getSort(currentSort)));
        this.addButton(48,previousPage());
        this.addButton(49,refresh());
        this.addButton(50,nextPage());
        if (!isAdmin) {this.addButton(53, myAuctions());} else {this.addButton(53, commandBlockInfo());}
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

    private void fillOutItems(int page, Sort sort){
        switch (sort){
            case HIGHEST_PRICE -> fillOutAuctionItemsHighestPrice(page);
            case LOWEST_PRICE -> fillOutAuctionItemsLowestPrice(page);
            case ENDING_SOON -> fillOutAuctionItemsEndingSoon(page);
            case ALPHABETICAL -> fillOutAuctionItemsAlphabetical(page);
        }
    }

    private void fillOutAuctionItemsHighestPrice(int page){
        int startPage = page*21;
        Map<ItemNote, Integer> sortedHighestPrice = ItemNoteStorageUtil.sortedHighestPrice();
        if(!currentSearch.isEmpty()){
            sortedHighestPrice = ItemNoteStorageUtil.hiPrSearch(currentSearch);
        }
        int size = sortedHighestPrice.size();
        for(int i = startPage; i < startPage+21; i++){
            if(size-1<i)break;
            Map.Entry<ItemNote,Integer> entry = sortedHighestPrice.entrySet().stream().skip(size-i-1).findFirst().orElse(null);
            if(entry == null) continue;
            int j = i%21+10 + i%21/7 + i%21/7;
            this.addButton(j,auctionItem(entry.getKey()));
        }
    }
    private void fillOutAuctionItemsLowestPrice(int page){
        int startPage = page*21;
        Map<ItemNote, Integer> sortedHighestPrice = ItemNoteStorageUtil.sortedHighestPrice();
        if(!currentSearch.isEmpty()){
            sortedHighestPrice = ItemNoteStorageUtil.hiPrSearch(currentSearch);
        }
        int size = sortedHighestPrice.size();
        for(int i = startPage; i < startPage+21; ++i){
            if(size-1<i)break;
            Map.Entry<ItemNote,Integer> entry = sortedHighestPrice.entrySet().stream().skip(i).findFirst().orElse(null);
            if(entry == null) continue;
            int j = i%21+10 + i%21/7 + i%21/7;
            this.addButton(j,auctionItem(entry.getKey()));
        }
    }
    private void fillOutAuctionItemsEndingSoon(int page){
        int startPage = page*21;
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.sortedDateCreated();
        if(!currentSearch.isEmpty()){
            sortedDateCreated = ItemNoteStorageUtil.dateSearch(currentSearch);
        }
        int size = sortedDateCreated.size();
        for(int i = startPage; i < startPage+21; ++i){
            if(size-1<i)break;
            Map.Entry<ItemNote,Long> entry = sortedDateCreated.entrySet().stream().skip(i).findFirst().orElse(null);
            if(entry == null) continue;
            int j = i%21+10 + i%21/7 + i%21/7;
            this.addButton(j,auctionItem(entry.getKey()));
        }
    }
    private void fillOutAuctionItemsAlphabetical(int page){
        int startPage = page*21;
        Map<ItemNote, String> sortedAlphabetical = ItemNoteStorageUtil.sortedAlphabetical();
        if(!currentSearch.isEmpty()){
            sortedAlphabetical = ItemNoteStorageUtil.alphaSearch(currentSearch);
        }
        int size = sortedAlphabetical.size();
        for(int i = startPage; i < startPage+21; ++i){
            if(size-1<i)break;
            Map.Entry<ItemNote,String> entry = sortedAlphabetical.entrySet().stream().skip(i).findFirst().orElse(null);
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
                    if(ItemManager.isShulkerBox(item) && event.isRightClick()) {
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,currentPage,currentSort, currentSearch,isAdmin), currentPlayer);
                        return;
                    }
                    if(!Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()),currentPlayer)) {
                        if(isAdmin) {
                            AuctionHouse.getGuiManager().openGUI(new AdminManageItemGUI(note, currentPlayer), currentPlayer);
                        }else {
                            AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, currentPlayer), currentPlayer);
                        }
                    }
                });
    }


    private void fillOutPlaces(String[] places){
        for(int i = 0; i < places.length; i++){
            for(int j = 0; j < places[i].length(); j+=2){
                if(places[i].charAt(j)=='#') {
                    this.addButton(i*9+j/2, this.fillerItem());
                }
            }
        }
    }
    private InventoryButton fillerItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }
    private InventoryButton commandBlockInfo(){
        return new InventoryButton()
                .creator(player -> ItemManager.commandBlockInfo)
                .consumer(event -> {});
    }

    private InventoryButton refresh(){
        return new InventoryButton()
                .creator(player -> ItemManager.refresh)
                .consumer(event -> {
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPage,currentSort,currentSearch,currentPlayer,isAdmin), currentPlayer);
                    Sounds.click(event);
                });
    }
    private InventoryButton nextPage(){
        List<ItemNote> notes = ItemNoteStorageUtil.findAllNotes();
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore", "%page%", String.valueOf(currentPage), "%pages%", String.valueOf(notes.size()/21)));

        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        if(currentPage != notes.size()/21){
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(notes.size()/21,currentSort,currentSearch,currentPlayer,isAdmin), currentPlayer);
                        }
                    }else {
                        if(currentPage < notes.size()/21){
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPage+1,currentSort,currentSearch,currentPlayer,isAdmin), currentPlayer);
                        }
                    }
                });
    }
    private InventoryButton previousPage(){
        List<ItemNote> notes = ItemNoteStorageUtil.findAllNotes();
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.previous-page.name"));
        meta.setLore(Messages.getLoreList("items.previous-page.lore", "%page%", String.valueOf(currentPage), "%pages%", String.valueOf(notes.size()/21)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        if(currentPage != 0){
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(0,currentSort,currentSearch,currentPlayer,isAdmin), currentPlayer);
                        }
                    }else {
                        if(currentPage > 0){
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPage-1,currentSort,currentSearch,currentPlayer,isAdmin),currentPlayer);
                        }
                    }
                });
    }
    private InventoryButton searchOption(){
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.search.name"));
        meta.setLore(Messages.getLoreList("items.search.lore", "%filter%", currentSearch));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(event.isRightClick()){
                        //clear filter
                        Sounds.breakWood(event);
                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPage,currentSort,"",currentPlayer,isAdmin), currentPlayer);
                    }else {
                        Sounds.click(event);
                        if(isAdmin){
                            new AnvilSearchGUI((Player) event.getWhoClicked(), AnvilSearchGUI.SearchType.ADMIN_AH, null);
                        }else {
                            new AnvilSearchGUI((Player) event.getWhoClicked(), AnvilSearchGUI.SearchType.AH, null);
                        }
                    }
                });
    }
    private InventoryButton sortButton(ItemStack item){
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPage,previousSort(currentSort),currentSearch,currentPlayer,isAdmin), currentPlayer);
                    }else {
                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(currentPage,nextSort(currentSort),currentSearch,currentPlayer,isAdmin), currentPlayer);
                    }
                });
    }
    private InventoryButton myAuctions(){
        return new InventoryButton()
                .creator(player -> ItemManager.myAuction)
                .consumer(event -> {
                    Sounds.enderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(currentPlayer), (Player) event.getWhoClicked());
                });
    }

    private Sort nextSort(Sort input){
        if(input.equals(Sort.HIGHEST_PRICE)) return Sort.LOWEST_PRICE;
        if(input.equals(Sort.LOWEST_PRICE)) return Sort.ENDING_SOON;
        if(input.equals(Sort.ENDING_SOON)) return Sort.ALPHABETICAL;
        return Sort.HIGHEST_PRICE;
    }
    private Sort previousSort(Sort input){
        if(input.equals(Sort.ALPHABETICAL)) return Sort.ENDING_SOON;
        if(input.equals(Sort.ENDING_SOON)) return Sort.LOWEST_PRICE;
        if(input.equals(Sort.LOWEST_PRICE)) return Sort.HIGHEST_PRICE;
        return Sort.ALPHABETICAL;
    }

}
