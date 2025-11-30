package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilSearchGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.items.AhConfiguration;
import me.elaineqheart.auctionHouse.data.items.ItemManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.persistentStorage.NoteStorage;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class AuctionHouseGUI extends InventoryGUI implements Runnable {

    public final AhConfiguration c;
    private UUID invID = UUID.randomUUID();
    private final String[] pattern = new String[]{
            "# # # # # # # # #",
            "# . . . . . . . #",
            "# . . . . . . . #",
            "# . . . . . . . #",
            "# # # # # # # # #",
            ". . # . . . # # .",
    };
    private int noteSize;

    @Override
    public void run() {
        decorateItems(c.currentPlayer);
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
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }
    public AuctionHouseGUI(Player p) {
        super();
        this.c = new AhConfiguration(0, Sort.HIGHEST_PRICE, "", p ,false);
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }
    public AuctionHouseGUI(AhConfiguration configuration) {
        super();
        this.c = configuration;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.auction-house"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(pattern);
        this.addButton(45,searchOption());
        this.addButton(48,previousPage(noteSize));
        this.addButton(50,nextPage(noteSize));
        this.addButton(46,sortButton(ItemManager.getSort(c.currentSort)));
        this.addButton(49,loading());
        if (!c.isAdmin) {this.addButton(53, myAuctions());} else {this.addButton(53, commandBlockInfo());}
        decorateItems(player);
    }
    private void decorateItems(Player player) {
        this.addButton(49,refresh());
        fillOutItems(c.currentPage, c.currentSort);
        this.addButton(48, previousPage(noteSize));
        this.addButton(50, nextPage(noteSize));
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

    private void update() {
        TaskManager.cancelTask(invID);
        Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), () -> decorate(c.currentPlayer));
        invID = UUID.randomUUID();
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    private void fillOutItems(int page, Sort sort){
        switch (sort){
            case HIGHEST_PRICE -> createButtonsForAuctionItems(page, NoteStorage.SortMode.PRICE_DESC);
            case LOWEST_PRICE -> createButtonsForAuctionItems(page, NoteStorage.SortMode.PRICE_ASC);
            case ENDING_SOON -> createButtonsForAuctionItems(page, NoteStorage.SortMode.DATE);
            case ALPHABETICAL -> createButtonsForAuctionItems(page, NoteStorage.SortMode.NAME);
        }
    }

    private void createButtonsForAuctionItems(int page, NoteStorage.SortMode mode){
        List<ItemNote> auctions = NoteStorage.getSortedList(mode, c.currentSearch);
        noteSize = auctions.size();
        int start = page*21;
        int stop = page*21+21;
        int end = Math.min(noteSize, stop);
        auctions = auctions.subList(start, end);
        int size = auctions.size();
        for(int i = 0; i < 21; ++i){
            int j = i%21+10 + i%21/7 + i%21/7;
            if(size-1<i) {
                this.addButton(j, new InventoryButton()
                        .creator(player -> null)
                        .consumer(event -> {}));
                continue;
            }
            ItemNote note = auctions.stream().skip(i).findFirst().orElse(null);
            if(note == null) continue;
            this.addButton(j,auctionItem(note));
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

    private InventoryButton loading(){
        return new InventoryButton()
                .creator(player -> ItemManager.loading)
                .consumer(event -> {});
    }

    private InventoryButton nextPage(int noteSize){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore", "%page%", String.valueOf(c.currentPage), "%pages%", String.valueOf((noteSize-1)/21)));

        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(event.isRightClick()){
                        if(c.currentPage != (noteSize-1)/21){
                            c.currentPage = (noteSize-1)/21;
                            Sounds.click(event);
                            update();
                        }
                    }else {
                        if(c.currentPage < (noteSize-1)/21){
                            c.currentPage++;
                            Sounds.click(event);
                            update();
                        }
                    }
                });
    }
    private InventoryButton previousPage(int noteSize){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.previous-page.name"));
        meta.setLore(Messages.getLoreList("items.previous-page.lore", "%page%", String.valueOf(c.currentPage), "%pages%", String.valueOf((noteSize-1)/21)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(event.isRightClick()){
                        if(c.currentPage != 0){
                            c.currentPage = 0;
                            Sounds.click(event);
                            update();
                        }
                    }else {
                        if(c.currentPage > 0){
                            c.currentPage--;
                            Sounds.click(event);
                            update();
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
                        c.currentPage = 0;
                        update();
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
                    if(event.isRightClick()) c.currentSort = previousSort(c.currentSort);
                    else c.currentSort = nextSort(c.currentSort);
                    c.currentPage = 0;
                    update();
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
