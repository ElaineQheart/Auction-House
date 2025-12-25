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
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
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
    private int noteSize;
    private int screenSize;

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
        c.view = AhConfiguration.View.AUCTION_HOUSE;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }
    public AuctionHouseGUI(Player p) {
        super();
        this.c = new AhConfiguration(0, Sort.HIGHEST_PRICE, "", p ,false);
        c.view = AhConfiguration.View.AUCTION_HOUSE;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }
    public AuctionHouseGUI(AhConfiguration configuration) {
        super();
        this.c = configuration;
        c.view = AhConfiguration.View.AUCTION_HOUSE;
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,6*9, Messages.getFormatted("inventory-titles.auction-house"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(SettingManager.ahLayout);
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

    private void fillOutItems(Sort sort, List<Integer> itemSlots){
        switch (sort){
            case HIGHEST_PRICE -> createButtonsForAuctionItems(NoteStorage.SortMode.PRICE_DESC, itemSlots);
            case LOWEST_PRICE -> createButtonsForAuctionItems(NoteStorage.SortMode.PRICE_ASC, itemSlots);
            case ENDING_SOON -> createButtonsForAuctionItems(NoteStorage.SortMode.DATE, itemSlots);
            case ALPHABETICAL -> createButtonsForAuctionItems(NoteStorage.SortMode.NAME, itemSlots);
        }
    }

    private void createButtonsForAuctionItems(NoteStorage.SortMode mode, List<Integer> itemSlots){
        List<ItemNote> auctions = NoteStorage.getSortedList(mode, c.currentSearch);
        noteSize = auctions.size();
        screenSize = itemSlots.size();
        int start = c.currentPage * screenSize;
        int stop = start + screenSize;
        int end = Math.min(noteSize, stop);
        auctions = auctions.subList(start, end);
        int size = auctions.size();
        for(int i = 0; i < screenSize; ++i){
            int j = itemSlots.get(i);
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


    private void fillOutPlaces(List<String> places){
        List<Integer> itemSlots = new ArrayList<>();
        for(int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                if (places.get(i).charAt(j) == '.') itemSlots.add(i*9+j/2);
            }
        }
        fillOutItems(c.currentSort, itemSlots);
        for(int i = 0; i < places.size(); i++){
            for(int j = 0; j < places.get(i).length(); j+=2){
                int slot = i*9+j/2;
                switch (places.get(i).charAt(j)) {
                    case '#' -> this.addButton(slot, fillerItem());
                    case 's' -> this.addButton(slot, searchOption());
                    case 'o' -> this.addButton(slot, sortButton());
                    case 'p' -> this.addButton(slot, previousPage());
                    case 'n' -> this.addButton(slot, nextPage());
                    case 'r' -> this.addButton(slot, refresh());
                    case 'm' -> {
                        if (!c.isAdmin) this.addButton(slot, myAuctions()); else this.addButton(slot, commandBlockInfo());
                    }
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

    private InventoryButton nextPage(){
        int pages = (noteSize-1)/screenSize;
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore",
                "%page%", String.valueOf(c.currentPage+1),
                "%pages%", String.valueOf(pages+1)));

        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.currentPage == pages) return;
                    if(event.isRightClick()) c.currentPage = pages; else c.currentPage++;
                    Sounds.click(event);
                    update();
                });
    }
    private InventoryButton previousPage(){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.previous-page.name"));
        meta.setLore(Messages.getLoreList("items.previous-page.lore",
                "%page%", String.valueOf(c.currentPage+1),
                "%pages%", String.valueOf((noteSize-1)/screenSize+1)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.currentPage == 0) return;
                    if(event.isRightClick()) c.currentPage = 0; else c.currentPage--;
                    Sounds.click(event);
                    update();
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
    private InventoryButton sortButton(){
        return new InventoryButton()
                .creator(player -> ItemManager.getSort(c.currentSort))
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
