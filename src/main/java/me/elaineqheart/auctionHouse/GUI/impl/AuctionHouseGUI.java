package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.AnvilHandler;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.TaskManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Layout;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public class AuctionHouseGUI extends InventoryGUI implements Runnable {

    public final AhConfiguration c;
    private UUID invID = UUID.randomUUID();
    private int noteSize;
    private int screenSize;

    @Override
    public void run() {
        decorate(c.getPlayer());
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
        c.setView(AhConfiguration.View.AUCTION_HOUSE);
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }
    public AuctionHouseGUI(Player p) {
        super();
        this.c = AhConfiguration.getInstance(p).setPlayer(p.getUniqueId());
        c.setView(AhConfiguration.View.AUCTION_HOUSE);
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }
    public AuctionHouseGUI(AhConfiguration configuration) {
        super();
        this.c = configuration;
        c.setView(AhConfiguration.View.AUCTION_HOUSE);
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,Layout.ahLayout.size()*9, Messages.getFormatted("inventory-titles.auction-house"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(Layout.ahLayout);
        super.decorate(player);
    }

    @Override
    public void onClose(InventoryCloseEvent event) {
        TaskManager.cancelTask(invID);
    }

    private void update() {
        TaskManager.cancelTask(invID);
        Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), () -> decorate(c.getPlayer()));
        invID = UUID.randomUUID();
        TaskManager.addTaskID(invID,Bukkit.getScheduler().runTaskTimer(AuctionHouse.getPlugin(), this, 20, 20).getTaskId());
    }

    private void fillOutItems(Sort sort, List<Integer> itemSlots){
        switch (sort){
            case HIGHEST_PRICE -> createButtonsForAuctionItems(ItemNoteStorage.SortMode.PRICE_DESC, itemSlots);
            case LOWEST_PRICE -> createButtonsForAuctionItems(ItemNoteStorage.SortMode.PRICE_ASC, itemSlots);
            case ENDING_SOON -> createButtonsForAuctionItems(ItemNoteStorage.SortMode.DATE, itemSlots);
            case ALPHABETICAL -> createButtonsForAuctionItems(ItemNoteStorage.SortMode.NAME, itemSlots);
        }
    }

    private void createButtonsForAuctionItems(ItemNoteStorage.SortMode mode, List<Integer> itemSlots){
        List<ItemNote> auctions;
        auctions = AuctionHouseStorage.getSortedList(mode, c.getCurrentSearch(), c.getBinFilter());
        if(c.getWhitelist() != null) AuctionHouseStorage.applyWhitelist(auctions, c.getWhitelist());
        noteSize = auctions.size();
        screenSize = itemSlots.size();
        int start = c.getCurrentPage() * screenSize;
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
        ItemStack item = ItemManager.createItemFromNote(note, c.getPlayer(), false);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if (ItemManager.isShulkerBox(item) && event.isRightClick()) {
                        Sounds.openShulker(event);
                        AuctionHouse.getGuiManager().openGUI(new ShulkerViewGUI(note,c, AhConfiguration.View.AUCTION_HOUSE), c.getPlayer());
                        return;
                    }
                    Sounds.click(event);
                    if (c.isAdmin()) {
                        AuctionHouse.getGuiManager().openGUI(new AdminManageItemsGUI(note, c), c.getPlayer());
                        return;
                    }
                    if (Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()),c.getPlayer()) && !note.isBIDAuction()) {
                        AuctionHouse.getGuiManager().openGUI(new CancelAuctionGUI(note, c), c.getPlayer());
                        return;
                    }
                    AuctionHouse.getGuiManager().openGUI(new AuctionViewGUI(note, c, 0, AhConfiguration.View.AUCTION_HOUSE), c.getPlayer());
                });
    }


    private void fillOutPlaces(List<String> places){
        List<Integer> itemSlots = new ArrayList<>();
        for(int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                if (places.get(i).charAt(j) == '.') itemSlots.add(i*9+j/2);
            }
        }
        fillOutItems(c.getCurrentSort(), itemSlots);
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
                    case 'f' -> {
                        if (SettingManager.BINAuctions && SettingManager.BIDAuctions) this.addButton(slot, BINFilter());
                        else this.addButton(slot, fillerItem());
                    }
                    case 'm' -> {
                        if (!c.isAdmin()) this.addButton(slot, myAuctions()); else this.addButton(slot, commandBlockInfo());
                    }
                    case '.', ' ' -> {}
                    default -> {
                        String symbol = String.valueOf(places.get(i).charAt(j));
                        this.addButton(slot, customButton(Layout.getItem(symbol),
                            event -> {
                            List<Map<?, ?>> whitelist = ConfigManager.categories.get().getMapList(symbol);
                                if (!whitelist.isEmpty()) {
                                    c.setWhitelist(whitelist, symbol);
                                    Sounds.click(event);
                                    update();
                                }
                            }
                        ));
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
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.getPlayer());
                    Sounds.click(event);
                });
    }

