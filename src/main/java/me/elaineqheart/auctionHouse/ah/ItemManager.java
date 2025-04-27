package me.elaineqheart.auctionHouse.ah;

import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemManager {

    public static ItemStack fillerItem;
    public static ItemStack lockedSlot;
    public static ItemStack refresh;
    public static ItemStack backToMainMenu;
    public static ItemStack backToMyAuctions;
    public static ItemStack info;
    public static ItemStack myAuction;
    public static ItemStack sortHighestPrice;
    public static ItemStack sortLowestPrice;
    public static ItemStack sortEndingSoon;
    public static ItemStack sortAlphabetical;
    public static ItemStack mySortAllAuctions;
    public static ItemStack mySortSoldItems;
    public static ItemStack mySortExpiredItems;
    public static ItemStack mySortActiveAuctions;
    public static ItemStack emptyPaper;
    public static ItemStack cancel;
    public static ItemStack collectExpiredItem;

    public static void init(){
        createFillerItem();
        createLockedSlot();
        createRefresh();
        createBackToMainMenu();
        createBackToMyAuctions();
        createInfo();
        createMyAuction();
        createSortHighestPrice();
        createSortLowestPrice();
        createSortEndingSoon();
        createSortAlphabetical();
        createMySortAllAuctions();
        createMySortSoldItems();
        createMySortExpiredItems();
        createMySortActiveAuctions();
        createEmptyPaper();
        createCancel();
        createCollectExpiredItem();
    }

    private static void createFillerItem(){
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        fillerItem = item;
    }
    private static void createLockedSlot(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Locked Slot");
        item.setItemMeta(meta);
        lockedSlot = item;
    }
    private static void createRefresh(){
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Refresh");
        meta.setLore(List.of(ChatColor.GRAY + "Click to refresh the menu"));
        item.setItemMeta(meta);
        refresh = item;
    }
    private static void createBackToMainMenu(){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Main Menu");
        meta.setLore(List.of(ChatColor.GRAY + "Click to go back!"));
        item.setItemMeta(meta);
        backToMainMenu = item;
    }
    private static void createBackToMyAuctions(){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "My Auctions");
        meta.setLore(List.of(ChatColor.GRAY + "Click to go back!"));
        item.setItemMeta(meta);
        backToMyAuctions = item;
    }
    private static void createInfo(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Info");
        meta.setLore(List.of(ChatColor.GRAY + "To set up a new auction use",
                ChatColor.GRAY + "/ah sell 1000",
                ChatColor.GRAY + "while holding the item to sell in your hand.",
                ChatColor.GRAY + "the number will be the price of the item"
        ));
        item.setItemMeta(meta);
        info = item;
    }
    private static void createMyAuction(){
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "My Auctions");
        meta.setLore(List.of(ChatColor.GRAY + "Click to view your auctions!"));
        item.setItemMeta(meta);
        myAuction = item;
    }
    private static void createSortHighestPrice(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.AQUA + "► Highest Price",
                ChatColor.GRAY + " Lowest Price",
                ChatColor.GRAY + " Ending soon",
                ChatColor.GRAY + " Alphabetical", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        sortHighestPrice = item;
    }
    private static void createSortLowestPrice(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.GRAY + " Highest Price",
                ChatColor.AQUA + "► Lowest Price",
                ChatColor.GRAY + " Ending soon",
                ChatColor.GRAY + " Alphabetical", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        sortLowestPrice = item;
    }
    private static void createSortEndingSoon(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.GRAY + " Highest Price",
                ChatColor.GRAY + " Lowest Price",
                ChatColor.AQUA + "► Ending soon",
                ChatColor.GRAY + " Alphabetical", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        sortEndingSoon = item;
    }
    private static void createSortAlphabetical(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.GRAY + " Highest Price",
                ChatColor.GRAY + " Lowest Price",
                ChatColor.GRAY + " Ending soon",
                ChatColor.AQUA + "► Alphabetical", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        sortAlphabetical = item;
    }
    public static ItemStack getSort(AuctionHouseGUI.Sort sort){
        if(sort.equals(AuctionHouseGUI.Sort.LOWEST_PRICE)) return sortLowestPrice;
        if(sort.equals(AuctionHouseGUI.Sort.ENDING_SOON)) return sortEndingSoon;
        if(sort.equals(AuctionHouseGUI.Sort.ALPHABETICAL)) return sortAlphabetical;
        return sortHighestPrice;
    }
    public static ItemStack getMySort(MyAuctionsGUI.MySort sort){
        if(sort.equals(MyAuctionsGUI.MySort.ALL_AUCTIONS)) return mySortAllAuctions;
        if(sort.equals(MyAuctionsGUI.MySort.SOLD_ITEMS)) return mySortSoldItems;
        if(sort.equals(MyAuctionsGUI.MySort.EXPIRED_ITEMS)) return mySortExpiredItems;
        return mySortActiveAuctions;
    }
    private static void createMySortAllAuctions(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.AQUA + "► All Auctions",
                ChatColor.GRAY + " Sold Items",
                ChatColor.GRAY + " Expired Items",
                ChatColor.GRAY + " Active Auctions", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        mySortAllAuctions = item;
    }
    private static void createMySortSoldItems(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.GRAY + " All Auctions",
                ChatColor.AQUA + "► Sold Items",
                ChatColor.GRAY + " Expired Items",
                ChatColor.GRAY + " Active Auctions", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        mySortSoldItems = item;
    }
    private static void createMySortExpiredItems(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.GRAY + " All Auctions",
                ChatColor.GRAY + " Sold Items",
                ChatColor.AQUA + "► Expired Items",
                ChatColor.GRAY + " Active Auctions", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        mySortExpiredItems = item;
    }
    private static void createMySortActiveAuctions(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Sort");
        meta.setLore(List.of("", ChatColor.GRAY + " All Auctions",
                ChatColor.GRAY + " Sold Items",
                ChatColor.GRAY + " Expired Items",
                ChatColor.AQUA + "► Active Auctions", "",
                ChatColor.YELLOW + "Right-Click to go backwards!",
                ChatColor.GREEN + "Click to switch sort!"
        ));
        item.setItemMeta(meta);
        mySortActiveAuctions = item;
    }
    private static void createEmptyPaper() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GRAY + "");
        item.setItemMeta(meta);
        emptyPaper = item;
    }
    private static void createCancel() {
        ItemStack item = new ItemStack(Material.RED_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Cancel");
        item.setItemMeta(meta);
        cancel = item;
    }
    private static void createCollectExpiredItem() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.RED + "Collect Expired Item");
        meta.setLore(List.of("", ChatColor.YELLOW + "Click to collect!"));
        item.setItemMeta(meta);
        collectExpiredItem = item;
    }

    public static ItemStack createItemFromNote(ItemNote note, Player p){
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "------------------");
        lore.add(ChatColor.GRAY + "Seller: " + note.getPlayerName());
        lore.add(ChatColor.GRAY + "Price: " + ChatColor.GOLD + note.getPrice());
        lore.add("");
        if(Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()),p)) {
            lore.add(ChatColor.GREEN + "This is your own item!");
            lore.add("");
        }
        if(note.isExpired()){
            lore.add(ChatColor.RED + "Expired!");
        }else if(note.isSold()){
            lore.add(ChatColor.GOLD + "Sold!");
            lore.add(ChatColor.GRAY + "Buyer: " + note.getBuyerName());
        }else{
            lore.add("Ends in: "+ note.getTimeLeft());
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBuyingItemDisplay(ItemNote note) {
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + "------------------");
        lore.add(ChatColor.YELLOW + "" + ChatColor.UNDERLINE + "BUYING ITEM!");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createTurtleScute(double price) {
        ItemStack item = new ItemStack(Material.TURTLE_SCUTE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GOLD + "Buy Item right now!");
        meta.setLore(List.of("", ChatColor.GRAY + "Price: " + ChatColor.GOLD + price,
                "", ChatColor.YELLOW + "Click to buy!"));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createArmadilloScute(double price) {
        ItemStack item = new ItemStack(Material.ARMADILLO_SCUTE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GOLD + "Buy Item right now!");
        meta.setLore(List.of("", ChatColor.GRAY + "Price: " + ChatColor.GOLD + price,
                "", ChatColor.RED + "Not enough coins!"));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createConfirm(double price) {
        ItemStack item = new ItemStack(Material.GREEN_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Confirm");
        meta.setLore(List.of(ChatColor.GRAY + "Cost: " + ChatColor.GOLD + price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack collectSoldItem(double price) {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GREEN + "Collect Sold Item");
        meta.setLore(List.of("", ChatColor.GRAY + "Value: " + ChatColor.GOLD + price,
                "", ChatColor.YELLOW + "Click to collect!"));
        item.setItemMeta(meta);
        return item;
    }

}
