package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemStackConverter;
import me.elaineqheart.auctionHouse.data.persistentStorage.TradeHistory;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.SellOptions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Purchase & sale history of the player (hub -> "History"). The trades come from TradeHistory.
// Layout: layout.yml (history-layout, '.' = a trade), texts: messages.yml (items.history-*, history-text.*).
public class HistoryGUI extends InventoryGUI {

    public enum Filter { ALL, BOUGHT, SOLD }

    private final Player p;
    private Filter filter = Filter.ALL;
    private int page;
    private int pages = 1;
    private List<TradeHistory.Trade> trades = List.of(); // the trades of the current page
    private int request; // ignores the result of a previous page load when the player clicked again meanwhile

    private HistoryGUI(Player p) {
        super();
        this.p = p;
    }

    // opens the menu right away, the trades are loaded from the database in the background
    public static void open(Player p) {
        HistoryGUI gui = new HistoryGUI(p);
        AuctionHouse.getGuiManager().openGUI(gui, p);
        gui.load();
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, ConfigManager.layout.historyLayout.size() * 9, M.getFormatted("inventory-titles.history"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(ConfigManager.layout.historyLayout);
        super.decorate(player);
    }

    private void update() {
        decorate(p);
    }

    private static List<Integer> tradeSlots(List<String> places) {
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                if (places.get(i).charAt(j) == '.') slots.add(i * 9 + j / 2);
            }
        }
        return slots;
    }

    private void load() {
        int id = ++request;
        int perPage = Math.max(1, tradeSlots(ConfigManager.layout.historyLayout).size());
        Boolean bought = filter == Filter.ALL ? null : filter == Filter.BOUGHT;
        TradeHistory.page(p.getUniqueId(), bought, page * perPage, perPage).thenAccept(result ->
                AuctionHouse.getScheduler().entitySpecificScheduler(p).run(() -> {
                    if (id != request) return;
                    pages = Math.max(1, (result.total() + perPage - 1) / perPage);
                    if (page > pages - 1) { // trades were removed meanwhile
                        page = pages - 1;
                        load();
                        return;
                    }
                    trades = result.trades();
                    update();
                }, null));
    }

    private void fillOutPlaces(List<String> places) {
        for (int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                int slot = i * 9 + j / 2;
                switch (places.get(i).charAt(j)) {
                    case '#' -> this.addButton(slot, fillerItem());
                    case 'F' -> this.addButton(slot, filterButton());
                    case 'p' -> this.addButton(slot, previousPage());
                    case 'n' -> this.addButton(slot, nextPage());
                    case 'h' -> this.addButton(slot, backToHub());
                    case '.', ' ' -> {}
                }
            }
        }
        List<Integer> tradeSlots = tradeSlots(places);
        for (int k = 0; k < tradeSlots.size(); k++) {
            if (k < trades.size()) {
                this.addButton(tradeSlots.get(k), tradeButton(trades.get(k)));
            } else {
                this.addButton(tradeSlots.get(k), new InventoryButton()
                        .creator(player -> null)
                        .consumer(event -> {}));
            }
        }
    }

    private InventoryButton fillerItem() {
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }

    private InventoryButton backToHub() {
        return new InventoryButton()
                .creator(player -> ItemManager.backToHub)
                .consumer(event -> {
                    Sounds.closeEnderChest(event);
                    AuctionHouse.getGuiManager().openGUI(new HubGUI(p), p);
                });
    }

    private String filterText(Filter f) {
        return M.getFormatted("history-text.filter-" + f.name().toLowerCase());
    }

    private InventoryButton filterButton() {
        ItemStack item = ConfigManager.layout.getItem("history-filter");
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setItemName(M.getFormatted("items.history-filter.name"));
            meta.setLore(M.getLoreList("items.history-filter.lore", "%filter%", filterText(filter)));
            item.setItemMeta(meta);
        }
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    Filter[] values = Filter.values();
                    int next = filter.ordinal() + (event.isRightClick() ? -1 : 1);
                    if (next < 0) next = values.length - 1;
                    if (next >= values.length) next = 0;
                    filter = values[next];
                    page = 0;
                    Sounds.click(event);
                    load();
                });
    }

    private InventoryButton nextPage() {
        ItemStack item = ConfigManager.layout.getItem("n");
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setItemName(M.getFormatted("items.next-page.name"));
            meta.setLore(M.getLoreList("items.next-page.lore",
                    "%page%", String.valueOf(page + 1),
                    "%pages%", String.valueOf(pages)));
            item.setItemMeta(meta);
        }
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if (page >= pages - 1) return;
                    page = event.isRightClick() ? pages - 1 : page + 1;
                    Sounds.click(event);
                    load();
                });
    }

    private InventoryButton previousPage() {
        ItemStack item = ConfigManager.layout.getItem("p");
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setItemName(M.getFormatted("items.previous-page.name"));
            meta.setLore(M.getLoreList("items.previous-page.lore",
                    "%page%", String.valueOf(page + 1),
                    "%pages%", String.valueOf(pages)));
            item.setItemMeta(meta);
        }
        return new InventoryButton()
                .creator(player -> item)
                .consumer(event -> {
                    if (page <= 0) return;
                    page = event.isRightClick() ? 0 : page - 1;
                    Sounds.click(event);
                    load();
                });
    }

    private String dateText(long time) {
        try {
            return new SimpleDateFormat(M.getFormatted("history-text.date-format")).format(new Date(time));
        } catch (Exception e) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time));
        }
    }

    private InventoryButton tradeButton(TradeHistory.Trade t) {
        ItemStack item;
        try {
            item = ItemStackConverter.decode(t.itemData());
        } catch (Exception e) {
            item = null;
        }
        if (item == null) item = new ItemStack(Material.BARRIER);
        item.setAmount(Math.max(1, Math.min(t.amount(), item.getMaxStackSize())));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
            List<String> extra = M.getLoreList(t.bought() ? "items.history-bought.lore" : "items.history-sold.lore",
                    "%player%", t.otherName() == null ? "?" : t.otherName(),
                    "%amount%", String.valueOf(t.amount()),
                    "%type%", M.getFormatted(t.bid() ? "sell-text.auction" : "sell-text.bin"),
                    "%tax%", SellOptions.formatTax(t.taxRate()),
                    "%date%", dateText(t.time()));
            double profit = SellOptions.profit(t.price(), t.taxRate());
            extra.replaceAll(line -> M.replace(line, t.price(), profit));
            lore.addAll(extra);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        final ItemStack icon = item;
        return new InventoryButton()
                .creator(player -> icon)
                .consumer(event -> {});
    }

}
