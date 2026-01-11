package me.elaineqheart.auctionHouse.listeners;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.CollectSoldItemGUI;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.Messages;
import me.elaineqheart.auctionHouse.data.persistentStorage.yml.SettingManager;
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
            for(ItemNote note : AuctionHouseStorage.getMySortedDateCreated(p)) {
                sell(note, p);
            }
        }, 1);

    }

    public static void sell(ItemNote note, Player p) {
        if (!note.isSold() && !(!note.isBINAuction() && note.hasBidHistory() && note.isExpired())) return;
        int amount = note.getItem().getAmount() - note.getPartiallySoldAmountLeft();
        if(CollectSoldItemGUI.collect(p, note.getNoteID(), amount, note.getSoldPrice())
            && SettingManager.soldMessageEnabled) p.sendMessage(Messages.getFormatted("chat.sold-message.auto-collect", note.getSoldPrice(),
                    "%player%", note.getBuyerName(),
                    "%item%", note.getItemName(),
                    "%amount%", String.valueOf(amount)));
    }

}
