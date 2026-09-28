package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.GUI.other.input.InputHandler;
import me.elaineqheart.auctionHouse.commands.ListingAnnouncer;
import me.elaineqheart.auctionHouse.data.StringUtils;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.Blacklist;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.data.ram.SellOptions;
import me.elaineqheart.auctionHouse.data.ram.SellSession;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

// The sell menu (hub -> "Sell an Item"). Instead of holding the item and typing /ah sell <price>:
//  - click an item in your own inventory to select it (it only leaves your inventory when you confirm)
//  - set the price, choose BIN / auction and the duration (each duration has its own tax, see config.yml: sell-durations)
// Layout, icons and texts are configurable: layout.yml (sell-layout, sell-*), messages.yml (items.sell-*, sell-text.*).
public class SellGUI extends InventoryGUI {

    private final Player p;
    private final SellSession s;

    public SellGUI(Player p) {
        super();
        this.p = p;
        this.s = SellSession.get(p);
        // defaults / validation of what is remembered from last time
        if (s.bid == null || (s.bid && !SettingManager.BIDAuctions) || (!s.bid && !SettingManager.BINAuctions)) {
            s.bid = !SettingManager.BINAuctions;
            s.tierIndex = -1;
        }
        List<SellOptions.Tier> tiers = SellOptions.getTiers(p, s.bid);
        if (s.tierIndex < 0 || s.tierIndex >= tiers.size()) s.tierIndex = SellOptions.defaultIndex(tiers, s.bid);
        validateItem();
    }

    // the item is still in the inventory (it can change between opening the menu and clicking)
    private boolean validateItem() {
        if (!s.hasItem()) return true;
        ItemStack inv = p.getInventory().getItem(s.slot);
        if (inv == null || inv.getType() == Material.AIR || !inv.isSimilar(s.item)) {
            s.clearItem();
            return false;
        }
        if (s.amount > inv.getAmount()) s.amount = inv.getAmount();
        return true;
    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null, ConfigManager.layout.sellLayout.size() * 9, M.getFormatted("inventory-titles.sell-menu"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(ConfigManager.layout.sellLayout);
        super.decorate(player);
    }

    private void update() {
        decorate(p);
    }

    // clicks in the player's own inventory select the item to sell; clicks in the menu use the buttons
    @Override
    public void onClick(InventoryClickEvent event) {
        if (event.getRawSlot() >= getInventory().getSize()) {
            event.setCancelled(true);
            selectItem(event);
            return;
        }
        super.onClick(event);
    }

    private void selectItem(InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (item == null || item.getType() == Material.AIR) return;
        if (Blacklist.isBlacklisted(item)) {
            p.sendMessage(M.getFormatted("command-feedback.item-blacklisted"));
            p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 0.7f, 0.1f);
            return;
        }
        Sounds.click(event);
        s.slot = event.getSlot();
        s.item = item.clone();
        s.item.setAmount(1);
        s.amount = item.getAmount();
        update();
    }

