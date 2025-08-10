package me.elaineqheart.auctionHouse.data.items;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import me.elaineqheart.auctionHouse.data.Messages;
import me.elaineqheart.auctionHouse.data.SettingManager;
import me.elaineqheart.auctionHouse.data.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
    public static ItemStack cancelAuction;
    public static ItemStack commandBlockInfo;
    public static ItemStack adminCancelAuction;
    public static ItemStack adminExpireAuction;
    public static ItemStack confirm;

    static {
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
        createCancelAuction();
        createCommandBlockInfo();
        createAdminCancelAuction();
        createAdminExpireAuction();
        createConfirmItem();
    }

    private static void createFillerItem(){
        ItemStack item = new ItemStack(Material.matchMaterial(SettingManager.fillerItem));
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
        meta.setItemName(Messages.getFormatted("items.locked-slot.name"));
        item.setItemMeta(meta);
        lockedSlot = item;
    }
    private static void createRefresh(){
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.refresh.name"));
        meta.setLore(Messages.getLoreList("items.refresh.lore"));
        item.setItemMeta(meta);
        refresh = item;
    }
    private static void createBackToMainMenu(){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.back-main-menu.name"));
        meta.setLore(Messages.getLoreList("items.back-main-menu.lore"));
        item.setItemMeta(meta);
        backToMainMenu = item;
    }
    private static void createBackToMyAuctions(){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.back-my-auctions.name"));
        meta.setLore(Messages.getLoreList("items.back-my-auctions.lore"));
        item.setItemMeta(meta);
        backToMyAuctions = item;
    }
    private static void createInfo(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.info.name"));
        //cast the tax value to int and then to double to avoid floating point issues
        String tax = ChatColor.GOLD + "" + (double)(int)(AuctionHouse.getPlugin().getConfig().getDouble("tax") * 1000) / 10 + "%";
        meta.setLore(Messages.getLoreList("items.info.lore", "%tax%", tax));
        item.setItemMeta(meta);
        info = item;
    }
    private static void createMyAuction(){
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-auctions.name"));
        meta.setLore(Messages.getLoreList("items.my-auctions.lore"));
        item.setItemMeta(meta);
        myAuction = item;
    }
    private static void createSortHighestPrice(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-highest-price.name"));
        meta.setLore(Messages.getLoreList("items.sort-highest-price.lore"));
        item.setItemMeta(meta);
        sortHighestPrice = item;
    }
    private static void createSortLowestPrice(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-lowest-price.name"));
        meta.setLore(Messages.getLoreList("items.sort-lowest-price.lore"));
        item.setItemMeta(meta);
        sortLowestPrice = item;
    }
    private static void createSortEndingSoon(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-ending-soon.name"));
        meta.setLore(Messages.getLoreList("items.sort-ending-soon.lore"));
        item.setItemMeta(meta);
        sortEndingSoon = item;
    }
    private static void createSortAlphabetical(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-alphabetical.name"));
        meta.setLore(Messages.getLoreList("items.sort-alphabetical.lore"));
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
        meta.setItemName(Messages.getFormatted("items.my-sort-all.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-all.lore"));
        item.setItemMeta(meta);
        mySortAllAuctions = item;
    }
    private static void createMySortSoldItems(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-sold.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-sold.lore"));
        item.setItemMeta(meta);
        mySortSoldItems = item;
    }
    private static void createMySortExpiredItems(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-expired.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-expired.lore"));
        item.setItemMeta(meta);
        mySortExpiredItems = item;
    }
    private static void createMySortActiveAuctions(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-active.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-active.lore"));
        item.setItemMeta(meta);
        mySortActiveAuctions = item;
    }
    private static void createEmptyPaper() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GRAY + "");
        meta.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(),"AuctionHouseSearch"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        emptyPaper = item;
    }
    private static void createCancel() {
        ItemStack item = new ItemStack(Material.RED_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cancel.name"));
        item.setItemMeta(meta);
        cancel = item;
    }
    private static void createCollectExpiredItem() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.collect-expired.name"));
        meta.setLore(Messages.getLoreList("items.collect-expired.lore"));
        item.setItemMeta(meta);
        collectExpiredItem = item;
    }
    private static void createCancelAuction() {
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cancel-auction.name"));
        meta.setLore(Messages.getLoreList("items.cancel-auction.lore"));
        item.setItemMeta(meta);
        cancelAuction = item;
    }
    private static void createCommandBlockInfo() {
        ItemStack item = new ItemStack(Material.STRUCTURE_BLOCK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-info.name"));
        meta.setLore(Messages.getLoreList("items.admin-info.lore"));
        item.setItemMeta(meta);
        commandBlockInfo = item;
    }
    private static void createAdminCancelAuction() {
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-cancel-auction.name"));
        meta.setLore(Messages.getLoreList("items.admin-cancel-auction.lore"));
        item.setItemMeta(meta);
        adminCancelAuction = item;
    }
    private static void createAdminExpireAuction() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-expire-auction.name"));
        meta.setLore(Messages.getLoreList("items.admin-expire-auction.lore"));
        item.setItemMeta(meta);
        adminExpireAuction = item;
    }
    private static void createConfirmItem() {
        ItemStack item = new ItemStack(Material.GREEN_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.confirm.name"));
        item.setItemMeta(meta);
        confirm = item;
    }

    public static ItemStack createDirt() {
        ItemStack item = new ItemStack(Material.DIRT);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.deleted.name"));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createItemFromNote(ItemNote note, Player p){
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        lore.addAll(Messages.getLoreList("items.auction.lore.default",
                "%player%", note.getPlayerName(),
                "%price%", StringUtils.formatNumber(note.getPrice())));
        if(Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()),p)) {
            lore.addAll(Messages.getLoreList("items.auction.lore.own-auction"));
        }
        if(note.isExpired() && !note.isSold()){
            if(note.getAdminMessage()!=null){
                if(note.getItem().equals(createDirt())){
                    lore.addAll(Messages.getLoreList("items.auction.lore.admin-deleted"));
                }else {
                    lore.addAll(Messages.getLoreList("items.auction.lore.admin-expired"));
                }
                lore.addAll(Messages.getLoreList("items.auction.lore.admin-message",
                        "%reason%", note.getAdminMessage()));
            }else {
                lore.addAll(Messages.getLoreList("items.auction.lore.expired"));
            }
        }else if(note.isSold()){
            lore.addAll(Messages.getLoreList("items.auction.lore.sold",
                    "%buyer%", note.getBuyerName()));
        }else if(note.isOnWaitingList()){
            lore.addAll(Messages.getLoreList("items.auction.lore.waiting-list",
                    "%time%", StringUtils.getTime(note.timeLeft() - SettingManager.auctionDuration, false)));
        }else{
            lore.addAll(Messages.getLoreList("items.auction.lore.active",
                    "%time%", StringUtils.getTime(note.timeLeft(), false)));
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
        lore.addAll(Messages.getLoreList("items.auction.lore.buying-item"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createAdminExpireItem(ItemNote note, String reason) {
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        lore.addAll(Messages.getLoreList("items.admin-expire-item.lore",
                "%player%", note.getPlayerName(),
                "%price%", StringUtils.formatNumber(note.getPrice()),
                "%reason%", reason));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createAdminDeleteItem(ItemNote note, String reason) {
        ItemStack item = createDirt();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        lore.addAll(Messages.getLoreList("items.admin-delete-item.lore",
                "%player%", note.getPlayerName(),
                "%price%", StringUtils.formatNumber(note.getPrice()),
                "%reason%", reason));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createTurtleScute(String price) {
        ItemStack item = new ItemStack(Material.TURTLE_SCUTE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.buy-item.name"));
        meta.setLore(Messages.getLoreList("items.buy-item.lore",
                        "%price%", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createArmadilloScute(String price) {
        ItemStack item = new ItemStack(Material.ARMADILLO_SCUTE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.not-enough-money.name"));
        meta.setLore(Messages.getLoreList("items.not-enough-money.lore",
                "%price%", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createConfirm(String price) {
        ItemStack item = new ItemStack(Material.GREEN_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.confirm-buy.name"));
        meta.setLore(Messages.getLoreList("items.confirm-buy.lore",
                        "%price%", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack collectSoldItem(String price) {
        ItemStack item = new ItemStack(Material.DIAMOND);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.collect-sold.name"));
        meta.setLore(Messages.getLoreList("items.collect-sold.lore",
                        "%price%", price));
        item.setItemMeta(meta);
        return item;
    }

}
