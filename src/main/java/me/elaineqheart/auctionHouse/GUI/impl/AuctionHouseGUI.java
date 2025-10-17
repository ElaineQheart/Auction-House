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
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AuctionHouseGUI extends InventoryGUI implements Runnable {

    public final AhConfiguration c;
    private final UUID invID = UUID.randomUUID();

    @Override
    public void run() {
        decorate(c.currentPlayer);
    }

    public enum Sort{
        HIGHEST_PRICE,
        LOWEST_PRICE,
        ENDING_SOON,
        ALPHABETICAL
    }

    public AuctionHouseGUI(int page, Sort sort, String search, Player p, boolean isAdmin) {
        super();
        this.c = new AhConfiguration(page, sort, search, p ,isAdmin);
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }
    public AuctionHouseGUI(Player p) {
        super();
        this.c = new AhConfiguration(0, Sort.HIGHEST_PRICE, "", p ,false);
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 0, 20).getTaskId());
    }
    public AuctionHouseGUI(AhConfiguration configuration) {
        super();
        this.c = configuration;
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
        fillOutItems(c.currentPage,c.currentSort);
        this.addButton(45,searchOption());
        this.addButton(46,sortButton(ItemManager.getSort(c.currentSort)));
        this.addButton(48,previousPage());
        this.addButton(49,refresh());
        this.addButton(50,nextPage());
        if (!c.isAdmin) {this.addButton(53, myAuctions());} else {this.addButton(53, commandBlockInfo());}
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
        Map<ItemNote, Double> sortedHighestPrice = ItemNoteStorageUtil.sortedHighestPrice();
        if(!c.currentSearch.isEmpty()){
            sortedHighestPrice = ItemNoteStorageUtil.hiPrSearch(c.currentSearch);
        }
        int size = sortedHighestPrice.size();
        for(int i = startPage; i < startPage+21; i++){
            if(size-1<i)break;
            Map.Entry<ItemNote,Double> entry = sortedHighestPrice.entrySet().stream().skip(size-i-1).findFirst().orElse(null);
            if(entry == null) continue;
            int j = i%21+10 + i%21/7 + i%21/7;
            this.addButton(j,auctionItem(entry.getKey()));
        }
    }
    private void fillOutAuctionItemsLowestPrice(int page){
        int startPage = page*21;
        Map<ItemNote, Double> sortedHighestPrice = ItemNoteStorageUtil.sortedHighestPrice();
        if(!c.currentSearch.isEmpty()){
            sortedHighestPrice = ItemNoteStorageUtil.hiPrSearch(c.currentSearch);
        }
        int size = sortedHighestPrice.size();
        for(int i = startPage; i < startPage+21; ++i){
            if(size-1<i)break;
            Map.Entry<ItemNote,Double> entry = sortedHighestPrice.entrySet().stream().skip(i).findFirst().orElse(null);
            if(entry == null) continue;
            int j = i%21+10 + i%21/7 + i%21/7;
            this.addButton(j,auctionItem(entry.getKey()));
        }
    }
    private void fillOutAuctionItemsEndingSoon(int page){
        int startPage = page*21;
        Map<ItemNote, Long> sortedDateCreated = ItemNoteStorageUtil.sortedDateCreated();
        if(!c.currentSearch.isEmpty()){
            sortedDateCreated = ItemNoteStorageUtil.dateSearch(c.currentSearch);
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
        if(!c.currentSearch.isEmpty()){
            sortedAlphabetical = ItemNoteStorageUtil.alphaSearch(c.currentSearch);
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
        ItemStack item = ItemManager.createItemFromNote(note, c.currentPlayer, false);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(ItemManager.isShulkerBox(item) && event.isRightClick()) {
                        Sounds.openShulker(event);
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c), c.currentPlayer);
                        return;
                    }
                    Sounds.click(event);
                    if(!Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()),c.currentPlayer) || c.isAdmin) {
                        if(c.isAdmin) {
                            AuctionHouse.getGuiManager().openGUI(new AdminManageItemsGUI(note, c), c.currentPlayer);
                        }else {
                            AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c), c.currentPlayer);
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
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                    Sounds.click(event);
                });
    }
    private InventoryButton nextPage(){
        List<ItemNote> notes = ItemNoteStorageUtil.findAllNotes();
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore", "%page%", String.valueOf(c.currentPage), "%pages%", String.valueOf(notes.size()/21)));

        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        if(c.currentPage != notes.size()/21){
                            c.currentPage = notes.size()/21;
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                        }
                    }else {
                        if(c.currentPage < notes.size()/21){
                            c.currentPage++;
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
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
        meta.setLore(Messages.getLoreList("items.previous-page.lore", "%page%", String.valueOf(c.currentPage), "%pages%", String.valueOf(notes.size()/21)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()){
                        if(c.currentPage != 0){
                            c.currentPage = 0;
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                        }
                    }else {
                        if(c.currentPage > 0){
                            c.currentPage--;
                            AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                        }
                    }
                });
    }
    private InventoryButton searchOption(){
        ItemStack item = new ItemStack(Material.OAK_SIGN);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.search.name"));
        meta.setLore(Messages.getLoreList("items.search.lore", "%filter%", c.currentSearch));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(event.isRightClick()){
                        //clear filter
                        Sounds.breakWood(event);
                        c.currentSearch = "";
                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                    }else {
                        Sounds.click(event);
                        if(c.isAdmin){
                            new AnvilSearchGUI((Player) event.getWhoClicked(), AnvilSearchGUI.SearchType.ADMIN_AH, null, c);
                        }else {
                            new AnvilSearchGUI((Player) event.getWhoClicked(), AnvilSearchGUI.SearchType.AH, null, c);
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
                        c.currentSort = previousSort(c.currentSort);
                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                    }else {
                        c.currentSort = nextSort(c.currentSort);
                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.currentPlayer);
                    }
                });
    }
    private InventoryButton myAuctions(){
        return new InventoryButton()
                .creator(player -> ItemManager.myAuction)
                .consumer(event -> {
                    Sounds.openEnderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new MyAuctionsGUI(c), (Player) event.getWhoClicked());
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
