package me.elaineqheart.auctionHouse.data.items;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNote;
import me.elaineqheart.auctionHouse.data.yml.Messages;
import me.elaineqheart.auctionHouse.data.yml.Permissions;
import me.elaineqheart.auctionHouse.data.yml.SettingManager;
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

    public final static ItemStack fillerItem = createFillerItem();
    public final static ItemStack lockedSlot = createLockedSlot();
    public final static ItemStack refresh = createRefresh();
    public final static ItemStack backToMainMenu = createBackToMainMenu();
    public final static ItemStack backToMyAuctions = createBackToMyAuctions();
    public final static ItemStack info = createInfo();
    public final static ItemStack myAuction = createMyAuction();
    public final static ItemStack sortHighestPrice = createSortHighestPrice();
    public final static ItemStack sortLowestPrice = createSortLowestPrice();
    public final static ItemStack sortEndingSoon = createSortEndingSoon();
    public final static ItemStack sortAlphabetical = createSortAlphabetical();
    public final static ItemStack mySortAllAuctions = createMySortAllAuctions();
    public final static ItemStack mySortSoldItems = createMySortSoldItems();
    public final static ItemStack mySortExpiredItems = createMySortExpiredItems();
    public final static ItemStack mySortActiveAuctions =createMySortActiveAuctions();
    public final static ItemStack emptyPaper = createEmptyPaper();
    public final static ItemStack cancel = createCancel();
    public final static ItemStack collectExpiredItem = createCollectExpiredItem();
    public final static ItemStack cancelAuction = createCancelAuction();
    public final static ItemStack commandBlockInfo = createCommandBlockInfo();
    public final static ItemStack adminCancelAuction = createAdminCancelAuction();
    public final static ItemStack adminExpireAuction = createAdminExpireAuction();
    public final static ItemStack confirm = createConfirmItem();
    public final static ItemStack chooseItemBuyAmount = createChooseItemBuyAmount();
    public final static ItemStack loading = createLoadingItem();

    private static ItemStack createFillerItem(){
        ItemStack item = new ItemStack(Material.matchMaterial(SettingManager.fillerItem));
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createLockedSlot(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.locked-slot.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createRefresh(){
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.refresh.name"));
        meta.setLore(Messages.getLoreList("items.refresh.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createBackToMainMenu(){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.back-main-menu.name"));
        meta.setLore(Messages.getLoreList("items.back-main-menu.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createBackToMyAuctions(){
        ItemStack item = new ItemStack(Material.ARROW);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.back-my-auctions.name"));
        meta.setLore(Messages.getLoreList("items.back-my-auctions.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createInfo(){
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.info.name"));
        //cast the tax value to int and then to double to avoid floating point issues
        String tax = ChatColor.GOLD + "" + (double)(int)(AuctionHouse.getPlugin().getConfig().getDouble("tax") * 1000) / 10 + "%";
        meta.setLore(Messages.getLoreList("items.info.lore", "%tax%", tax));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMyAuction(){
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-auctions.name"));
        meta.setLore(Messages.getLoreList("items.my-auctions.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortHighestPrice(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-highest-price.name"));
        meta.setLore(Messages.getLoreList("items.sort-highest-price.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortLowestPrice(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-lowest-price.name"));
        meta.setLore(Messages.getLoreList("items.sort-lowest-price.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortEndingSoon(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-ending-soon.name"));
        meta.setLore(Messages.getLoreList("items.sort-ending-soon.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortAlphabetical(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-alphabetical.name"));
        meta.setLore(Messages.getLoreList("items.sort-alphabetical.lore"));
        item.setItemMeta(meta);
        return item;
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
    private static ItemStack createMySortAllAuctions(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-all.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-all.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortSoldItems(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-sold.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-sold.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortExpiredItems(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-expired.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-expired.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortActiveAuctions(){
        ItemStack item = new ItemStack(Material.HOPPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-active.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-active.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createEmptyPaper() {
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GRAY + "");
        meta.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(),"AuctionHouseSearch"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancel() {
        ItemStack item = new ItemStack(Material.RED_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cancel.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCollectExpiredItem() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.collect-expired.name"));
        meta.setLore(Messages.getLoreList("items.collect-expired.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancelAuction() {
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cancel-auction.name"));
        meta.setLore(Messages.getLoreList("items.cancel-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCommandBlockInfo() {
        ItemStack item = new ItemStack(Material.STRUCTURE_BLOCK);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-info.name"));
        meta.setLore(Messages.getLoreList("items.admin-info.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createAdminCancelAuction() {
        ItemStack item = new ItemStack(Material.RED_CONCRETE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-cancel-auction.name"));
        meta.setLore(Messages.getLoreList("items.admin-cancel-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createAdminExpireAuction() {
        ItemStack item = new ItemStack(Material.RED_DYE);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-expire-auction.name"));
        meta.setLore(Messages.getLoreList("items.admin-expire-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createConfirmItem() {
        ItemStack item = new ItemStack(Material.GREEN_BANNER);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.confirm.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createChooseItemBuyAmount() {
        ItemStack item = new ItemStack(Material.SPRUCE_HANGING_SIGN);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.choose-item-buy-amount.name"));
        meta.setLore(Messages.getLoreList("items.choose-item-buy-amount.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createLoadingItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.loading.name"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createDirt() {
        ItemStack item = new ItemStack(Material.DIRT);
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.deleted.name"));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createItemFromNote(ItemNote note, Player p, boolean ownAuction){
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        if(isShulkerBox(item)) {
            lore.addAll(Messages.getLoreList("items.auction.lore.shulker-preview"));
        }
        lore.addAll(Messages.getLoreList("items.auction.lore.default",
                "%player%", note.getPlayerName(),
                "%price%", StringUtils.formatNumber(ownAuction ? note.getPrice() : note.getCurrentPrice())));
        if(Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()),p)) {
            lore.addAll(Messages.getLoreList("items.auction.lore.own-auction"));
        }
        if(note.isExpired() && note.getAdminMessage()!=null && !note.getAdminMessage().isEmpty()) {
            if (note.getItem().equals(createDirt())) {
                lore.addAll(Messages.getLoreList("items.auction.lore.admin-deleted"));
            } else {
                lore.addAll(Messages.getLoreList("items.auction.lore.admin-expired"));
            }
            lore.addAll(Messages.getLoreList("items.auction.lore.admin-message",
                    "%reason%", note.getAdminMessage()));
            lore.addAll(Messages.getLoreList("items.auction.lore.expired"));
        }else if(note.isSold() && note.isOnAuction()) {
            if(ownAuction) {
                lore.addAll(Messages.getLoreList("items.auction.lore.partially-sold",
                        "%sold%", String.valueOf(note.getItem().getAmount() - note.getPartiallySoldAmountLeft()),
                        "%total%", String.valueOf(note.getItem().getAmount()),
                        "%buyer%", note.getBuyerName()));
            } else {
                item.setAmount(note.getPartiallySoldAmountLeft());
            }
            if(!note.isExpired()) {
                lore.addAll(Messages.getLoreList("items.auction.lore.active",
                        "%time%", StringUtils.getTime(note.timeLeft(), false)));
            } else {
                lore.addAll(Messages.getLoreList("items.auction.lore.expired"));
            }
        }else if(note.isExpired() && !note.isSold()) {
            lore.addAll(Messages.getLoreList("items.auction.lore.expired"));
        }else if(note.isSold() && !note.isOnAuction()) {
            lore.addAll(Messages.getLoreList("items.auction.lore.sold",
                    "%buyer%", note.getBuyerName()));
        }else if(note.isOnWaitingList()){
            lore.addAll(Messages.getLoreList("items.auction.lore.waiting-list",
                    "%time%", StringUtils.getTime(note.timeLeft() - Permissions.getAuctionDuration(p), false)));
        }else{
            lore.addAll(Messages.getLoreList("items.auction.lore.active",
                    "%time%", StringUtils.getTime(note.timeLeft(), false)));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCollectingItemFromNote(ItemNote note, Player p) {
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.addAll(Messages.getLoreList("items.auction.lore.default",
                "%player%", note.getPlayerName(),
                "%price%", StringUtils.formatNumber(note.getSoldPrice())));
        lore.addAll(Messages.getLoreList("items.auction.lore.own-auction"));
        lore.addAll(Messages.getLoreList("items.auction.lore.sold",
                "%buyer%", note.getBuyerName()));
        item.setAmount(item.getAmount() - note.getPartiallySoldAmountLeft());

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBuyingItemDisplay(ItemStack item) {
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

    public static boolean isShulkerBox(ItemStack item) {
        if (item == null) return false;
        Material type = item.getType();
        return type == Material.SHULKER_BOX ||
                type == Material.WHITE_SHULKER_BOX ||
                type == Material.ORANGE_SHULKER_BOX ||
                type == Material.MAGENTA_SHULKER_BOX ||
                type == Material.LIGHT_BLUE_SHULKER_BOX ||
                type == Material.YELLOW_SHULKER_BOX ||
                type == Material.LIME_SHULKER_BOX ||
                type == Material.PINK_SHULKER_BOX ||
                type == Material.GRAY_SHULKER_BOX ||
                type == Material.LIGHT_GRAY_SHULKER_BOX ||
                type == Material.CYAN_SHULKER_BOX ||
                type == Material.PURPLE_SHULKER_BOX ||
                type == Material.BLUE_SHULKER_BOX ||
                type == Material.BROWN_SHULKER_BOX ||
                type == Material.GREEN_SHULKER_BOX ||
                type == Material.RED_SHULKER_BOX ||
                type == Material.BLACK_SHULKER_BOX;
    }

}
