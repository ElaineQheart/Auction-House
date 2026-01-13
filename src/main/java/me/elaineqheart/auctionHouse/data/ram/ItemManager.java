package me.elaineqheart.auctionHouse.data.ram;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.AuctionHouseGUI;
import me.elaineqheart.auctionHouse.GUI.impl.MyAuctionsGUI;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Layout;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Permissions;
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
        ItemStack item = Layout.getItem("#");
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setHideTooltip(true);
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createLockedSlot(){
        ItemStack item = Layout.getItem("locked-slot");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.locked-slot.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createRefresh(){
        ItemStack item = Layout.getItem("r");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.refresh.name"));
        meta.setLore(Messages.getLoreList("items.refresh.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMyBids(){
        ItemStack item = Layout.getItem("d");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-bids.name"));
        meta.setLore(Messages.getLoreList("items.my-bids.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createBackToMainMenu(){
        ItemStack item = Layout.getItem("b");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.back-main-menu.name"));
        meta.setLore(Messages.getLoreList("items.back-main-menu.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createBackToMyAuctions(){
        ItemStack item = Layout.getItem("back-to-my-auctions");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.back-my-auctions.name"));
        meta.setLore(Messages.getLoreList("items.back-my-auctions.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createInfo(){
        ItemStack item = Layout.getItem("i");
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
        ItemStack item = Layout.getItem("m");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-auctions.name"));
        meta.setLore(Messages.getLoreList("items.my-auctions.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortHighestPrice(){
        ItemStack item = Layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-highest-price.name"));
        meta.setLore(Messages.getLoreList("items.sort-highest-price.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortLowestPrice(){
        ItemStack item = Layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-lowest-price.name"));
        meta.setLore(Messages.getLoreList("items.sort-lowest-price.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortEndingSoon(){
        ItemStack item = Layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.sort-ending-soon.name"));
        meta.setLore(Messages.getLoreList("items.sort-ending-soon.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createSortAlphabetical(){
        ItemStack item = Layout.getItem("o");
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
        ItemStack item = Layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-all.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-all.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortSoldItems(){
        ItemStack item = Layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-sold.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-sold.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortExpiredItems(){
        ItemStack item = Layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-expired.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-expired.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createMySortActiveAuctions(){
        ItemStack item = Layout.getItem("o");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.my-sort-active.name"));
        meta.setLore(Messages.getLoreList("items.my-sort-active.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createEmptyPaper() {
        ItemStack item = Layout.getItem("anvil-search-paper");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(ChatColor.GRAY + "");
        meta.getPersistentDataContainer().set(new NamespacedKey(AuctionHouse.getPlugin(),"AuctionHouseSearch"), PersistentDataType.BOOLEAN, true);
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancel() {
        ItemStack item = Layout.getItem("cancel");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cancel.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCollectExpiredItem() {
        ItemStack item = Layout.getItem("collect-expired-item");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.collect-expired.name"));
        meta.setLore(Messages.getLoreList("items.collect-expired.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancelBINAuction() {
        ItemStack item = Layout.getItem("cancel-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cancel-auction.name"));
        meta.setLore(Messages.getLoreList("items.cancel-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCancelAuction() {
        ItemStack item = Layout.getItem("cancel-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cancel-bid-auction.name"));
        meta.setLore(Messages.getLoreList("items.cancel-bid-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createCommandBlockInfo() {
        ItemStack item = Layout.getItem("command-block-info");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-info.name"));
        meta.setLore(Messages.getLoreList("items.admin-info.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createAdminCancelAuction() {
        ItemStack item = Layout.getItem("admin-cancel-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-cancel-auction.name"));
        meta.setLore(Messages.getLoreList("items.admin-cancel-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createAdminExpireAuction() {
        ItemStack item = Layout.getItem("admin-expire-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.admin-expire-auction.name"));
        meta.setLore(Messages.getLoreList("items.admin-expire-auction.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createConfirmItem() {
        ItemStack item = Layout.getItem("confirm");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.confirm.name"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createChooseItemBuyAmount() {
        ItemStack item = Layout.getItem("choose-item-buy-amount");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.choose-item-buy-amount.name"));
        meta.setLore(Messages.getLoreList("items.choose-item-buy-amount.lore"));
        item.setItemMeta(meta);
        return item;
    }
    private static ItemStack createLoadingItem() {
        ItemStack item = Layout.getItem("loading");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.loading.name"));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createDirt() {
        ItemStack item = Layout.getItem("dirt");
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
        if(note.isBINAuction()) {
            lore.addAll(Messages.getLoreList("items.auction.lore.default", ownAuction ? note.getPrice() : note.getCurrentPrice(),
                    "%player%", note.getPlayerName()));
        } else {
            if(note.getBidHistoryList().isEmpty()) {
                lore.addAll(Messages.getLoreList("items.auction.lore.default-starting-bid", note.getPrice(),
                        "%player%", note.getPlayerName()));
            } else {
                lore.addAll(Messages.getLoreList("items.auction.lore.default-bid", note.getPrice(),
                        "%player%", note.getPlayerName(),
                        "%amountOfBids%", String.valueOf(note.getBidHistoryList().size()),
                        "%bidder%", note.getLastBidderName()));
            }
        }
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
                        "%time%", StringUtils.getTime(note.getTimeLeft(), true)));
            } else {
                lore.addAll(Messages.getLoreList("items.auction.lore.expired"));
            }
        }else if(note.isExpired() && (!note.isSold() && note.isBINAuction() || !note.hasBidHistory() && !note.isBINAuction())) {
            lore.addAll(Messages.getLoreList("items.auction.lore.expired"));
        }else if (!note.isBINAuction() && note.hasBidHistory() && note.isExpired()) {
            lore.addAll(Messages.getLoreList("items.auction.lore.ended"));
        }else if(note.isSold() && !note.isOnAuction()) {
            lore.addAll(Messages.getLoreList("items.auction.lore.sold",
                    "%buyer%", note.getBuyerName()));
        }else if(note.isOnWaitingList()) {
            lore.addAll(Messages.getLoreList("items.auction.lore.waiting-list",
                    "%time%", StringUtils.getTime(
                            note.getTimeLeft() - Permissions.getAuctionDuration(p, note.isBINAuction()), true
                    )));
        }else{
            lore.addAll(Messages.getLoreList("items.auction.lore.active",
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
        lore.addAll(Messages.getLoreList("items.auction.lore.default", note.getSoldPrice(),
                "%player%", note.getPlayerName()));
        lore.addAll(Messages.getLoreList("items.auction.lore.own-auction"));
        lore.addAll(Messages.getLoreList("items.auction.lore.sold",
                "%buyer%", note.getLastBidderName()));
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
        lore.addAll(Messages.getLoreList("items.admin-expire-item.lore", note.getPrice(),
                "%player%", note.getPlayerName(),
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
        lore.addAll(Messages.getLoreList("items.admin-delete-item.lore", note.getPrice(),
                "%player%", note.getPlayerName(),
                "%reason%", reason));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createTurtleScute(double price) {
        ItemStack item = Layout.getItem("turtle-scute-confirm");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.buy-item.name"));
        meta.setLore(Messages.getLoreList("items.buy-item.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createArmadilloScute(double price) {
        ItemStack item = Layout.getItem("cannot-afford");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.not-enough-money.name"));
        meta.setLore(Messages.getLoreList("items.not-enough-money.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createConfirm(double price) {
        ItemStack item = Layout.getItem("confirm");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.confirm-buy.name"));
        meta.setLore(Messages.getLoreList("items.confirm-buy.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack collectSoldItem(double price) {
        ItemStack item = Layout.getItem("collect-sold-item");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.collect-sold.name"));
        meta.setLore(Messages.getLoreList("items.collect-sold.lore", price));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBidHistory(List<Bid> bidHistory) {
        ItemStack item = Layout.getItem("bid-history");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.bid-history.name"));
        List<String> lore = meta.getLore();
        if(lore==null) lore = new ArrayList<>();
        lore.addAll(Messages.getLoreList("items.bid-history.lore",
                "%amountOfBids%", String.valueOf(bidHistory.size())));
        for(int i = 0; i < Math.min(bidHistory.size(), 6); i++) {
            Bid bid = bidHistory.get(bidHistory.size()-1-i);
            lore.addAll(Messages.getLoreList("items.bid-history.bid", bid.getPrice(),
                    "%player%", bid.getPlayerName(),
                    "%time%", bid.getTimeAgo()));
        }
        if(bidHistory.size() - 6 > 0) {
            lore.addAll(Messages.getLoreList("items.bid-history.more",
                    "%amount%", String.valueOf(bidHistory.size()-6)));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBidExplanation(double amount) {
        ItemStack item = Layout.getItem("bid-explanation");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.bid-explanation.name", amount));
        meta.setLore(Messages.getLoreList("items.bid-explanation.lore", amount));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createSubmitBid(double amount, double previousBid) {
        ItemStack item = Layout.getItem("submit-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        if(previousBid == 0) {
            meta.setItemName(Messages.getFormatted("items.submit-bid.name", amount));
            meta.setLore(Messages.getLoreList("items.submit-bid.lore", amount));
        } else {
            meta.setItemName(Messages.getFormatted("items.submit-another-bid.name", amount));
            meta.setLore(Messages.getLoreList("items.submit-another-bid.lore", amount,
                    "%price2%", StringUtils.formatPrice(previousBid),
                    "%number2%", StringUtils.formatNumber(previousBid),
                    "%price3%", StringUtils.formatPrice(amount - previousBid),
                    "%number3%", StringUtils.formatNumber(amount - previousBid)));
        }
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createOwnBid(double amount) {
        ItemStack item = Layout.getItem("own-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.own-bid.name", amount));
        meta.setLore(Messages.getLoreList("items.own-bid.lore", amount));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCannotAffordBid(double amount) {
        ItemStack item = Layout.getItem("cannot-afford-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.cannot-afford-bid.name", amount));
        meta.setLore(Messages.getLoreList("items.cannot-afford-bid.lore", amount));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createTopBid(double amount, double newBid) {
        ItemStack item = Layout.getItem("top-bid");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.top-bid.name", amount));
        meta.setLore(Messages.getLoreList("items.top-bid.lore", amount,
                "%price2%", StringUtils.formatPrice(newBid),
                "%number2%", StringUtils.formatNumber(newBid)));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createBINFilter(AhConfiguration.BINFilter binFilter) {
        ItemStack item = switch(binFilter) {
            case ALL -> Layout.getItem("f");
            case BIN_ONLY -> Layout.getItem("bin-filter-bin");
            case AUCTIONS_ONLY -> Layout.getItem("bin-filter-auctions");
        };
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted(switch(binFilter) {
            case ALL -> "items.bin-filter-all.name";
            case BIN_ONLY -> "items.bin-filter-bin.name";
            case AUCTIONS_ONLY -> "items.bin-filter-auctions.name";
        }));
        meta.setLore(Messages.getLoreList(switch(binFilter) {
            case ALL -> "items.bin-filter-all.lore";
            case BIN_ONLY -> "items.bin-filter-bin.lore";
            case AUCTIONS_ONLY -> "items.bin-filter-auctions.lore";
        }));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCollectAuction(ItemNote note) {
        ItemStack item = Layout.getItem("collect-auction");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.collect-auction.name"));
        meta.setLore(Messages.getLoreList("items.collect-auction.lore", note.getBidHistoryList().getLast().getPrice()));
        item.setItemMeta(meta);
        return item;
    }
    public static ItemStack createCollectCoins(ItemNote note, Player p) {
        ItemStack item = Layout.getItem("collect-coins");
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.setItemName(Messages.getFormatted("items.collect-coins.name"));
        meta.setLore(Messages.getLoreList("items.collect-coins.lore", note.getBidHistoryList().getLast().getPrice(),
                "%price2%", StringUtils.formatPrice(note.getBid(p)),
                "%number2%", StringUtils.formatNumber(note.getBid(p)),
                "%player%", note.getLastBidderName()));
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
