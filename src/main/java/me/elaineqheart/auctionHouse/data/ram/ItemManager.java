package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
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
    public static ItemStack cancelBINAuction;
    public static ItemStack cancelAuction;
    public static ItemStack commandBlockInfo;
    public static ItemStack adminCancelAuction;
    public static ItemStack adminExpireAuction;
    public static ItemStack confirm;
    public static ItemStack chooseItemBuyAmount;
    public static ItemStack refresh;
    public static ItemStack myBids;

    static {
        reload();
    }

    public static void reload() {
        fillerItem = createFillerItem();
        lockedSlot = createLockedSlot();
        backToMainMenu = createBackToMainMenu();
        backToMyAuctions = createBackToMyAuctions();
        info = createInfo();
        myAuction = createMyAuction();
        sortHighestPrice = createSortHighestPrice();
        sortLowestPrice = createSortLowestPrice();
        sortEndingSoon = createSortEndingSoon();
        sortAlphabetical = createSortAlphabetical();
        mySortAllAuctions = createMySortAllAuctions();
        mySortSoldItems = createMySortSoldItems();
        mySortExpiredItems = createMySortExpiredItems();
        mySortActiveAuctions =createMySortActiveAuctions();
        emptyPaper = createEmptyPaper();
        cancel = createCancel();
        collectExpiredItem = createCollectExpiredItem();
        cancelBINAuction = createCancelBINAuction();
        cancelAuction = createCancelAuction();
        commandBlockInfo = createCommandBlockInfo();
        adminCancelAuction = createAdminCancelAuction();
        adminExpireAuction = createAdminExpireAuction();
        confirm = createConfirmItem();
        chooseItemBuyAmount = createChooseItemBuyAmount();
        refresh = createRefresh();
        myBids = createMyBids();
    }

    private static ItemStack createFillerItem(){
        ItemStack item = ConfigManager.layout.getItem("#");
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createLockedSlot(){
        ItemStack item = ConfigManager.layout.getItem("locked-slot");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.locked-slot.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createRefresh(){
        ItemStack item = ConfigManager.layout.getItem("r");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.refresh.name"));
        meta.setLore(M.getLoreList("items.refresh.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMyBids(){
        ItemStack item = ConfigManager.layout.getItem("d");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.my-bids.name"));
        meta.setLore(M.getLoreList("items.my-bids.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createBackToMainMenu(){
        ItemStack item = ConfigManager.layout.getItem("b");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.back-main-menu.name"));
        meta.setLore(M.getLoreList("items.back-main-menu.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createBackToMyAuctions(){
        ItemStack item = ConfigManager.layout.getItem("back-to-my-auctions");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.back-my-auctions.name"));
        meta.setLore(M.getLoreList("items.back-my-auctions.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createInfo(){
        ItemStack item = ConfigManager.layout.getItem("i");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.info.name"));
        //cast the tax value to int and then to double to avoid floating point issues
        String tax = ChatColor.GOLD + "" + (double)(int)(AuctionHouse.getPlugin().getConfig().getDouble("tax") * 1000) / 10 + "%";
        meta.setLore(M.getLoreList("items.info.lore", "%tax%", tax));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMyAuction(){
        ItemStack item = ConfigManager.layout.getItem("m");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.my-auctions.name"));
        meta.setLore(M.getLoreList("items.my-auctions.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortHighestPrice(){
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.sort-highest-price.name"));
        meta.setLore(M.getLoreList("items.sort-highest-price.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortLowestPrice(){
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.sort-lowest-price.name"));
        meta.setLore(M.getLoreList("items.sort-lowest-price.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortEndingSoon(){
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.sort-ending-soon.name"));
        meta.setLore(M.getLoreList("items.sort-ending-soon.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortAlphabetical(){
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.sort-alphabetical.name"));
        meta.setLore(M.getLoreList("items.sort-alphabetical.lore"));
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
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.my-sort-all.name"));
        meta.setLore(M.getLoreList("items.my-sort-all.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortSoldItems(){
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.my-sort-sold.name"));
        meta.setLore(M.getLoreList("items.my-sort-sold.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortExpiredItems(){
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.my-sort-expired.name"));
        meta.setLore(M.getLoreList("items.my-sort-expired.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortActiveAuctions(){
        ItemStack item = ConfigManager.layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.my-sort-active.name"));
        meta.setLore(M.getLoreList("items.my-sort-active.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createEmptyPaper() {
        ItemStack item = ConfigManager.layout.getItem("anvil-search-paper");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GRAY + "");
        meta.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(),"AuctionHouseSearch"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancel() {
        ItemStack item = ConfigManager.layout.getItem("cancel");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.cancel.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCollectExpiredItem() {
        ItemStack item = ConfigManager.layout.getItem("collect-expired-item");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.collect-expired.name"));
        meta.setLore(M.getLoreList("items.collect-expired.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancelBINAuction() {
        ItemStack item = ConfigManager.layout.getItem("cancel-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.cancel-auction.name"));
        meta.setLore(M.getLoreList("items.cancel-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancelAuction() {
        ItemStack item = ConfigManager.layout.getItem("cancel-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.cancel-bid-auction.name"));
        meta.setLore(M.getLoreList("items.cancel-bid-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCommandBlockInfo() {
        ItemStack item = ConfigManager.layout.getItem("command-block-info");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.admin-info.name"));
        meta.setLore(M.getLoreList("items.admin-info.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createAdminCancelAuction() {
        ItemStack item = ConfigManager.layout.getItem("admin-cancel-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.admin-cancel-auction.name"));
        meta.setLore(M.getLoreList("items.admin-cancel-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createAdminExpireAuction() {
        ItemStack item = ConfigManager.layout.getItem("admin-expire-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.admin-expire-auction.name"));
        meta.setLore(M.getLoreList("items.admin-expire-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createConfirmItem() {
        ItemStack item = ConfigManager.layout.getItem("confirm");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.confirm.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createChooseItemBuyAmount() {
        ItemStack item = ConfigManager.layout.getItem("choose-item-buy-amount");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.choose-item-buy-amount.name"));
        meta.setLore(M.getLoreList("items.choose-item-buy-amount.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createLoadingItem() {
        ItemStack item = ConfigManager.layout.getItem("loading");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.loading.name"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createDirt() {
        ItemStack item = ConfigManager.layout.getItem("dirt");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.deleted.name"));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createItemFromNote(ItemNote note, Player p, boolean ownAuction){
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if (lore==null) lore = new ArrayList<>();
        if (isShulkerBox(item)) {
            lore.addAll(M.getLoreList("items.auction.lore.shulker-preview"));
        }
        if (!note.isBIDAuction()) {
            lore.addAll(M.getLoreList("items.auction.lore.default", ownAuction ? note.getPrice() : note.getCurrentPrice(),
                    "%seller%", M.formatSeller(note.getPlayerName(), note.getPlayerUUID())));
        } else {
            if (note.getBidHistoryList().isEmpty()) {
                lore.addAll(M.getLoreList("items.auction.lore.default-starting-bid", note.getPrice(),
                        "%seller%", M.formatSeller(note.getPlayerName(), note.getPlayerUUID())));
            } else {
                lore.addAll(M.getLoreList("items.auction.lore.default-bid", note.getPrice(),
                        "%seller%", M.formatSeller(note.getPlayerName(), note.getPlayerUUID()),
                        "%amountOfBids%", String.valueOf(note.getBidHistoryList().size()),
                        "%buyer%", M.formatBuyer(note.getLastBidderName(), note.getLastBidder())));
            }
        }
        if (Objects.equals(Bukkit.getPlayer(note.getPlayerUUID()),p)) {
            lore.addAll(M.getLoreList("items.auction.lore.own-auction"));
        }
        if (note.isExpired() && note.getAdminMessage()!=null && !note.getAdminMessage().isEmpty()) {
            if (note.getItem().equals(createDirt())) {
                lore.addAll(M.getLoreList("items.auction.lore.admin-deleted"));
            } else {
                lore.addAll(M.getLoreList("items.auction.lore.admin-expired"));
            }
            lore.addAll(M.getLoreList("items.auction.lore.admin-message",
                    "%reason%", note.getAdminMessage()));
            lore.addAll(M.getLoreList("items.auction.lore.expired"));
        } else if (note.isSold() && note.isOnAuction()) {
            if (ownAuction) {
                lore.addAll(M.getLoreList("items.auction.lore.partially-sold",
                        "%sold%", String.valueOf(note.getItem().getAmount() - note.getPartiallySoldAmountLeft()),
                        "%total%", String.valueOf(note.getItem().getAmount()),
                        "%buyer%", M.formatBuyer(note.getBuyerName(), note.getBuyerUUID())));
            } else {
                item.setAmount(note.getPartiallySoldAmountLeft());
            }
            if (!note.isExpired()) {
                lore.addAll(M.getLoreList("items.auction.lore.active",
                        "%time%", StringUtils.getTime(note.getTimeLeft(), true)));
            } else {
                lore.addAll(M.getLoreList("items.auction.lore.expired"));
            }
        } else if (note.isExpired() && (!note.isSold() && !note.isBIDAuction() || !note.hasBidHistory() && note.isBIDAuction())) {
            lore.addAll(M.getLoreList("items.auction.lore.expired"));
        } else if (note.isBIDAuction() && note.hasBidHistory() && note.isExpired()) {
            lore.addAll(M.getLoreList("items.auction.lore.ended"));
        } else if (note.isSold() && !note.isOnAuction()) {
            lore.addAll(M.getLoreList("items.auction.lore.sold",
                    "%buyer%", M.formatBuyer(note.getBuyerName(), note.getBuyerUUID())));
        } else if (note.isOnWaitingList()) {
            lore.addAll(M.getLoreList("items.auction.lore.waiting-list",
                    "%time%", StringUtils.getTime(
                            note.getTimeLeft() - ConfigManager.permissions.getAuctionDuration(p, note.isBIDAuction()), true
                    )));
        } else {
            lore.addAll(M.getLoreList("items.auction.lore.active",
                    "%time%", StringUtils.getTime(note.getTimeLeft(), true)));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCollectingItemFromNote(ItemNote note) {
        ItemStack item = note.getItem();
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        lore.addAll(M.getLoreList("items.auction.lore.default", note.getSoldPrice(),
                "%seller%", M.formatSeller(note.getPlayerName(), note.getPlayerUUID())));
        lore.addAll(M.getLoreList("items.auction.lore.own-auction"));
        lore.addAll(M.getLoreList("items.auction.lore.sold",
                "%buyer%", M.formatBuyer(note.getBuyerName(), note.getBuyerUUID())));
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
        lore.addAll(M.getLoreList("items.auction.lore.buying-item"));
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
        lore.addAll(M.getLoreList("items.admin-expire-item.lore", note.getPrice(),
                "%seller%", M.formatSeller(note.getPlayerName(), note.getPlayerUUID()),
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
        lore.addAll(M.getLoreList("items.admin-delete-item.lore", note.getPrice(),
                "%seller%", M.formatSeller(note.getPlayerName(), note.getPlayerUUID()),
                "%reason%", reason));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createTurtleScute(double price) {
        ItemStack item = ConfigManager.layout.getItem("turtle-scute-confirm");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.buy-item.name"));
        meta.setLore(M.getLoreList("items.buy-item.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createArmadilloScute(double price) {
        ItemStack item = ConfigManager.layout.getItem("cannot-afford");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.not-enough-money.name"));
        meta.setLore(M.getLoreList("items.not-enough-money.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createConfirm(double price) {
        ItemStack item = ConfigManager.layout.getItem("confirm");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.confirm-buy.name"));
        meta.setLore(M.getLoreList("items.confirm-buy.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack collectSoldItem(double price) {
        ItemStack item = ConfigManager.layout.getItem("collect-sold-item");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.collect-sold.name"));
        meta.setLore(M.getLoreList("items.collect-sold.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBidHistory(List<Bid> bidHistory) {
        ItemStack item = ConfigManager.layout.getItem("bid-history");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.bid-history.name"));
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        lore.addAll(M.getLoreList("items.bid-history.lore",
                "%amountOfBids%", String.valueOf(bidHistory.size())));
        for(int i = 0; i < Math.min(bidHistory.size(), 6); i++) {
            Bid bid = bidHistory.get(bidHistory.size()-1-i);
            lore.addAll(M.getLoreList("items.bid-history.bid", bid.getPrice(),
                    "%player%", M.formatPlayer(bid.getPlayerName(), bid.getPlayerID()),
                    "%time%", bid.getTimeAgo()));
        }
        if(bidHistory.size() - 6 > 0) {
            lore.addAll(M.getLoreList("items.bid-history.more",
                    "%amount%", String.valueOf(bidHistory.size()-6)));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBidExplanation(double amount) {
        ItemStack item = ConfigManager.layout.getItem("bid-explanation");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.bid-explanation.name", amount));
        meta.setLore(M.getLoreList("items.bid-explanation.lore", amount));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createSubmitBid(double amount, double previousBid) {
        ItemStack item = ConfigManager.layout.getItem("submit-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if(previousBid == 0) {
            meta.setItemName(M.getFormatted("items.submit-bid.name", amount));
            meta.setLore(M.getLoreList("items.submit-bid.lore", amount));
        } else {
            meta.setItemName(M.getFormatted("items.submit-another-bid.name", amount));
            List<String> lore = M.getLoreList("items.submit-another-bid.lore");
            lore.replaceAll(s -> M.replace(s, amount, previousBid, amount-previousBid));
            meta.setLore(lore);
        }
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createOwnBid(double amount) {
        ItemStack item = ConfigManager.layout.getItem("own-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.own-bid.name", amount));
        meta.setLore(M.getLoreList("items.own-bid.lore", amount));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCannotAffordBid(double amount) {
        ItemStack item = ConfigManager.layout.getItem("cannot-afford-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.cannot-afford-bid.name", amount));
        meta.setLore(M.getLoreList("items.cannot-afford-bid.lore", amount));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createTopBid(double amount, double newBid) {
        ItemStack item = ConfigManager.layout.getItem("top-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.top-bid.name", amount));
        List<String> lore = M.getLoreList("items.top-bid.lore");
        lore.replaceAll(s -> M.replace(s, amount, newBid));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBINFilter(AhConfiguration.BINFilter binFilter) {
        ItemStack item = switch(binFilter) {
            case ALL -> ConfigManager.layout.getItem("f");
            case BIN_ONLY -> ConfigManager.layout.getItem("bin-filter-bin");
            case AUCTIONS_ONLY -> ConfigManager.layout.getItem("bin-filter-auctions");
        };
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted(switch(binFilter) {
            case ALL -> "items.bin-filter-all.name";
            case BIN_ONLY -> "items.bin-filter-bin.name";
            case AUCTIONS_ONLY -> "items.bin-filter-auctions.name";
        }));
        meta.setLore(M.getLoreList(switch(binFilter) {
            case ALL -> "items.bin-filter-all.lore";
            case BIN_ONLY -> "items.bin-filter-bin.lore";
            case AUCTIONS_ONLY -> "items.bin-filter-auctions.lore";
        }));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCollectAuction(ItemNote note) {
        ItemStack item = ConfigManager.layout.getItem("collect-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.collect-auction.name"));
        meta.setLore(M.getLoreList("items.collect-auction.lore", note.getBidHistoryList().getLast().getPrice()));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCollectCoins(ItemNote note, Player p) {
        ItemStack item = ConfigManager.layout.getItem("collect-coins");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(M.getFormatted("items.collect-coins.name"));
        List<String> lore = M.getLoreList("items.collect-coins.lore", note.getBidHistoryList().getLast().getPrice(),
                "%player%", M.formatPlayer(note.getLastBidderName(), note.getLastBidder()));
        lore.replaceAll(s -> M.replace(s, note.getBidHistoryList().getLast().getPrice(), note.getBid(p)));
        meta.setLore(lore);
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
