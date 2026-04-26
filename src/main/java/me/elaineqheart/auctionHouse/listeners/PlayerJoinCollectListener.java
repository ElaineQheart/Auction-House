package me.elaineqheart.auctionHouse.listeners;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.CollectSoldItemGUI;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.SettingManager;
import me.elaineqheart.auctionHouse.data.persistentStorage.local.configs.M;
import me.elaineqheart.auctionHouse.data.ram.AuctionHouseStorage;
import me.elaineqheart.auctionHouse.data.ram.ItemNote;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinCollectListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(!SettingManager.autoCollect) return;
        Bukkit.getScheduler().runTaskLater(AuctionHouse.getPlugin(), () -> {
            Player p = event.getPlayer();
            for(ItemNote note : AuctionHouseStorage.getMySortedDateCreated(p.getUniqueId())) sell(note, p);
        }, 1);

    }

    public static void sell(ItemNote note, Player p) {
        if (!note.isSold() && !(note.isBIDAuction() && note.hasBidHistory() && note.isExpired())) return;
        int amount = note.getItem().getAmount() - note.getPartiallySoldAmountLeft();
        String message = M.getFormatted("chat.sold-message.auto-collect", note.getSoldPrice(),
                "%buyer%", M.formatBuyer(note.getBuyerName(), note.getBuyerUUID()),
                "%item%", note.getItemName(),
                "%amount%", String.valueOf(amount));
        boolean success = CollectSoldItemGUI.collect(p, note.getNoteID(), amount, note.getSoldPrice());
        if (!success || !p.isOnline()) return;
        if (SettingManager.soldMessageEnabled) p.sendMessage(message);
    }

}
