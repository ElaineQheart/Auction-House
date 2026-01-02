package me.elaineqheart.auctionHouse.listeners;

import me.elaineqheart.auctionHouse.AuctionHouse;
import me.elaineqheart.auctionHouse.GUI.impl.CollectSoldItemGUI;
import me.elaineqheart.auctionHouse.data.StringUtils;
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
            for(ItemNote note : AuctionHouseStorage.getMySortedDateCreated(p.getUniqueId())) {
                if (!note.isSold()) continue;
                int amount = note.getItem().getAmount() - note.getPartiallySoldAmountLeft();
                CollectSoldItemGUI.collect(p, note.getNoteID().toString(), amount, note.getSoldPrice());
                if(SettingManager.soldMessageEnabled) p.sendMessage(Messages.getFormatted("chat.sold-message.auto-collect",
                        "%player%", note.getBuyerName(),
                        "%item%", note.getItemName(),
                        "%price%", StringUtils.formatPrice(note.getSoldPrice()),
                        "%amount%", String.valueOf(amount)));
            }
        }, 1);

    }

}