    private void fillOutPlaces(List<String> places) {
        for (int i = 0; i < places.size(); i++) {
            for (int j = 0; j < places.get(i).length(); j += 2) {
                int slot = i * 9 + j / 2;
                switch (places.get(i).charAt(j)) {
                    case '#' -> this.addButton(slot, fillerItem());
                    case 'D' -> this.addButton(slot, depositButton());
                    case 'P' -> this.addButton(slot, priceButton());
                    case 'T' -> this.addButton(slot, typeButton());
                    case 'L' -> this.addButton(slot, durationButton());
                    case 'K' -> this.addButton(slot, confirmButton());
                    case 'h' -> this.addButton(slot, backToHub());
                    case '.', ' ' -> {}
                }
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

    // ---------- helpers ----------

    // item from layout.yml (layoutKey) with the name and lore from messages.yml (messageKey)
    private ItemStack icon(String layoutKey, String messageKey, String... replacements) {
        ItemStack item = ConfigManager.layout.getItem(layoutKey);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;
        meta.setItemName(M.getFormatted(messageKey + ".name", replacements));
        meta.setLore(M.getLoreList(messageKey + ".lore", replacements));
        item.setItemMeta(meta);
        return item;
    }

    private String priceText() {
        return s.price > 0 ? StringUtils.formatPrice(s.price, false) : M.getFormatted("sell-text.not-set");
    }
    private String typeText() {
        return M.getFormatted(s.bid ? "sell-text.auction" : "sell-text.bin");
    }
    private SellOptions.Tier tier() {
        List<SellOptions.Tier> tiers = SellOptions.getTiers(p, s.bid);
        if (s.tierIndex < 0 || s.tierIndex >= tiers.size()) s.tierIndex = SellOptions.defaultIndex(tiers, s.bid);
        return tiers.get(s.tierIndex);
    }
    private String itemNameText() {
        return s.hasItem() ? StringUtils.getItemName(s.item, p.getLocation()) : "";
    }

    // ---------- buttons ----------

    private InventoryButton depositButton() {
        ItemStack icon;
        if (!s.hasItem()) {
            icon = icon("sell-deposit-empty", "items.sell-deposit-empty");
        } else {
            icon = s.item.clone();
            icon.setAmount(Math.min(s.amount, icon.getMaxStackSize()));
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                List<String> lore = meta.getLore() == null ? new ArrayList<>() : new ArrayList<>(meta.getLore());
                lore.addAll(M.getLoreList("items.sell-deposit.lore", "%amount%", String.valueOf(s.amount)));
                meta.setLore(lore);
                icon.setItemMeta(meta);
            }
        }
        final ItemStack finalIcon = icon;
        return new InventoryButton()
                .creator(player -> finalIcon)
                .consumer(event -> {
                    if (!s.hasItem()) return;
                    if (event.isRightClick()) {
                        Sounds.click(event);
                        openAmountInput();
                    } else {
                        Sounds.breakWood(event);
                        s.clearItem();
                        update();
                    }
                });
    }

    private void openAmountInput() {
        if (!s.hasItem()) return;
        InputHandler handler = new InputHandler() {
            public void execute(Player player, String typedText) {
                ItemStack inv = p.getInventory().getItem(s.slot);
                try {
                    int amount = Integer.parseInt(typedText.trim());
                    if (inv == null || amount < 1 || amount > inv.getAmount()) throw new RuntimeException();
                    s.amount = amount;
                } catch (Exception e) {
                    p.sendMessage(M.getFormatted("command-feedback.invalid-number7"));
                }
                AuctionHouse.getGuiManager().openGUI(new SellGUI(p), p);
            }
            public void onClose(Player player) {
                reopenLater();
            }
        };
        AuctionHouse.getInputManager().open(p, "inventory-titles.anvil-set-amount", handler);
    }

    private InventoryButton priceButton() {
        ItemStack icon = icon("sell-price", "items.sell-price", "%value%", priceText());
        return new InventoryButton()
                .creator(player -> icon)
                .consumer(event -> {
                    Sounds.click(event);
                    InputHandler handler = new InputHandler() {
                        public void execute(Player player, String typedText) {
                            double price = StringUtils.parsePositiveNumber(typedText);
                            if (price == -1) {
                                p.sendMessage(M.getFormatted("command-feedback.invalid-number"));
                            } else if (price == 0) {
                                p.sendMessage(M.getFormatted("command-feedback.invalid-number2"));
                            } else {
                                s.price = price;
                            }
                            AuctionHouse.getGuiManager().openGUI(new SellGUI(p), p);
                        }
                        public void onClose(Player player) {
                            reopenLater();
                        }
                    };
                    AuctionHouse.getInputManager().open(p, "inventory-titles.anvil-sell-price", handler);
                });
    }

    private InventoryButton typeButton() {
        boolean both = SettingManager.BINAuctions && SettingManager.BIDAuctions;
        ItemStack icon = icon(s.bid ? "bin-filter-auctions" : "bin-filter-bin",
                both ? "items.sell-type" : "items.sell-type-locked", "%type%", typeText());
        return new InventoryButton()
                .creator(player -> icon)
                .consumer(event -> {
                    if (!both) return;
                    Sounds.click(event);
                    s.bid = !s.bid;
                    s.tierIndex = -1; // pick the default duration of the new type
                    update();
                });
    }

    private InventoryButton durationButton() {
        SellOptions.Tier tier = tier();
        List<SellOptions.Tier> tiers = SellOptions.getTiers(p, s.bid);
        ItemStack icon = icon("sell-duration", tiers.size() > 1 ? "items.sell-duration" : "items.sell-duration-locked",
                "%time%", StringUtils.getTimeTrimmed(tier.seconds()),
                "%tax%", SellOptions.formatTax(tier.tax()));
        return new InventoryButton()
                .creator(player -> icon)
                .consumer(event -> {
                    if (tiers.size() <= 1) return;
                    Sounds.click(event);
                    int next = s.tierIndex + (event.isRightClick() ? -1 : 1);
                    if (next < 0) next = tiers.size() - 1;
                    if (next >= tiers.size()) next = 0;
                    s.tierIndex = next;
                    update();
                });
    }

    private InventoryButton confirmButton() {
        SellOptions.Tier tier = tier();
        boolean ready = s.hasItem() && s.price > 0;
        ItemStack icon;
        if (ready) {
            ItemStack item = ConfigManager.layout.getItem("sell-confirm");
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setItemName(M.getFormatted("items.sell-confirm.name"));
                List<String> lore = M.getLoreList("items.sell-confirm.lore",
                        "%item%", itemNameText(),
                        "%amount%", String.valueOf(s.amount),
                        "%type%", typeText(),
                        "%time%", StringUtils.getTimeTrimmed(tier.seconds()),
                        "%tax%", SellOptions.formatTax(tier.tax()));
                double profit = SellOptions.profit(s.price, tier.tax());
                lore.replaceAll(line -> M.replace(line, s.price, profit));
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            icon = item;
        } else {
            icon = icon("sell-confirm-disabled", "items.sell-confirm-disabled",
                    "%reason%", M.getFormatted(!s.hasItem() ? "sell-text.no-item" : "sell-text.no-price"));
        }
        return new InventoryButton()
                .creator(player -> icon)
                .consumer(event -> {
                    if (!ready) {
                        Sounds.villagerDeny(event);
                        return;
                    }
                    confirm(event);
                });
    }

    // ---------- listing ----------

    private void confirm(InventoryClickEvent event) {
        if (ConfigManager.bannedPlayers.checkIsBannedSendMessage(p)) return;
        if (AuctionHouseStorage.getNumberOfAuctions(p.getUniqueId()) >= ConfigManager.permissions.getAuctionSlots(p)) {
            p.sendMessage(M.getFormatted("command-feedback.reached-max-auctions",
                    "%limit%", String.valueOf(ConfigManager.permissions.getAuctionSlots(p))));
            Sounds.villagerDeny(event);
            return;
        }
        if (!validateItem()) {
            p.sendMessage(M.getFormatted("chat.sell-item-changed"));
            Sounds.villagerDeny(event);
            update();
            return;
        }
        boolean bid = s.bid;
        double price = s.price;
        // the config may have been reloaded since the menu was opened
        if (bid ? !SettingManager.BIDAuctions : !SettingManager.BINAuctions) {
            Sounds.villagerDeny(event);
            return;
        }
        if (!bid && price < SettingManager.minBINPrice) {
            p.sendMessage(M.getFormatted("command-feedback.min-bin", SettingManager.minBINPrice));
            Sounds.villagerDeny(event);
            return;
        } else if (bid && price < SettingManager.minBIDPrice) {
            p.sendMessage(M.getFormatted("command-feedback.min-bid", SettingManager.minBIDPrice));
            Sounds.villagerDeny(event);
            return;
        }
        if (!bid && SettingManager.maxBINPrice > -1 && price > SettingManager.maxBINPrice) {
            p.sendMessage(M.getFormatted("command-feedback.max-bin", SettingManager.maxBINPrice));
            Sounds.villagerDeny(event);
            return;
        } else if (bid && SettingManager.maxBIDPrice > -1 && price > SettingManager.maxBIDPrice) {
            p.sendMessage(M.getFormatted("command-feedback.max-bid", SettingManager.maxBIDPrice));
            Sounds.villagerDeny(event);
            return;
        }
        ItemStack inv = p.getInventory().getItem(s.slot);
        if (inv == null) return; // validateItem() made sure it exists
        if (Blacklist.isBlacklisted(inv)) { // the blacklist may have changed since the item was selected
            p.sendMessage(M.getFormatted("command-feedback.item-blacklisted"));
            Sounds.villagerDeny(event);
            s.clearItem();
            update();
            return;
        }
        SellOptions.Tier tier = tier();
        int amount = s.amount;
        ItemStack inputItem = inv.clone();
        inputItem.setAmount(amount);
        inv.setAmount(inv.getAmount() - amount);
        p.getInventory().setItem(s.slot, inv.getAmount() <= 0 ? null : inv);
        ItemNote note = ItemNoteStorage.createNote(p, inputItem, price, bid, tier.seconds(), tier.tax());
        ListingAnnouncer.afterListing(p, note, inputItem, price, bid, amount);
        s.reset();
        AuctionHouse.getGuiManager().openGUI(new SellGUI(p), p);
    }

    // reopen the menu after the anvil input was closed without typing anything
    private void reopenLater() {
        AuctionHouse.getScheduler().entitySpecificScheduler(p).runDelayed(() ->
                AuctionHouse.getGuiManager().openGUI(new SellGUI(p), p), null, 1);
    }

}
