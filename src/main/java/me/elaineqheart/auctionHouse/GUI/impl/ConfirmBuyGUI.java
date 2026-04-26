package me.elaineqheart.auctionHouse.GUI.impl;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.InventoryButton;
import me.elaineqheart.auctionHouse.GUI.InventoryGUI;
import me.elaineqheart.auctionHouse.GUI.other.Sounds;
import me.elaineqheart.auctionHouse.data.persistentStorage.ItemNoteStorage;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.data.ConfigManager;
import me.elaineqheart.auctionHouse.data.ram.AhConfiguration;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemManager;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import me.elaineqheart.auctionHouse.pluginDependencies.VaultHook;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfirmBuyGUI extends InventoryGUI{

    private final ItemNote note;
    private final ItemStack item;
    private final AhConfiguration c;
    private final double price;

    public ConfirmBuyGUI(ItemNote note, AhConfiguration configuration, ItemStack item) {
        super();
        this.note = note;
        this.item = item;
        c = configuration;
        price = note.getPrice() / note.getItem().getAmount() * item.getAmount();

    }

    @Override
    protected Inventory createInventory() {
        return Bukkit.createInventory(null,3*9, M.getFormatted("inventory-titles.auction-view"));
    }

    @Override
    public void decorate(Player player) {
        fillOutPlaces(new String[]{
                "# # # # # # # # #",
                "# # . # . # . # #",
                "# # # # # # # # #"
        },fillerItem());
        this.addButton(11, confirm());
        this.addButton(13, buyingItem());
        this.addButton(15, cancel());
        super.decorate(player);
    }

    private void fillOutPlaces(String[] places, InventoryButton fillerItem){
        for(int i = 0; i < places.length; i++){
            for(int j = 0; j < places[i].length(); j+=2){
                if(places[i].charAt(j)=='#') {
                    this.addButton(i*9+j/2, fillerItem);
                }
            }
        }
    }

    private InventoryButton fillerItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.fillerItem)
                .consumer(event -> {});
    }
    private InventoryButton buyingItem(){
        return new InventoryButton()
                .creator(player -> ItemManager.createBuyingItemDisplay(item.clone()))
                .consumer(event -> {});
    }
    private InventoryButton confirm(){
        return new InventoryButton()
                .creator(player -> ItemManager.createConfirm(price))
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    //check if inventory is full
                    if(p.getInventory().firstEmpty() == -1) {
                        p.sendMessage(M.getFormatted("chat.inventory-full"));
                        Sounds.villagerDeny(event);
                        return;
                    }

                    ItemNote test = AuctionHouseStorage.getNote(note.getNoteID());
                    if (test == null) {
                        p.sendMessage(M.getFormatted("chat.non-existent2"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if (!test.isOnAuction() || test.getCurrentAmount() < item.getAmount()) {
                        p.sendMessage(M.getFormatted("chat.already-sold2"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    if (test.isExpired()) {
                        p.sendMessage(M.getFormatted("chat.expired"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    Economy eco = VaultHook.getEconomy();
                    Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), () -> AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p));
                    if (eco.getBalance(p) < price) { //extra check to make sure that they have enough coins
                        p.sendMessage(M.getFormatted("chat.not-enough-money"));
                        Sounds.villagerDeny(event);
                        return;
                    }
                    //concurrent check
                    boolean claimed = ItemNoteStorage.setSoldIfOnAuction(note, p, item.getAmount(), price);
                    if (!claimed) {
                        p.sendMessage(M.getFormatted("chat.already-sold"));
                        Sounds.villagerDeny(event);
                        return;
                    }

                    eco.withdrawPlayer(p, price);
                    Sounds.experience(event);
                    p.getInventory().addItem(item);

                    p.sendMessage(M.getFormatted("chat.purchase-auction",
                            "%seller%", M.formatSeller(note.getPlayerName(), note.getPlayerUUID()),
                            "%item%", note.getItemName()));
                    Player seller = Bukkit.getPlayer(note.getPlayerUUID());
                    if (SettingManager.soldMessageEnabled && seller != null && Bukkit.getOnlinePlayers().contains(seller)) {
                        String itemName = note.getItemName();
                        String amount = String.valueOf(item.getAmount());
                        String buyer = M.formatBuyer(p.getDisplayName(), p.getUniqueId());
                        if(SettingManager.autoCollect) {
                            seller.sendMessage(M.getFormatted("chat.sold-message.auto-collect", price,
                                    "%buyer%", buyer,
                                    "%item%", itemName,
                                    "%amount%", amount));
                        } else {
                            TextComponent component = new TextComponent(M.getFormatted("chat.sold-message.prefix", price,
                                    "%buyer%", buyer,
                                    "%item%", itemName,
                                    "%amount%", amount));
                            TextComponent click = new TextComponent(M.getFormatted("chat.sold-message.interaction"));
                            click.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ah view " + note.getNoteID().toString()));
                            seller.spigot().sendMessage(component, click);
                        }
                    }
                    if (SettingManager.autoCollect && Bukkit.getPlayer(note.getPlayerUUID()) != null) {
                        Bukkit.getScheduler().runTask(AuctionHouse.getPlugin(), () -> CollectSoldItemGUI.collect
                                (Bukkit.getOfflinePlayer(note.getPlayerUUID()), note.getNoteID(), item.getAmount(), note.getSoldPrice())
                        );
                    }
                    ConfigManager.transactionLogger.logTransaction(
                            p.getName(),
                            note.getPlayerName(),
                            note.getItemName(),
                            price,
                            item.getAmount(),
                            !note.isBIDAuction());
                });
    }
    private InventoryButton cancel(){
        return new InventoryButton()
                .creator(player -> ItemManager.cancel)
                .consumer(event -> {
                    Player p = (Player) event.getWhoClicked();
                    Sounds.click(event);
                    AuctionHouse.getGuiManager().openGUI(new AuctionHouseGUI(c), p);
                });
    }

}