//    private InventoryButton loading(){
//        return new InventoryButton()
//                .creator(player -> ItemManager.loading)
//                .consumer(event -> {});
//    }

    private InventoryButton nextPage(){
        int pages = (noteSize-1)/screenSize;
        ItemStack item = Layout.getItem("n");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.next-page.name"));
        meta.setLore(Messages.getLoreList("items.next-page.lore",
                "%page%", String.valueOf(c.getCurrentPage()+1),
                "%pages%", String.valueOf(pages+1)));

        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.getCurrentPage() == pages) return;
                    if(event.isRightClick()) c.setCurrentPage(pages); else c.setCurrentPage(c.getCurrentPage()+1);
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
                "%page%", String.valueOf(c.getCurrentPage()+1),
                "%pages%", String.valueOf((noteSize-1)/screenSize+1)));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(c.getCurrentPage() == 0) return;
                    if(event.isRightClick()) c.setCurrentPage(0); else c.setCurrentPage(c.getCurrentPage()-1);
                    Sounds.click(event);
                    update();
                });
    }
    private InventoryButton searchOption(){
        ItemStack item = c.getCurrentSearch().isEmpty() ? Layout.getItem("s") : Layout.getItem("active-search");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.search.name"));
        meta.setLore(Messages.getLoreList("items.search.lore", "%filter%", c.getCurrentSearch()));
        item.setItemMeta(meta);
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if(event.isRightClick()){
                        //clear filter
                        Sounds.breakWood(event);
                        c.setCurrentSearch("");
                        c.setCurrentPage(0);
                        update();
                    }else {
                        Sounds.click(event);
                        AnvilHandler handler = new AnvilHandler() {
                            public void execute(Player p, String typedText) {
                                c.setCurrentSearch(typedText);
                                AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
                            }
                            public void onClose(Player p) {
                                Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () ->
                                        AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), c.getPlayer()),1);
                            }
                        };
                        if(c.isAdmin()){
                            AuctionHouse.getAnvilManager().open(c.getPlayer(), "inventory-titles.anvil-admin-search", handler);
                        }else {
                            AuctionHouse.getAnvilManager().open(c.getPlayer(), "inventory-titles.anvil-search", handler);
                        }
                    }
                });
    }
    private InventoryButton sortButton(){
        return new InventoryButton()
                .creator(player -> ItemManager.getSort(c.getCurrentSort()))
                .consumer(event -> {
                    Sounds.click(event);
                    if(event.isRightClick()) c.setCurrentSort(previousSort(c.getCurrentSort()));
                    else c.setCurrentSort(nextSort(c.getCurrentSort()));;
                    c.setCurrentPage(0);
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
    private InventoryButton BINFilter(){
        return new InventoryButton()
                .creator(player -> ItemManager.createBINFilter(c.getBinFilter()))
                .consumer(event -> {
                    if(event.isRightClick()) c.setBinFilter(previousBINFilter(c.getBinFilter())); else c.setBinFilter(nextBINFilter(c.getBinFilter()));
                    Sounds.click(event);
                    update();
                });
    }
    private InventoryButton customButton(ItemStack item, Consumer<InventoryClickEvent> execute) {
        return new InventoryButton()
                .creator(player -> item)
                .consumer(execute);
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
    private AhConfiguration.BINFilter nextBINFilter(AhConfiguration.BINFilter input) {
        if(input.equals(AhConfiguration.BINFilter.ALL)) return AhConfiguration.BINFilter.BIN_ONLY;
        if(input.equals(AhConfiguration.BINFilter.BIN_ONLY)) return AhConfiguration.BINFilter.AUCTIONS_ONLY;
        return AhConfiguration.BINFilter.ALL;
    }
    private AhConfiguration.BINFilter previousBINFilter(AhConfiguration.BINFilter input) {
        if(input.equals(AhConfiguration.BINFilter.ALL)) return AhConfiguration.BINFilter.AUCTIONS_ONLY;
        if(input.equals(AhConfiguration.BINFilter.AUCTIONS_ONLY)) return AhConfiguration.BINFilter.BIN_ONLY;
        return AhConfiguration.BINFilter.ALL;
    }

}
